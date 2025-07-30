package org.pawlowski.karoo_cgm.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer.LineProvider.Companion.series
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.pawlowski.karoo_cgm.data.GlucoseMeasurement
import org.pawlowski.karoo_cgm.data.GlucoseMeasurementWithTrend
import org.pawlowski.karoo_cgm.data.LibreLinkUpClient
import org.pawlowski.karoo_cgm.data.LoginArgs
import org.pawlowski.karoo_cgm.datastore.UserPreferencesRepository
import javax.inject.Inject

data class MainUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val glucoseHistory: List<GlucoseMeasurement> = emptyList(),
    val latestGlucose: GlucoseMeasurementWithTrend? = null,
    val highThreshold: Double = 180.0,
    val lowThreshold: Double = 70.0
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val libreLinkUpClient: LibreLinkUpClient,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    val chartProducer = CartesianChartModelProducer()

    init {
        viewModelScope.launch {
            userPreferencesRepository.userPreferencesFlow.firstOrNull()?.let { prefs ->
                if (prefs.authToken != null && prefs.accountId != null && prefs.tokenExpiration != null && prefs.patientId != null) {
                    if (System.currentTimeMillis() / 1000 < prefs.tokenExpiration) {
                        libreLinkUpClient.setToken(prefs.authToken, prefs.accountId)
                        fetchData(prefs.patientId)
                    } else {
                        // Token expired, re-authenticate
                        if (prefs.email != null && prefs.password != null) {
                            try {
                                val response = libreLinkUpClient.authenticate(
                                    LoginArgs(
                                        prefs.email,
                                        prefs.password
                                    )
                                )
                                if (response.data?.authTicket?.token != null) {
                                    userPreferencesRepository.updateAuthToken(
                                        response.data.authTicket.token,
                                        response.data.authTicket.expires,
                                        response.data.user.id
                                    )
                                    fetchData(prefs.patientId)
                                } else {
                                    _uiState.update { it.copy(errorMessage = "Failed to refresh token") }
                                }
                            } catch (e: Exception) {
                                _uiState.update { it.copy(errorMessage = e.message) }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun fetchData(patientId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val history = libreLinkUpClient.getGraph(patientId)
                _uiState.update {
                    chartProducer.runTransaction {
                        lineSeries {
                            series(history.graphData.map { it.valueInMgPerDl })
                        }
                    }
                    it.copy(
                        isLoading = false,
                        glucoseHistory = history.graphData,
                        latestGlucose = history.connection.glucoseMeasurement,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }
}
