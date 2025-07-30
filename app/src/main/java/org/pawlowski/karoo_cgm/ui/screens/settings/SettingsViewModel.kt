package org.pawlowski.karoo_cgm.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.pawlowski.karoo_cgm.data.LibreLinkUpClient
import org.pawlowski.karoo_cgm.data.LoginArgs
import org.pawlowski.karoo_cgm.data.Patient
import org.pawlowski.karoo_cgm.datastore.UserPreferencesRepository
import javax.inject.Inject

data class SettingsUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val patients: List<Patient> = emptyList(),
    val selectedPatientId: String? = null,
    val loginSuccessful: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val libreLinkUpClient: LibreLinkUpClient,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferencesRepository.userPreferencesFlow.collect { prefs ->
                _uiState.update {
                    it.copy(
                        email = prefs.email ?: "",
                        password = prefs.password ?: "",
                        selectedPatientId = prefs.patientId
                    )
                }
            }
        }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun login() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = libreLinkUpClient.authenticate(
                    LoginArgs(
                        _uiState.value.email,
                        _uiState.value.password
                    )
                )
                if (response.data?.authTicket?.token != null) {
                    userPreferencesRepository.updateEmail(_uiState.value.email)
                    userPreferencesRepository.updatePassword(_uiState.value.password)
                    userPreferencesRepository.updateAuthToken(
                        response.data.authTicket.token,
                        response.data.authTicket.expires,
                        response.data.user.id
                    )
                    val patients = libreLinkUpClient.getPatients()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            patients = patients,
                            loginSuccessful = true
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = response.error?.message ?: "Unknown error"
                        )
                    }
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

    fun selectPatient(patientId: String) {
        viewModelScope.launch {
            userPreferencesRepository.updatePatientId(patientId)
            _uiState.update { it.copy(selectedPatientId = patientId) }
        }
    }
}
