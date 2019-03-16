package laurenyew.dogfoodcomparisonapp.networking.api

import laurenyew.dogfoodcomparisonapp.networking.api.DogFoodApiConstants.GET_COMPANY_PRICE_LIST_METHOD
import laurenyew.dogfoodcomparisonapp.networking.api.DogFoodApiConstants.GET_DOG_FOOD_METHOD
import laurenyew.dogfoodcomparisonapp.networking.api.response.CompanyPriceResponse
import laurenyew.dogfoodcomparisonapp.networking.api.response.DogFoodResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author Lauren Yew
 * Retrofit api for fake dog food api end points
 */
interface DogFoodApi {
    /**
     * Get the dog food ref based on a given brand name
     */
    @Throws(RuntimeException::class)
    @GET(GET_DOG_FOOD_METHOD)
    fun getDogFood(@Query(DogFoodApiConstants.BRAND_NAME_QUERY_PARAM) brandName: String)
            : Call<DogFoodResponse?>?

    /**
     * Get the company/price list for a given dog food
     */
    @Throws(RuntimeException::class)
    @GET(GET_COMPANY_PRICE_LIST_METHOD)
    fun getCompanyPriceList(@Query(DogFoodApiConstants.DOG_FOOD_ID_QUERY_PARAM) dogFoodId: String)
            : Call<List<CompanyPriceResponse>?>?
}