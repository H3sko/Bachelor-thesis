package bachelorThesis.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import bachelorThesis.app.common.DEFAULT_URL
import bachelorThesis.app.data.remote.BackendApi
import bachelorThesis.app.data.repository.DataStoreRepositoryImpl
import bachelorThesis.app.data.repository.RepositoryImpl
import bachelorThesis.app.domain.repository.DataStoreRepository
import bachelorThesis.app.domain.repository.Repository
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideApi(): BackendApi {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        return Retrofit.Builder()
            .baseUrl(DEFAULT_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(BackendApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRepository(api: BackendApi): Repository {
        return RepositoryImpl(api)
    }

    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        preferencesDataStore(name = "data-store").getValue(context, String::javaClass)

    @Provides
    @Singleton
    fun provideDataStorage(dataStore: DataStore<Preferences>): DataStoreRepository = DataStoreRepositoryImpl(dataStore)
}