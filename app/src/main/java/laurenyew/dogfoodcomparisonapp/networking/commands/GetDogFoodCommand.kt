package laurenyew.dogfoodcomparisonapp.networking.commands

import android.util.Log
import laurenyew.dogfoodcomparisonapp.models.FoodRef

/**
 * This command makes the fake network call with the API
 * to get the dog food. It throws a runtime exception if it was unable to find the food
 */
class GetDogFoodCommand(networkDelay: Long = 0L) : BaseNetworkCommand(networkDelay) {

    @Throws(RuntimeException::class)
    fun execute(brandName: String): FoodRef? {
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
            return FoodRef(data.id, data.name, data.nutrition)
        }
    }

    companion object {
        val TAG: String = GetDogFoodCommand::class.java.simpleName
    }
}