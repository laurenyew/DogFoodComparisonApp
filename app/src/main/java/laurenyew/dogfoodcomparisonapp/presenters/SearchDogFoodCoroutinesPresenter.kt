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

    //region Getters
    val view: SearchDogFoodContract.View?
        get() = viewRef?.get()
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
            //Launch / Return on Background thread
            launch {
                try {
                    val companyPriceResults = searchDogFoodCompanyPrices(searchTerm)
                    updateViewWithSearchResults(companyPriceResults)
                } catch (e: RuntimeException) {
                    withContext(Dispatchers.Main) {
                        updateViewWithSearchResults(null, e)
                    }
                }
            }
        } else {
            view?.onSearchResultsLoaded(null)
        }
    }

    /**
     * Launch coroutines on a background pool of threads
     */
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    //region Helper Methods

    /**
     * Coroutines call to get dog food results w/ network calls
     */
    @Throws(RuntimeException::class)
    private suspend fun searchDogFoodCompanyPrices(searchTerm: String): List<CompanyPriceDataWrapper>? {
        var dogFoodCompanyPrices: List<CompanyPriceDataWrapper>? = null
        //Make network calls on IO thread
        withContext(Dispatchers.IO) {
            val deferredResult = async {
                getDogFood(searchTerm)?.let { foodRef ->
                    getCompanyPriceList(foodRef)
                }
            }
            dogFoodCompanyPrices = parseSearchResults(deferredResult.await())
        }
        return dogFoodCompanyPrices
    }

    /**
     * Coroutines call to update the view on the main thread
     * with results
     */
    private suspend fun updateViewWithSearchResults(
        companyPriceResults: List<CompanyPriceDataWrapper>?,
        exception: Exception? = null
    ) {
        withContext(Dispatchers.Main) {
            view?.onSearchResultsLoaded(companyPriceResults)
            exception?.message?.let {
                view?.onSearchFailed(it)
            }
        }
    }

    /**
     * Make a network call to get the dog food
     */
    @Throws(RuntimeException::class)
    private fun getDogFood(brandName: String): FoodRef? {
        val command = GetDogFoodCommand(networkCallDelay)
        return command.execute(brandName)
    }

    /**
     * Make a network call to get the company price list
     */
    @Throws(RuntimeException::class)
    private fun getCompanyPriceList(foodRef: FoodRef): List<Company>? {
        val command = GetCompanyPriceListCommand(networkCallDelay)
        return command.execute(foodRef)
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