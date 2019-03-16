package laurenyew.dogfoodcomparisonapp.presenters

import laurenyew.dogfoodcomparisonapp.contracts.SearchDogFoodContract
import laurenyew.dogfoodcomparisonapp.models.Company
import laurenyew.dogfoodcomparisonapp.models.FoodRef
import laurenyew.dogfoodcomparisonapp.networking.commands.GetCompanyPriceListCommand
import laurenyew.dogfoodcomparisonapp.networking.commands.GetDogFoodCommand
import laurenyew.dogfoodcomparisonapp.views.adapters.data.CompanyPriceDataWrapper
import java.lang.ref.WeakReference

/**
 * @author Lauren Yew
 *
 * Search Dog Food Contract Presenter using Kotlin Coroutines for threading
 */
class SearchDogFoodCoroutinesPresenter : SearchDogFoodContract.Presenter {
    private var viewRef: WeakReference<SearchDogFoodContract.View>? = null
    private var networkCallDelay: Long = 0L

    //region Getters
    val view: SearchDogFoodContract.View?
        get() = viewRef?.get()
    //endregion

    override fun onBind(view: SearchDogFoodContract.View, delay: Long) {
        viewRef = WeakReference(view)
        networkCallDelay = delay
    }

    override fun unBind() {
        viewRef?.clear()
        viewRef = null
    }

    override fun onSearch(searchTerm: String?) {
        if (searchTerm != null) {
            try {
                val foodRef = getDogFood(searchTerm)
                if (foodRef != null) {
                    val companyList = getCompanyPriceList(foodRef)
                    if (companyList != null) {
                        parseSearchResults(companyList)
                    } else {
                        view?.onSearchResultsLoaded(null)
                    }
                } else {
                    view?.onSearchResultsLoaded(null)
                }
            } catch (e: RuntimeException) {
                view?.onSearchResultsLoaded(null)
                e.message?.let {
                    view?.onSearchFailed(it)
                }
            }
        } else {
            view?.onSearchResultsLoaded(null)
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
    private fun parseSearchResults(companyPriceList: List<Company>) {
        val data: ArrayList<CompanyPriceDataWrapper> = ArrayList()
        companyPriceList.forEach {
            data.add(CompanyPriceDataWrapper(it.id, it.companyName, it.price))
        }
        view?.onSearchResultsLoaded(data)
    }
}