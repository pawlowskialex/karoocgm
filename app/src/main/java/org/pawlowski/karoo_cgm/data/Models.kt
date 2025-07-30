package org.pawlowski.karoo_cgm.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Patient(
    val id: String,
    val patientId: String,
    val country: String,
    val status: Int,
    val firstName: String,
    val lastName: String,
    val targetLow: Int,
    val targetHigh: Int,
    val uom: Int,
    val sensor: Sensor,
    val alarmRules: AlarmRules,
    val glucoseMeasurement: GlucoseMeasurementWithTrend,
    val glucoseItem: GlucoseMeasurement,
    val glucoseAlarm: GlucoseAlarm?,
    val patientDevice: PatientDevice,
    val created: Long
)

enum class Trend(val indicator: String) {
    @SerialName("1")
    DOWN_FAST("↓"),

    @SerialName("2")
    DOWN_SLOW("↘"),

    @SerialName("3")
    STABLE("→"),

    @SerialName("4")
    UP_SLOW("↗"),

    @SerialName("5")
    UP_FAST("↑")
}

@Serializable
data class GlucoseMeasurement(
    @SerialName("FactoryTimestamp")
    val factoryTimestamp: String,
    @SerialName("Timestamp")
    val timestamp: String,
    @SerialName("Type")
    val type: Int = 0,
    @SerialName("ValueInMgPerDl")
    val valueInMgPerDl: Double = 0.0,
    @SerialName("MeasurementColor")
    val measurementColor: Int = 0,
    @SerialName("GlucoseUnits")
    val glucoseUnits: Int = 0,
    @SerialName("Value")
    val value: Double = 0.0,
    @SerialName("isHigh")
    val isHigh: Boolean,
    @SerialName("isLow")
    val isLow: Boolean
)

@Serializable
data class GlucoseMeasurementWithTrend(
    @SerialName("FactoryTimestamp")
    val factoryTimestamp: String,
    @SerialName("Timestamp")
    val timestamp: String,
    @SerialName("Type")
    val type: Int = 0,
    @SerialName("ValueInMgPerDl")
    val valueInMgPerDl: Double = 0.0,
    @SerialName("MeasurementColor")
    val measurementColor: Int = 0,
    @SerialName("GlucoseUnits")
    val glucoseUnits: Int = 0,
    @SerialName("Value")
    val value: Double = 0.0,
    @SerialName("isHigh")
    val isHigh: Boolean,
    @SerialName("isLow")
    val isLow: Boolean,
    @SerialName("TrendArrow")
    val trend: Trend = Trend.STABLE,
    @SerialName("TrendMessage")
    val trendMessage: String? = null
)

@Serializable
data class Sensor(
    val deviceId: String,
    val sn: String,
    val a: Long,
    val w: Int,
    val pt: Int,
    val s: Boolean,
    val lj: Boolean
)

@Serializable
data class AlarmRules(
    val c: Boolean,
    val h: HighAlarmRule,
    val f: FastAlarmRule,
    val l: LowAlarmRule,
    val nd: NoDataAlarmRule,
    val p: Int,
    val r: Int,
    val std: Map<String, String> = emptyMap()
)

@Serializable
data class HighAlarmRule(
    val on: Boolean,
    val th: Int,
    val thmm: Double,
    val d: Int,
    val f: Double
)

@Serializable
data class FastAlarmRule(
    val on: Boolean,
    val th: Int,
    val thmm: Double,
    val d: Int,
    val tl: Int,
    val tlmm: Double
)

@Serializable
data class LowAlarmRule(
    val on: Boolean,
    val th: Int,
    val thmm: Double,
    val d: Int,
    val tl: Int,
    val tlmm: Double
)

@Serializable
data class NoDataAlarmRule(
    val i: Int,
    val r: Int,
    val l: Int
)

@Serializable
data class GlucoseAlarm(
    // Define fields if/when you encounter non-null glucose alarms
    val placeholder: String? = null
)

@Serializable
data class PatientDevice(
    val did: String,
    val dtid: Int,
    val v: String,
    val l: Boolean,
    val ll: Int,
    val h: Boolean,
    val hl: Int,
    val u: Long,
    val fixedLowAlarmValues: FixedLowAlarmValues,
    val alarms: Boolean,
    val fixedLowThreshold: Int
)

@Serializable
data class FixedLowAlarmValues(
    val mgdl: Int,
    val mmoll: Double
)

@Serializable
data class LoginArgs(
    val email: String,
    val password: String
)

@Serializable
data class AuthTicket(
    val token: String,
    val expires: Long,
    val duration: Long
)

@Serializable
data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val country: String,
    val uiLanguage: String,
    val communicationLanguage: String,
    val accountType: String,
    val uom: String,
    val dateFormat: String,
    val timeFormat: String
)

@Serializable
data class LoginData(
    val user: User,
    val authTicket: AuthTicket,
    val invitations: List<String>? = emptyList()
)

@Serializable
data class LoginResponse(
    val status: Int,
    val data: LoginData? = null,
    val error: ErrorMessage? = null
)

@Serializable
data class ErrorMessage(
    val message: String
)

@Serializable
data class LoginRedirectData(
    val redirect: Boolean,
    val region: String
)

@Serializable
data class LoginRedirectResponse(
    val status: Int,
    val data: LoginRedirectData
)

@Serializable
data class Connection(
    val id: String,
    val patientId: String,
    val country: String,
    val status: Int,
    val firstName: String,
    val lastName: String,
    val targetLow: Int,
    val targetHigh: Int,
    val uom: Int,
    val sensor: Sensor,
    val alarmRules: AlarmRules,
    val glucoseMeasurement: GlucoseMeasurementWithTrend,
    val glucoseItem: GlucoseMeasurementWithTrend,
    val glucoseAlarm: GlucoseAlarm?,
    val patientDevice: PatientDevice,
    val created: Long
)

@Serializable
data class ActiveSensor(
    val sensor: Sensor,
    val device: PatientDevice
)

@Serializable
data class GraphDataResponse(
    val connection: Connection,
    val activeSensors: List<ActiveSensor>,
    val graphData: List<GlucoseMeasurement>
)

@Serializable
data class GraphApiResponse(
    val status: Int,
    val data: GraphDataResponse,
    val ticket: AuthTicket
)

@Serializable
data class PatientsApiResponse(
    val status: Int,
    val data: List<Patient>,
    val ticket: AuthTicket
)

@Serializable
data class LogbookResponse(
    val status: Int,
    val data: List<GlucoseMeasurement>,
    val ticket: AuthTicket
)