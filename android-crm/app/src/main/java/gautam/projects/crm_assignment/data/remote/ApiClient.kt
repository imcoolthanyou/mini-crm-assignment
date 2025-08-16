package gautam.projects.crm_assignment.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import gautam.projects.crm_assignment.data.remote.RandomDataApi

object ApiClient {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    // This creates a logger which will print the request and response details.
    private val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // This is the network client. We add the logger to it here.
    private val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        .build()

    // Create the Retrofit instance, making sure to use our custom client.
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client) // This line is crucial
        .build()

    // Create a public instance of our API service that the app can use.
    val api: RandomDataApi = retrofit.create(RandomDataApi::class.java)
}