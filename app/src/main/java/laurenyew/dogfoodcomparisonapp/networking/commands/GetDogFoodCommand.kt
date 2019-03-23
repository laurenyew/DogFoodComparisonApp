package laurenyew.dogfoodcomparisonapp.networking.commands

import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import laurenyew.dogfoodcomparisonapp.models.FoodRef

/**
 * This command makes the fake network call with the API
 * to get the dog food. It throws a runtime exception if it was unable to find the food
 */
class GetDogFoodCommand(networkDelay: Long = 0L) : BaseNetworkCommand(networkDelay) {

    @Throws(RuntimeException::class)
    suspend fun execute(brandName: String): FoodRef? {
        var foodRef: FoodRef? = null
        coroutineScope {
            val deferred = async {
                Log.d(GetCompanyPriceListCommand.TAG, "Executing ${TAG} with network delay: $networkDelay millis")
                val api = dogFoodApi
                val call = api?.getDogFood(brandName)
                if (networkDelay > 0) {
                    Log.d(TAG, "sleeping for $networkDelay millis")
                    Thread.sleep(networkDelay)
                }
                val response = call?.execute()
                val data = response?.body()
                if (response?.code() != 200 || data == null) {
                    throw RuntimeException("API call failed. Unable to find dog food $brandName")
                } else {
                    Log.d(TAG, "Completed command with food ref")
                    FoodRef(data.id, data.name, data.nutrition)
                }
            }
            foodRef = deferred.await()
        }
        return foodRef
    }

    companion object {
        val TAG: String = GetDogFoodCommand::class.java.simpleName
    }
}