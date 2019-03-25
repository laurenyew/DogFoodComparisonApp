package laurenyew.dogfoodcomparisonapp.presenters

import kotlinx.coroutines.*
import laurenyew.dogfoodcomparisonapp.contracts.SearchDogFoodContract
import laurenyew.dogfoodcomparisonapp.models.Company
import laurenyew.dogfoodcomparisonapp.models.FoodRef
import laurenyew.dogfoodcomparisonapp.networking.commands.GetCompanyPriceListCommand
import laurenyew.dogfoodcomparisonapp.networking.commands.GetDogFoodCommand
import laurenyew.dogfoodcomparisonapp.views.adapters.data.CompanyPriceDataWrapper
import java.lang.ref.WeakReference
import kotlin.coroutines.CoroutineContext

/**
 * @author Lauren Yew
 *
 * Search Dog Food Contract Presenter using Kotlin Coroutines for threading
 */
class SearchDogFoodCoroutinesPresenter : SearchDogFoodContract.Presenter, CoroutineScope {
    private lateinit var job: Job
    private var viewRef: WeakReference<SearchDogFoodContract.View>? = null
    private var networkCallDelay: Long = 0L
    private var searchJob: Job? = null

    //region Getters
    val view: SearchDogFoodContract.View?
        get() = viewRef?.get()

    /**
     * Launch coroutines on a background pool of threads
     */
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job
    //endregion

    override fun onBind(view: SearchDogFoodContract.View, delay: Long) {
        job = Job()
        viewRef = WeakReference(view)
        networkCallDelay = delay
    }

    override fun unBind() {
        job.cancel()
        viewRef?.clear()
        viewRef = null
    }

    /**
     * Search for dog food results using Kotlin Coroutines
     */
    override fun onSearch(searchTerm: String?) {
        if (searchTerm != null) {
            //Only one search should be run at a time
            searchJob?.cancel()

            //Launch coroutines on Background thread pool
            searchJob = launch {
                try {
                    val results = searchDogFoodCompanyPrices(searchTerm)
                    //Send updates back to UI thread
                    withContext(Dispatchers.Main) {
                        updateViewWithSearchResults(results)
                    }
                } catch (runtimeException: RuntimeException) {
                    withContext(Dispatchers.Main) {
                        updateViewWithSearchResults(
                            null,
                            runtimeException.message
                        )
                    }
                }
            }

        } else {
            view?.onSearchResultsLoaded(null)
        }
    }

    //region Helper Methods

    /**
     * Coroutines call to get dog food results w/ network calls
     */
    @Throws(RuntimeException::class)
    private suspend fun searchDogFoodCompanyPrices(searchTerm: String):
            List<CompanyPriceDataWrapper>? {
        var dogFoodCompanyPrices: List<CompanyPriceDataWrapper>? = null
        //Put these dependent calls in the same scope
        coroutineScope {
            val dogFood = getDogFood(searchTerm)
            val result = dogFood?.let { getCompanyPriceList(it) }
            dogFoodCompanyPrices = parseSearchResults(result)
        }
        return dogFoodCompanyPrices
    }

    /**
     * Coroutines call to update the view on the main thread
     * with results
     */
    private fun updateViewWithSearchResults(
        companyPriceResults: List<CompanyPriceDataWrapper>?,
        errorMessage: String? = null
    ) {
        view?.onSearchResultsLoaded(companyPriceResults)
        errorMessage?.let {
            view?.onSearchFailed(it)
        }
    }


    /**
     * Make a network call to get the dog food
     */
    @Throws(RuntimeException::class)
    private suspend fun getDogFood(brandName: String): FoodRef? {
        val command = GetDogFoodCommand(networkCallDelay)
        try {
            return command.execute(brandName)
        } finally {
            command.finish()
        }
    }

    /**
     * Make a network call to get the company price list
     */
    @Throws(RuntimeException::class)
    private suspend fun getCompanyPriceList(foodRef: FoodRef): List<Company>? {
        val command = GetCompanyPriceListCommand(networkCallDelay)
        try {
            return command.execute(foodRef)
        } finally {
            command.finish()
        }
    }

    /**
     * Parse the search results and update UI
     */
    private fun parseSearchResults(companyPriceList: List<Company>?): List<CompanyPriceDataWrapper>? {
        var data: ArrayList<CompanyPriceDataWrapper>? = null
        if (companyPriceList != null) {
            data = ArrayList()
            companyPriceList.forEach {
                data.add(CompanyPriceDataWrapper(it.id, it.companyName, it.price))
            }
        }
        return data
    }
//endregion
}