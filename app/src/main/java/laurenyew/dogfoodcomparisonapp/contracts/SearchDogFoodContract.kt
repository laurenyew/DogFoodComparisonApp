package laurenyew.dogfoodcomparisonapp.contracts

import laurenyew.dogfoodcomparisonapp.views.adapters.data.CompanyPriceDataWrapper

/**
 * @author Lauren Yew
 * MVP Contract for Search Dog Food Feature
 */
interface SearchDogFoodContract {
    interface View {
        fun onSearchResultsLoaded(data: List<CompanyPriceDataWrapper>?)
        fun onSearchFailed(errorMessage: String)
    }

    interface Presenter {
        fun onBind(view: View, delay: Long)
        fun unBind()
        fun onSearch(searchTerm: String?)
    }
}