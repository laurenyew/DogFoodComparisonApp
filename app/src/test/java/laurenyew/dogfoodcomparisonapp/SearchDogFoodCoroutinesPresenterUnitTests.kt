package laurenyew.dogfoodcomparisonapp

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.runBlocking
import laurenyew.dogfoodcomparisonapp.contracts.SearchDogFoodContract
import laurenyew.dogfoodcomparisonapp.presenters.SearchDogFoodCoroutinesPresenter
import laurenyew.dogfoodcomparisonapp.views.adapters.data.CompanyPriceDataWrapper
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mock

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class SearchDogFoodCoroutinesPresenterUnitTests : DogFoodBaseUnitTest() {
    @Mock
    private lateinit var mockView: SearchDogFoodContract.View

    private val presenter = SearchDogFoodCoroutinesPresenter()

    override fun setup() {
        super.setup()
        presenter.onBind(mockView, delay)
    }

    override fun teardown() {
        super.teardown()
        presenter.unBind()
    }

    @Test
    fun `verify setup`() {
        //Verify
        assertEquals(mockView, presenter.view)
        assertNotNull(presenter.job)
        assertTrue(presenter.job.isActive)
    }

    @Test
    fun `verify teardown`() {
        //Exercise
        presenter.unBind()

        //Verify
        assertNull(presenter.view)
        assertNotNull(presenter.job)
        assertFalse(presenter.job.isActive)
        assertTrue(presenter.job.isCancelled)
    }

    @Test
    fun `onSearch searchTerm null should update view`() {
        //Exercise
        presenter.onSearch(null)

        //Verify
        verify(mockView).onSearchResultsLoaded(null)
    }

    @Test
    fun `onSearch valid should start off network call and update with results`() = runBlocking {
        //Exercise
        presenter.onSearch(DOG_FOOD_A)
        presenter.searchJob!!.join() //Force the searchJob to complete

        //Verify
        val argumentCaptor = argumentCaptor<List<CompanyPriceDataWrapper>>()
        verify(mockView).onSearchResultsLoaded(argumentCaptor.capture())

        val result = argumentCaptor.firstValue
        assertNotNull(result)
        assertEquals(2, result.size)
        assertEquals("Chewy_ID", result[0].id)
        assertEquals("Petsmart_ID", result[1].id)
    }

    @Test
    fun `searchDogFoodCompanyPrices FOOD_A should get the expected results`() = runBlocking {
        //Exercise
        val results = presenter.searchDogFoodCompanyPrices(DOG_FOOD_A)

        //Verify
        assertNotNull(results)
        assertEquals(2, results!!.size)
    }

    @Test
    fun `searchDogFoodCompanyPrices FOOD_B should get the expected results`() = runBlocking {
        //Exercise
        val results = presenter.searchDogFoodCompanyPrices(DOG_FOOD_B)

        //Verify
        assertNotNull(results)
        assertEquals(2, results!!.size)
    }

    @Test
    fun `searchDogFoodCompanyPrices INVALID should get the expected results`() = runBlocking {
        //Setup
        var exception: RuntimeException? = null
        //Exercise
        try {
            presenter.searchDogFoodCompanyPrices(INVALID_DOG_FOOD)
        } catch (e: RuntimeException) {
            exception = e
        }

        //Verify
        assertNotNull(exception)
    }

    @Test
    fun `updateViewWithSearchResults valid should update view`() {
        //Setup
        val results = arrayListOf<CompanyPriceDataWrapper>()
        val errorMessage = null

        //Exercise
        presenter.updateViewWithSearchResults(results, errorMessage)

        //Verify
        verify(mockView).onSearchResultsLoaded(results)
        verify(mockView, never()).onSearchFailed(anyOrNull())
    }

    @Test
    fun `updateViewWithSearchResults invalid should update view`() {
        //Setup
        val results = arrayListOf<CompanyPriceDataWrapper>()
        val errorMessage = ""

        //Exercise
        presenter.updateViewWithSearchResults(results, errorMessage)

        //Verify
        verify(mockView).onSearchResultsLoaded(results)
        verify(mockView).onSearchFailed(errorMessage)
    }
}
