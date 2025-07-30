package org.pawlowski.karoo_cgm

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import io.hammerhead.karooext.KarooSystemService
import org.pawlowski.karoo_cgm.datastore.UserPreferencesRepository
import org.pawlowski.karoo_cgm.navigation.NavGraph
import org.pawlowski.karoo_cgm.ui.theme.KarooCGMTheme
import javax.inject.Inject

@HiltAndroidApp
class CGMApplication : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    @Inject
    lateinit var karooSystem: KarooSystemService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        karooSystem.connect()
        enableEdgeToEdge()
        setContent {
            KarooCGMTheme {
                NavGraph(userPreferencesRepository = userPreferencesRepository)
            }
        }
    }

    override fun onDestroy() {
        karooSystem.disconnect()
        super.onDestroy()
    }
}
