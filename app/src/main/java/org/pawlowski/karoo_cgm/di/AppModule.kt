package org.pawlowski.karoo_cgm.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.hammerhead.karooext.KarooSystemService
import org.pawlowski.karoo_cgm.data.LibreLinkUpClient
import org.pawlowski.karoo_cgm.datastore.UserPreferencesRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(@ApplicationContext context: Context): UserPreferencesRepository {
        return UserPreferencesRepository(context)
    }

    @Provides
    @Singleton
    fun provideKarooSystemService(@ApplicationContext context: Context): KarooSystemService {
        return KarooSystemService(context)
    }

    @Provides
    @Singleton
    fun provideLibreLinkUpClient(karooSystemService: KarooSystemService): LibreLinkUpClient {
        return LibreLinkUpClient(karooSystemService)
    }
}