package laurenyew.dogfoodcomparisonapp.networking

import androidx.annotation.VisibleForTesting
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import laurenyew.dogfoodcomparisonapp.networking.api.DogFoodApiConstants
import laurenyew.dogfoodcomparisonapp.networking.mock.MockServer
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*

/**
 * @author Lauren Yew on 04/29/2018.
 *
 * Api Builder to keep the retrofit creation logic separate from the commands
 *
 * Note: Currently trusting all certs b/c was getting "CertificateRevokedException: Certificate has been revoked, reason: UNSPECIFIED"
 * b/c flickr certificates kept expiring for Android. Wouldn't put something like this in production,
 * but for a test-app, leaving for now.
 * Resource: https://futurestud.io/tutorials/retrofit-2-how-to-trust-unsafe-ssl-certificates-self-signed-expired
 */
object DogFoodApiBuilder {
    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val okHttpClient: OkHttpClient.Builder
    private val retrofit: Retrofit

    init {
        okHttpClient = setupOkHttp()
        retrofit = Retrofit.Builder().baseUrl(DogFoodApiConstants.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient.build()).build()
    }

    fun <T> apiBuilder(apiClazz: Class<T>): T? = retrofit.create(apiClazz)

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun setupOkHttp(): OkHttpClient.Builder {
        //Setup HttpClient
        val httpClientBuilder = OkHttpClient.Builder()
        val interceptor = MockNetworkResultInterceptor()
        httpClientBuilder.addInterceptor(interceptor)

        //Add headers
        httpClientBuilder.addInterceptor {
            val request = it.request()
                .newBuilder()
                .addHeader("Accept-Language", Locale.getDefault().language)
                .build()
            it.proceed(request)
        }
        return httpClientBuilder
    }

    class MockNetworkResultInterceptor : Interceptor {
        @Throws(RuntimeException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val url = request.url()
            val path = url.pathSegments()[1]
            val response =
                when (path) {
                    DogFoodApiConstants.GET_DOG_FOOD_METHOD -> interceptGetDogFoodResponse(
                        request,
                        url
                    )
                    DogFoodApiConstants.GET_COMPANY_PRICE_LIST_METHOD -> interceptGetCompanyPriceListResponse(
                        request,
                        url
                    )
                    else -> throw RuntimeException("Invalid Request Path")
                }

            return response
        }

        @Throws(RuntimeException::class)
        fun interceptGetDogFoodResponse(request: Request, url: HttpUrl): Response {
            val query = url.queryParameter(DogFoodApiConstants.BRAND_NAME_QUERY_PARAM)
                ?: throw RuntimeException("Invalid Request Query Param")
            return MockServer.getDogFoodResponse(request, query)
        }

        @Throws(RuntimeException::class)
        fun interceptGetCompanyPriceListResponse(request: Request, url: HttpUrl): Response {
            val query = url.queryParameter(DogFoodApiConstants.DOG_FOOD_ID_QUERY_PARAM)
                ?: throw RuntimeException("Invalid Request Query Param")
            return MockServer.getCompanyPriceListResponse(request, query)
        }
    }
}