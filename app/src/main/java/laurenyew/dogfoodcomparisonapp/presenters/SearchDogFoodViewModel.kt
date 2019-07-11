package laurenyew.dogfoodcomparisonapp.presenters

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import laurenyew.dogfoodcomparisonapp.models.Company
import laurenyew.dogfoodcomparisonapp.models.FoodRef
import laurenyew.dogfoodcomparisonapp.networking.commands.GetCompanyPriceListCommand
import laurenyew.dogfoodcomparisonapp.networking.commands.GetDogFoodCommand
import laurenyew.dogfoodcomparisonapp.views.adapters.data.CompanyPriceDataWrapper

class SearchDogFoodViewModel(private val networkCallDelay: Long = 0L) : ViewModel() {
    @VisibleForTesting
    var searchJob: Job? = null

    private val searchResults: MutableLiveData<List<CompanyPriceDataWrapper>?> by lazy {
        MutableLiveData<List<CompanyPriceDataWrapper>?>().also {
            onSearch(null)
        }
    }

    fun getSearchResults(): LiveData<List<CompanyPriceDataWrapper>?> {
        return searchResults
    }

    fun onSearch(searchTerm: String?) {
        if (searchTerm != null) {
            //Only one search should be run at a time
            searchJob?.cancel()

            //Launch coroutines on Background thread pool
            searchJob = viewModelScope.launch {
                try {
                    val results = searchDogFoodCompanyPrices(searchTerm)
                    //Send updates back to UI thread
                    withContext(Dispatchers.Main) {
                        searchResults.value = results
                    }
                } catch (runtimeException: RuntimeException) {
                    //Send updates back to UI thread
                    withContext(Dispatchers.Main) {
                        searchResults.value = null
                    }
                }
            }

        } else {
            searchResults.value = null
        }
    }

    //region Helper Methods

    /**
     * Coroutines call to get dog food results w/ network calls
     */
    @Throws(RuntimeException::class)
    @VisibleForTesting
    suspend fun searchDogFoodCompanyPrices(searchTerm: String):
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
    @VisibleForTesting
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
}