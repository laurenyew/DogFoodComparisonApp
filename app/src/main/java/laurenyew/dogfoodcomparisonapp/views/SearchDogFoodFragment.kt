package laurenyew.dogfoodcomparisonapp.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_search_dog_food.*
import laurenyew.dogfoodcomparisonapp.R
import laurenyew.dogfoodcomparisonapp.TechType
import laurenyew.dogfoodcomparisonapp.contracts.SearchDogFoodContract
import laurenyew.dogfoodcomparisonapp.presenters.SearchDogFoodCoroutinesPresenter
import laurenyew.dogfoodcomparisonapp.views.adapters.DogFoodResultsRecyclerViewAdapter
import laurenyew.dogfoodcomparisonapp.views.adapters.data.CompanyPriceDataWrapper


/**
 * @author Lauren Yew
 *
 * Basic Search Dog Food Feature:
 *
 * Take user input on dog food brand names,
 * Return a list of companies / price points for that brand name if found
 */
class SearchDogFoodFragment : Fragment(), SearchDogFoodContract.View {
    private var delay: Long = DEFAULT_SEARCH_DELAY
    private var adapter: DogFoodResultsRecyclerViewAdapter? = null
    private var presenter: SearchDogFoodContract.Presenter? = null
    private var searchTerm: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var techType = TechType.KotlinCoroutines
        arguments?.let {
            techType = it.getSerializable(techTypeKey) as? TechType ?: TechType.KotlinCoroutines
            delay = it.getLong(
                delayKey,
                DEFAULT_SEARCH_DELAY
            )
        }

        presenter = createPresenter(techType)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_search_dog_food, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        companyPriceRecyclerView.apply {
            val linearLayoutManager =
                LinearLayoutManager(context)
            layoutManager = linearLayoutManager
            //Add separator lines between rows
            val dividerItemDecoration =
                DividerItemDecoration(
                    context,
                    linearLayoutManager.orientation
                )
            addItemDecoration(dividerItemDecoration)
        }

        searchFoodAButton.setOnClickListener {
            onSearch("FOOD_A")
        }
        searchFoodBButton.setOnClickListener {
            onSearch("FOOD_B")
        }
        searchInvalidFoodButton.setOnClickListener {
            onSearch("Invalid Brand Name")
        }

        presenter?.onBind(this, delay)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter?.unBind()
        adapter?.onDestroy()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter = null
        adapter = null
    }

    //region MVP
    override fun onSearchResultsLoaded(data: List<CompanyPriceDataWrapper>?) {
        if (isAdded && isVisible) {
            if (adapter == null) {
                adapter = DogFoodResultsRecyclerViewAdapter()
                companyPriceRecyclerView.adapter = adapter
            }
            adapter?.updateData(data)
            if (data == null || data.isEmpty()) {
                companyPriceEmptyTextView.text = getString(R.string.search_dog_food_empty)
                companyPriceEmptyTextView.visibility = View.VISIBLE
            } else {
                companyPriceEmptyTextView.visibility = View.GONE
            }
            loadingProgressBar.visibility = View.GONE
        }
    }

    override fun onSearchFailed(errorMessage: String) {
        if (isAdded && isVisible) {
            companyPriceEmptyTextView.text =
                getString(R.string.search_dog_food_empty_error, errorMessage)
        }
    }

    override fun onSearchJobCancelled(searchTerm: String) {
        if (isAdded && isVisible) {
            Toast.makeText(
                context,
                "Search For dog food results for search term: $searchTerm cancelled.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    //endregion

    //region Helper Methods
    private fun createPresenter(techType: TechType): SearchDogFoodContract.Presenter? =
        when (techType) {
            TechType.KotlinCoroutines -> SearchDogFoodCoroutinesPresenter()
            else -> null //TODO
        }

    /**
     * Send search to presenter presenter
     */
    private fun onSearch(newText: String?) {
        if (isAdded && isVisible) {
            val newSearchTerm = if (newText?.isNotEmpty() == true) newText else null
            //Don't allow duplicate searches
            if (searchTerm != newSearchTerm) {
                searchTerm = newSearchTerm
                companyPriceEmptyTextView.visibility = View.GONE
                //Show loading bar
                loadingProgressBar.visibility = View.VISIBLE
                //Make search call
                presenter?.onSearch(newSearchTerm)
            }
        }
    }
    //endregion

    companion object {
        private const val delayKey = "delayKey"
        private const val techTypeKey = "techTypeKey"

        @JvmStatic
        fun newInstance(
            techType: TechType,
            delay: Long = DEFAULT_SEARCH_DELAY
        ): SearchDogFoodFragment =
            SearchDogFoodFragment().apply {
                val bundle = Bundle()
                bundle.putSerializable(techTypeKey, techType)
                bundle.putLong(delayKey, delay)
                arguments = bundle
            }

        private const val DEFAULT_SEARCH_DELAY = 1000L
    }
}