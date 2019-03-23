package laurenyew.dogfoodcomparisonapp.networking.commands

import android.support.annotation.VisibleForTesting
import kotlinx.coroutines.CoroutineScope
import laurenyew.dogfoodcomparisonapp.models.FoodRef
import laurenyew.dogfoodcomparisonapp.networking.DogFoodApiBuilder
import laurenyew.dogfoodcomparisonapp.networking.api.DogFoodApi
import kotlin.coroutines.CoroutineContext

/**
 * This command makes the fake network call with the API
 * to get the company price list for a given [FoodRef].
 * It throws a runtime exception if it was unable to find the company list
 */
open class BaseNetworkCommand(val networkDelay: Long = 0L): CoroutineScope {
    private var _dogFoodApi = DogFoodApiBuilder.apiBuilder(DogFoodApi::class.java)
    open var dogFoodApi: DogFoodApi?
        get() = _dogFoodApi
        @VisibleForTesting
        set(api) {
            _dogFoodApi = api
        }

    override val coroutineContext: CoroutineContext
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
}