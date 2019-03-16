package laurenyew.dogfoodcomparisonapp.networking.api.response

import com.squareup.moshi.Json

/**
 * @author Lauren Yew on 04/29/2018.
 * JSON Responses expected from the flickr api
 * (https://www.flickr.com/services/api/flickr.photos.search.html)
 *
 * Using Moshi to parse Json
 */
data class DogFoodResponse(
    @Json(name = "id") val id: String,
    @Json(name = "brand_name") val name: String,
    @Json(name = "nutrition") val nutrition: String
)

data class CompanyPriceResponse(
    @Json(name = "id") val id: String,
    @Json(name = "company_name") val companyName: String,
    @Json(name = "price") val price: String
)