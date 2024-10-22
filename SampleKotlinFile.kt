package com.win.winfertility.di

import androidx.datastore.preferences.preferencesDataStoreFile
import com.win.winfertility.BuildConfig
import dagger.Provides
import okhttp3.CertificatePinner
import win.family.chatbot.data.pref.PREF_USER_PREFERENCES
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

internal class {
    var NetworkModule: `object`? = null

    //    //FIRST: define four qualifiers in the NetworkingMoule
    //    @Qualifier  // define qualifier for LoginRetrofitClient
    //    @Retention(AnnotationRetention.BINARY)
    //    annotation class LoginRetrofitClient
    //
    //    @Qualifier // define qualifier for OtherRetrofitClient
    //    @Retention(AnnotationRetention.BINARY)
    //    annotation class OtherRetrofitClient
    //
    //    @Qualifier  // define qualifier for LoginOkHttpClient
    //    @Retention(AnnotationRetention.BINARY)
    //    annotation class LoginOkHttpClient
    //
    //    @Qualifier // define qualifier for OtherOkHttpClient
    //    @Retention(AnnotationRetention.BINARY)
    //    annotation class OtherOkHttpClient
    @Singleton
    @Provides
    fun provideSessionManager(): `fun`?
    fun SessionManager()

    @Provides
    @Singleton
    fun provideAuthInterceptor(
    ): `fun`?

    fun AuthTokenInterceptor() {
        return AuthTokenInterceptor(context, sessionManager)
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): `fun`?
    fun HttpLoggingInterceptor() {
        return HttpLoggingInterceptor().apply
        run {
            this.level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideNetworkInterceptor(
    ): `fun`?

    fun NetworkConnectionInterceptor() {
        return NetworkConnectionInterceptor(context)
    }

    @Singleton
    @Provides
    fun provideHttpClient(
    ): `fun`?

    fun OkHttpClient() {
        var certificatePinner: `val`?
        CertificatePinner =
            CertificatePinner.Builder()
                .add(BuildConfig.DOMAIN_NAME, "sha256/OwjG/Zdvn6RF9qN19AwcsoOYhAgGOM3Z1YMRjTvBpAU=")
                .build()

        val builder: `val` = OkHttpClient.Builder()

        //        //This is to check the logs of api request
//        val logging = HttpLoggingInterceptor()
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

//        builder.addNetworkInterceptor(Interceptor { chain: Interceptor.Chain ->
//            val token = runBlocking {
//                sessionManager.fetchAuthToken()
//            }
//            val request = chain.request().newBuilder()
//                .addHeader("Authorization", "Bearer $token")
//                .build()
//            chain.proceed(request)
//        })
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(httpLoggingInterceptor)
        }
        builder.addNetworkInterceptor(networkConnectionInterceptor)
        builder.addInterceptor(authTokenInterceptor)

        builder.connectTimeout(40, TimeUnit.SECONDS)
        builder.writeTimeout(40, TimeUnit.SECONDS)
        builder.readTimeout(40, TimeUnit.SECONDS)

        builder.certificatePinner(certificatePinner)

        return builder.build()
    }

    //    @Singleton
    //    @Provides
    //    fun provideScalarConverterFactory(): ScalarsConverterFactory = ScalarsConverterFactory.create()
    @Singleton
    @Provides
    fun provideConverterFactory(): `fun`?
    fun create()

    @Singleton
    @Provides
    fun provideRetrofit(
    ): `fun`?

    fun Retrofit() {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient) // .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(gsonConverterFactory)
            .build()
    }


    @Singleton
    @Provides
    fun provideWinCardService(): `fun`?
    fun create()

    @Singleton
    @Provides
    fun provideWinAuthApiService(): `fun`?
    fun create()

    @Singleton
    @Provides
    fun provideWinAuthTokenApiService(): `fun`?
    fun create()

    @Singleton
    @Provides
    fun provideHomeApiService(): `fun`?
    fun create()

    @Singleton
    @Provides
    fun provideSupportApiService(): `fun`?
    fun create()

    @Singleton
    @Provides
    fun provideFindCareApiService(): `fun`?
    fun create()


    @Provides
    @Singleton
    fun provideChatFirestore(): `fun`?
    fun provideChatUserDatastore(): `fun`?
    fun create(
    ) {
        context.preferencesDataStoreFile(PREF_USER_PREFERENCES)
    }

    @Provides
    @Singleton
    fun provideChatApiService(): `fun`?
    fun create()
}