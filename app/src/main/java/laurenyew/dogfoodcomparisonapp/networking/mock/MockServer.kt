package laurenyew.dogfoodcomparisonapp.networking.mock

import android.content.Context
import android.support.annotation.VisibleForTesting
import okhttp3.*

/**
 * This MockServer object will pretend to be the server across the network
 * and just return to us the mocked json responses
 */
object MockServer {
    private const val FOOD_A = "FOOD_A"
    private const val FOOD_B = "FOOD_B"
    private const val FOOD_A_ID = "FOOD_A_ID"
    private const val FOOD_B_ID = "FOOD_B_ID"

    private const val FOOD_A_GET_FOOD_MOCK_RESPONSE_FILE = "mockFoodADogFoodResponse.json"
    private const val FOOD_B_GET_FOOD_MOCK_RESPONSE_FILE = "mockFoodBDogFoodResponse.json"
    private const val FOOD_A_GET_COMPANY_PRICE_LIST_MOCK_RESPONSE_FILE = "mockFoodACompanyListResponse.json"
    private const val FOOD_B_GET_COMPANY_PRICE_LIST_MOCK_RESPONSE_FILE = "mockFoodBCompanyListResponse.json"

    @VisibleForTesting
    lateinit var mockFoodADogFoodJsonResponse: String
    @VisibleForTesting
    lateinit var mockFoodBDogFoodJsonResponse: String
    @VisibleForTesting
    lateinit var mockFoodACompanyListJsonResponse: String
    @VisibleForTesting
    lateinit var mockFoodBCompanyListJsonResponse: String

    /**
     * Setup must be called so we can set up the mock responses
     */
    fun setup(context: Context) {
        mockFoodADogFoodJsonResponse = MockJsonUtil.getJsonStringFromFile(context, FOOD_A_GET_FOOD_MOCK_RESPONSE_FILE)
        mockFoodBDogFoodJsonResponse = MockJsonUtil.getJsonStringFromFile(context, FOOD_B_GET_FOOD_MOCK_RESPONSE_FILE)
        mockFoodACompanyListJsonResponse =
            MockJsonUtil.getJsonStringFromFile(context, FOOD_A_GET_COMPANY_PRICE_LIST_MOCK_RESPONSE_FILE)
        mockFoodBCompanyListJsonResponse =
            MockJsonUtil.getJsonStringFromFile(context, FOOD_B_GET_COMPANY_PRICE_LIST_MOCK_RESPONSE_FILE)
    }

    /**
     * Get the mock json response (or empty response)
     * for the requested brand name
     */
    fun getDogFoodResponse(request: Request, brandName: String): Response {
        val jsonString = when (brandName) {
            FOOD_A -> mockFoodADogFoodJsonResponse
            FOOD_B -> mockFoodBDogFoodJsonResponse
            else -> null
        }
        return createResponse(request, jsonString)
    }

    /**
     * Get the mock json response (or empty response)
     * for the requested food ref
     */
    fun getCompanyPriceListResponse(request: Request, foodRefId: String): Response {
        val jsonString = when (foodRefId) {
            FOOD_A_ID -> mockFoodACompanyListJsonResponse
            FOOD_B_ID -> mockFoodBCompanyListJsonResponse
            else -> null
        }
        return createResponse(request, jsonString)
    }

    /**
     * Create response based off the json string found
     */
    private fun createResponse(request: Request, jsonString: String?): Response {
        val code = jsonString?.let { 200 } ?: 400
        val message = jsonString?.let { "Success" } ?: "Error"
        val body = jsonString.let {
            ResponseBody.create(
                MediaType.parse("application/json"),
                it ?: ""
            )
        }
        val responseBuilder = Response.Builder()
        responseBuilder.request(request)
        responseBuilder.protocol(Protocol.HTTP_2)
        responseBuilder.message(message)
        responseBuilder.code(code)
        responseBuilder.body(body)
        return responseBuilder.build()
    }
}