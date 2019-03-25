package laurenyew.dogfoodcomparisonapp.networking.commands

import android.util.Log
import kotlinx.coroutines.async
import laurenyew.dogfoodcomparisonapp.models.FoodRef
import laurenyew.dogfoodcomparisonapp.networking.api.response.DogFoodResponse
import retrofit2.Response

/**
 * This command makes the fake network call with the API
 * to get the dog food. It throws a runtime exception if it was unable to find the food
 */
class GetDogFoodCommand(networkDelay: Long = 0L) : BaseNetworkCommand(networkDelay) {

    @Throws(RuntimeException::class)
    suspend fun execute(brandName: String): FoodRef? {
        val deferred = async {
            Log.d(GetCompanyPriceListCommand.TAG, "Executing $TAG " +
                    "with network delay: $networkDelay millis")
            val call = api?.getDogFood(brandName)
            try {
                mockNetworkDelay(TAG, brandName)
                val response = call?.execute()
                parseResponse(response, brandName)
            } finally {
                //Clean up and cancel network call
                call?.cancel()
            }
        }
        return deferred.await()
    }

    /**
     * Parse the network response
     */
    @Throws(RuntimeException::class)
    private fun parseResponse(response: Response<DogFoodResponse?>?, brandName: String): FoodRef {
        val data = response?.body()
        if (response?.code() != 200 || data == null) {
            throw RuntimeException("API call failed. Unable to find dog food $brandName")
        } else {
            Log.d(TAG, "Completed command with food ref")
            return FoodRef(data.id, data.name, data.nutrition)
        }
    }

    companion object {
        val TAG: String = GetDogFoodCommand::class.java.simpleName
    }
}