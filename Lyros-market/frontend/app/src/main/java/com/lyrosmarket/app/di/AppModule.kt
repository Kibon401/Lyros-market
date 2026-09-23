package com.lyrosmarket.app.di

import android.content.Context
import com.lyrosmarket.app.core.SessionManager
import com.lyrosmarket.app.data.remote.AuthApiService
import com.lyrosmarket.app.data.remote.ProductApiService
import com.lyrosmarket.app.data.repository.AuthRepositoryImpl
import com.lyrosmarket.app.data.repository.ProductRepositoryImpl
import com.lyrosmarket.app.data.repository.CartRepositoryImpl
import com.lyrosmarket.app.domain.repository.AuthRepository
import com.lyrosmarket.app.domain.repository.ProductRepository
import com.lyrosmarket.app.domain.repository.CartRepository
import com.lyrosmarket.app.domain.repository.DeliveryRepository
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

private const val BASE_URL = "https://lyros-market.onrender.com/"
    
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
    fun providePaymentApiService(client: HttpClient): com.lyrosmarket.app.data.remote.PaymentApiService {
        return com.lyrosmarket.app.data.remote.PaymentApiService(client, BASE_URL)
    }

    @Provides
    @Singleton
    fun providePaymentRepository(api: com.lyrosmarket.app.data.remote.PaymentApiService): com.lyrosmarket.app.domain.repository.PaymentRepository {
        return com.lyrosmarket.app.data.repository.PaymentRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideOrderApiService(client: HttpClient): com.lyrosmarket.app.data.remote.OrderApiService {
        return com.lyrosmarket.app.data.remote.OrderApiService(client, BASE_URL)
    }

    @Provides
    @Singleton
    fun provideOrderRepository(api: com.lyrosmarket.app.data.remote.OrderApiService): com.lyrosmarket.app.domain.repository.OrderRepository {
        return com.lyrosmarket.app.data.repository.OrderRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideCartApiService(client: HttpClient): com.lyrosmarket.app.data.remote.CartApiService {
        return com.lyrosmarket.app.data.remote.CartApiService(client, BASE_URL)
    }

    @Provides
    @Singleton
    fun provideCartRepository(api: com.lyrosmarket.app.data.remote.CartApiService): CartRepository {
        return CartRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideDeliveryApiService(client: HttpClient): com.lyrosmarket.app.data.remote.DeliveryApiService {
        return com.lyrosmarket.app.data.remote.DeliveryApiService(client, BASE_URL)
    }

    @Provides
    @Singleton
    fun provideDeliveryRepository(
        api: com.lyrosmarket.app.data.remote.DeliveryApiService,
        sessionManager: SessionManager
    ): DeliveryRepository {
        return com.lyrosmarket.app.data.repository.DeliveryRepositoryImpl(api, sessionManager)
    }

    @Provides
    @Singleton
    fun provideAdminApiService(client: HttpClient): com.lyrosmarket.app.data.remote.AdminApiService {
        return com.lyrosmarket.app.data.remote.AdminApiService(client, BASE_URL)
    }

    @Provides
    @Singleton
    fun provideAdminRepository(api: com.lyrosmarket.app.data.remote.AdminApiService): com.lyrosmarket.app.domain.repository.AdminRepository {
        return com.lyrosmarket.app.data.repository.AdminRepositoryImpl(api)
    }
}
