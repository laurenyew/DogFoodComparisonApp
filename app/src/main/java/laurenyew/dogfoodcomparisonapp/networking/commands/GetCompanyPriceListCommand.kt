package laurenyew.dogfoodcomparisonapp.networking.commands

import android.util.Log
import kotlinx.coroutines.async
import laurenyew.dogfoodcomparisonapp.models.Company
import laurenyew.dogfoodcomparisonapp.models.FoodRef
import laurenyew.dogfoodcomparisonapp.networking.api.response.CompanyPriceResponse
import retrofit2.Response

/**
 * This command makes the fake network call with the API
 * to get the company price list for a given [FoodRef].
 * It throws a runtime exception if it was unable to find the company list
 */
class GetCompanyPriceListCommand(networkDelay: Long = 0L) : BaseNetworkCommand(networkDelay) {

    @Throws(RuntimeException::class)
    suspend fun execute(foodRef: FoodRef): List<Company>? {
        val deferred = async {
            Log.d(TAG, "Executing $TAG with network delay: $networkDelay millis")
            val call = api?.getCompanyPriceList(foodRef.id)
            try {
                mockNetworkDelay(TAG, foodRef.brandName)
                val response = call?.execute()
                parseResponse(response, foodRef)
            } finally {
                //Clean up network call and cancel
                call?.cancel()
            }
        }
        return deferred.await()
    }

    /**
     * Parse the response from the network call
     */
    @Throws(RuntimeException::class)
    private fun parseResponse(response: Response<List<CompanyPriceResponse>?>?, foodRef: FoodRef): ArrayList<Company> {
        val data = response?.body()
        if (response?.code() != 200 || data == null) {
            throw RuntimeException("API call failed. Unable to find company list for given food ${foodRef.brandName}")
        } else {
            val companyList = ArrayList<Company>()
            data.forEach {
                companyList.add(Company(it.id, it.companyName, it.price))
            }
            Log.d(TAG, "Completed command with company list: ${companyList.size}")
            return companyList
        }
    }

    companion object {
        val TAG: String = GetCompanyPriceListCommand::class.java.simpleName
    }
}