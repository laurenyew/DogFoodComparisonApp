package laurenyew.dogfoodcomparisonapp.networking.commands

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import laurenyew.dogfoodcomparisonapp.models.FoodRef
import laurenyew.dogfoodcomparisonapp.networking.DogFoodApiBuilder
import laurenyew.dogfoodcomparisonapp.networking.api.DogFoodApi
import kotlin.coroutines.CoroutineContext

/**
 * This command makes the fake network call with the API
 * to get the company price list for a given [FoodRef].
 * It throws a runtime exception if it was unable to find the company list
 */
open class BaseNetworkCommand(val networkDelay: Long = 0L) : CoroutineScope {
    private var job = Job()
    var api: DogFoodApi? = DogFoodApiBuilder.apiBuilder(DogFoodApi::class.java)

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    fun mockNetworkDelay(methodName: String, searchTerm: String) {
        var delayTime = DELAY_SPLIT_TIME
        while (delayTime <= networkDelay) {
            if (!isActive) {
                Log.d(TAG, "------- CANCEL network delay for $methodName($searchTerm)")
                break
            } else {
                Thread.sleep(DELAY_SPLIT_TIME)
                Log.d(TAG, "$methodName($searchTerm): Slept for $delayTime millis")
                delayTime += DELAY_SPLIT_TIME
            }
        }
    }

    fun finish() {
        job.cancel()
    }

    companion object {
        val TAG: String = BaseNetworkCommand::class.java.simpleName
        const val DELAY_SPLIT_TIME = 500L
    }
}