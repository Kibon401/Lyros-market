package com.example.lyrosmarket.di

import android.content.Context
import com.example.lyrosmarket.core.SessionManager
import com.example.lyrosmarket.data.remote.AuthApiService
import com.example.lyrosmarket.data.remote.ProductApiService
import com.example.lyrosmarket.data.repository.AuthRepositoryImpl
import com.example.lyrosmarket.data.repository.ProductRepositoryImpl
import com.example.lyrosmarket.data.repository.CartRepositoryImpl
import com.example.lyrosmarket.domain.repository.AuthRepository
import com.example.lyrosmarket.domain.repository.ProductRepository
import com.example.lyrosmarket.domain.repository.CartRepository
import com.example.lyrosmarket.domain.repository.DeliveryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import jakarta.inject.Singleton
import kotlinx.serialization.json.Json

private const val BASE_URL = "https://kijani-market-server.onrender.com/"
    
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }

    @Provides
    @Singleton
    fun provideHttpClient(sessionManager: SessionManager): HttpClient {
        return HttpClient(Android) {
            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        android.util.Log.d("KtorClient", message)
                    }
                }
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                })
            }
            defaultRequest {
                url(BASE_URL)
                sessionManager.getToken()?.let {
                    header(HttpHeaders.Authorization, "Bearer $it")
                }
            }
        }
    }

    @Provides
    @Singleton
    fun provideAuthApiService(client: HttpClient): AuthApiService {
        return AuthApiService(client, BASE_URL)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(api: AuthApiService, sessionManager: SessionManager): AuthRepository {
        return AuthRepositoryImpl(api, sessionManager)
    }

    @Provides
    @Singleton
    fun provideProductApiService(client: HttpClient): ProductApiService {
        return ProductApiService(client, BASE_URL)
    }

    @Provides
    @Singleton
    fun provideProductRepository(api: ProductApiService): ProductRepository {
        return ProductRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun providePaymentApiService(client: HttpClient): com.example.lyrosmarket.data.remote.PaymentApiService {
        return com.example.lyrosmarket.data.remote.PaymentApiService(client, BASE_URL)
    }

    @Provides
    @Singleton
    fun providePaymentRepository(api: com.example.lyrosmarket.data.remote.PaymentApiService): com.example.lyrosmarket.domain.repository.PaymentRepository {
        return com.example.lyrosmarket.data.repository.PaymentRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideOrderApiService(client: HttpClient): com.example.lyrosmarket.data.remote.OrderApiService {
        return com.example.lyrosmarket.data.remote.OrderApiService(client, BASE_URL)
    }

    @Provides
    @Singleton
    fun provideOrderRepository(api: com.example.lyrosmarket.data.remote.OrderApiService): com.example.lyrosmarket.domain.repository.OrderRepository {
        return com.example.lyrosmarket.data.repository.OrderRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideCartApiService(client: HttpClient): com.example.lyrosmarket.data.remote.CartApiService {
        return com.example.lyrosmarket.data.remote.CartApiService(client, BASE_URL)
    }

    @Provides
    @Singleton
    fun provideCartRepository(api: com.example.lyrosmarket.data.remote.CartApiService): CartRepository {
        return CartRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideDeliveryApiService(client: HttpClient): com.example.lyrosmarket.data.remote.DeliveryApiService {
        return com.example.lyrosmarket.data.remote.DeliveryApiService(client, BASE_URL)
    }

    @Provides
    @Singleton
    fun provideDeliveryRepository(
        api: com.example.lyrosmarket.data.remote.DeliveryApiService,
        sessionManager: SessionManager
    ): DeliveryRepository {
        return com.example.lyrosmarket.data.repository.DeliveryRepositoryImpl(api, sessionManager)
    }

    @Provides
    @Singleton
    fun provideAdminApiService(client: HttpClient): com.example.lyrosmarket.data.remote.AdminApiService {
        return com.example.lyrosmarket.data.remote.AdminApiService(client, BASE_URL)
    }

    @Provides
    @Singleton
    fun provideAdminRepository(api: com.example.lyrosmarket.data.remote.AdminApiService): com.example.lyrosmarket.domain.repository.AdminRepository {
        return com.example.lyrosmarket.data.repository.AdminRepositoryImpl(api)
    }
}
