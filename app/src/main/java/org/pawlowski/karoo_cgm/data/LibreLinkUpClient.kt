package org.pawlowski.karoo_cgm.data

import de.jonasfranz.ktor.client.karoo.Karoo
import io.hammerhead.karooext.KarooSystemService
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.security.MessageDigest
import kotlin.time.Duration.Companion.seconds

class LibreLinkUpClient(
    karooService: KarooSystemService,
    private val apiUrl: String = "https://api-us.libreview.io"
) {
    private val client = HttpClient(Karoo(karooService)) {
        engine { requestTimeout = 30.seconds }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private var token: String? = null
    private var accountIdHash: String? = null

    private fun hashAccountId(accountId: String): String {
        val bytes = accountId.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    fun setToken(token: String, accountId: String) {
        this.token = token
        this.accountIdHash = hashAccountId(accountId)
    }

    suspend fun authenticate(loginArgs: LoginArgs): LoginResponse {
        val response: LoginResponse = client.post("$apiUrl/llu/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(loginArgs)
            headers {
                append("accept-encoding", "gzip")
                append("cache-control", "no-cache")
                append("connection", "Keep-Alive")
                append("content-type", "application/json")
                append("product", "llu.android")
                append("version", "4.12.0")
            }
        }.body()

        if (response.data?.authTicket?.token != null) {
            this.token = response.data.authTicket.token
            this.accountIdHash = hashAccountId(response.data.user.id)
        }

        return response
    }

    private suspend inline fun <reified T> get(url: String): T {
        return client.get(url) {
            headers {
                append("authorization", "Bearer $token")
                append("account-id", "$accountIdHash")
                append("accept-encoding", "gzip")
                append("cache-control", "no-cache")
                append("connection", "Keep-Alive")
                append("product", "llu.android")
                append("version", "4.12.0")
            }
        }.body()
    }

    suspend fun getPatients(): List<Patient> {
        val response: PatientsApiResponse = get("$apiUrl/llu/connections")
        return response.data
    }

    suspend fun getGraph(patientId: String): GraphDataResponse {
        try {
            val response: GraphApiResponse = get("$apiUrl/llu/connections/$patientId/graph")
            return response.data
        } catch (e: Exception) {
            println("Error getting graph data: $e")
            throw e
        }
    }

    suspend fun getLogbook(patientId: String): List<GlucoseMeasurement> {
        val response: LogbookResponse = get("$apiUrl/llu/connections/$patientId/logbook")
        return response.data
    }
}
