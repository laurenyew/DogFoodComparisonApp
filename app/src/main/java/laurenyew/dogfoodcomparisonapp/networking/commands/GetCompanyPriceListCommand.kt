package laurenyew.dogfoodcomparisonapp.networking.commands

import android.util.Log
import laurenyew.dogfoodcomparisonapp.models.Company
import laurenyew.dogfoodcomparisonapp.models.FoodRef

/**
 * This command makes the fake network call with the API
 * to get the company price list for a given [FoodRef].
 * It throws a runtime exception if it was unable to find the company list
 */
class GetCompanyPriceListCommand(networkDelay: Long = 0L) : BaseNetworkCommand(networkDelay) {

    @Throws(RuntimeException::class)
    fun execute(foodRef: FoodRef): List<Company>? {
        val api = dogFoodApi
        val call = api?.getCompanyPriceList(foodRef.id)
        if (networkDelay > 0) {
            Log.d(TAG, "sleeping for $networkDelay millis")
            Thread.sleep(networkDelay)
        }
        val response = call?.execute()

        val data = response?.body()
        if (response?.code() != 200 || data == null) {
            throw RuntimeException("API call failed. Unable to find company list for given food ${foodRef.brandName}")
        } else {
            val companyList = ArrayList<Company>()
            data.forEach {
                companyList.add(Company(it.id, it.companyName, it.price))
            }
            return companyList
        }
    }

    companion object {
        val TAG: String = GetCompanyPriceListCommand::class.java.simpleName
    }
}