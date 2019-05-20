package laurenyew.dogfoodcomparisonapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import laurenyew.dogfoodcomparisonapp.networking.mock.MockJsonUtil
import laurenyew.dogfoodcomparisonapp.networking.mock.MockServer
import org.junit.After
import org.junit.Before
import org.mockito.MockitoAnnotations

open class DogFoodBaseUnitTest {
    val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    open fun setup() {
        MockitoAnnotations.initMocks(this)

        Dispatchers.setMain(mainThreadSurrogate)

        val classLoader = javaClass.classLoader
        val mockFoodANameResponse = classLoader.getResourceAsStream("mockFoodADogFoodResponse.json")
        MockServer.mockFoodADogFoodJsonResponse = MockJsonUtil.convertInputStreamToString(mockFoodANameResponse)
        val mockFoodACompanyResponse = classLoader.getResourceAsStream("mockFoodACompanyListResponse.json")
        MockServer.mockFoodACompanyListJsonResponse =
            MockJsonUtil.convertInputStreamToString(mockFoodACompanyResponse)

        val mockFoodBNameResponse = classLoader.getResourceAsStream("mockFoodBDogFoodResponse.json")
        MockServer.mockFoodBDogFoodJsonResponse = MockJsonUtil.convertInputStreamToString(mockFoodBNameResponse)
        val mockFoodBCompanyResponse = classLoader.getResourceAsStream("mockFoodBCompanyListResponse.json")
        MockServer.mockFoodBCompanyListJsonResponse =
            MockJsonUtil.convertInputStreamToString(mockFoodBCompanyResponse)
    }

    @After
    open fun teardown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    companion object {
        const val delay = 0L
        const val DOG_FOOD_A = "FOOD_A"
        const val DOG_FOOD_B = "FOOD_B"
        const val INVALID_DOG_FOOD = "test"
    }
}