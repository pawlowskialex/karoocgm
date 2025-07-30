package org.pawlowski.karoo_cgm

import android.content.Context
import dagger.hilt.android.AndroidEntryPoint
import io.hammerhead.karooext.KarooSystemService
import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.extension.KarooExtension
import io.hammerhead.karooext.internal.Emitter
import io.hammerhead.karooext.models.DataPoint
import io.hammerhead.karooext.models.DataType
import io.hammerhead.karooext.models.StreamState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import org.pawlowski.karoo_cgm.data.LibreLinkUpClient
import org.pawlowski.karoo_cgm.datastore.UserPreferencesRepository
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class CGMExtension : KarooExtension("karoo-cgm", "1") {
    @Inject
    lateinit var karooSystem: KarooSystemService

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    @Inject
    lateinit var client: LibreLinkUpClient

    override val types by lazy {
        listOf(GlucoseDataType(userPreferencesRepository, client))
    }

    override fun onCreate() {
        super.onCreate()
        karooSystem.connect {
            println("CGM: Connected to Karoo System")
        }
    }

    override fun onDestroy() {
        karooSystem.disconnect()
        super.onDestroy()
    }
}

class GlucoseDataType(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val client: LibreLinkUpClient
) : DataTypeImpl("karoo-cgm", "glucose") {
    override fun startStream(emitter: Emitter<StreamState>) {
        val updateJob = CoroutineScope(Dispatchers.IO).launch {
            userPreferencesRepository.userPreferencesFlow.collect {
                // every minute query the latest cgm data
                while (true) {
                    println("cgm querying data")
                    if (it.authToken != null && it.accountId != null && it.patientId != null) {
                        client.setToken(it.authToken, it.accountId)
                        try {
                            val allPatientData = client.getPatients()
                            val targetPatientId = it.patientId
                            val patient = allPatientData.find { it.patientId == targetPatientId }
                            if (patient == null) {
                                emitter.onNext(StreamState.NotAvailable)
                                println("cgm patient not found")
                                return@collect
                            }
                            emitter.onNext(
                                StreamState.Streaming(
                                    DataPoint(
                                        dataTypeId,
                                        values = mapOf(DataType.Field.SINGLE to patient.glucoseMeasurement.valueInMgPerDl),
                                    )
                                )
                            )
                            println("cgm data: ${patient.glucoseMeasurement.valueInMgPerDl}")
                        } catch (e: Exception) {
                            println("cgm data not available ${e.message}")
                        }

                    } else {
                        emitter.onNext(StreamState.NotAvailable)
                        println("cgm user preferences not available")
                    }
                    println("cgm waiting")
                    delay(1.minutes)
                    println("cgm woke up")
                }

            }
        }
        emitter.setCancellable {
            updateJob.cancel()
        }
    }
}