package laurenyew.dogfoodcomparisonapp.views.adapters

import android.os.Handler
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import laurenyew.dogfoodcomparisonapp.R
import laurenyew.dogfoodcomparisonapp.views.adapters.data.CompanyPriceDataDiffCallback
import laurenyew.dogfoodcomparisonapp.views.adapters.data.CompanyPriceDataWrapper
import laurenyew.dogfoodcomparisonapp.views.adapters.viewholder.DogFoodResultViewHolder
import java.util.*

/**
 * @author Lauren Yew
 *
 * RecyclerViewAdapter for showing the dog food results
 * With performance updates (update only parts of list that have changed)
 */
class DogFoodResultsRecyclerViewAdapter : RecyclerView.Adapter<DogFoodResultViewHolder>() {
    private var data: MutableList<CompanyPriceDataWrapper> = ArrayList()
    private var pendingDataUpdates = ArrayDeque<List<CompanyPriceDataWrapper>>()

    //RecyclerView Diff.Util (List Updates)
    open fun updateData(newData: List<CompanyPriceDataWrapper>?) {
        val data = newData ?: ArrayList()
        pendingDataUpdates.add(data)
        if (pendingDataUpdates.size <= 1) {
            updateDataInternal(data)
        }
    }

    /**
     * Handle the diff util update on a background thread
     * (this can take O(n) time so we don't want it on the main thread)
     */
    private fun updateDataInternal(newData: List<CompanyPriceDataWrapper>?) {
        val oldData = ArrayList(data)

        //TODO: Kotlin Coroutines here?
        val handler = Handler()
        Thread(Runnable {
            val diffCallback = createDataDiffCallback(oldData, newData)
            val diffResult = DiffUtil.calculateDiff(diffCallback)

            handler.post {
                applyDataDiffResult(newData, diffResult)
            }
        }).start()
    }

    /**
     * UI thread callback to apply the diff result to the adapter
     * and take in the latest update
     */
    private fun applyDataDiffResult(newData: List<CompanyPriceDataWrapper>?, diffResult: DiffUtil.DiffResult) {
        if (pendingDataUpdates.isNotEmpty()) {
            pendingDataUpdates.remove()
        }

        //Apply the data to the view
        data.clear()
        if (newData != null) {
            data.addAll(newData)
        }
        diffResult.dispatchUpdatesTo(this)

        //Take in the next latest update
        if (pendingDataUpdates.isNotEmpty()) {
            val latestDataUpdate = pendingDataUpdates.pop()
            pendingDataUpdates.clear()
            updateDataInternal(latestDataUpdate)
        }
    }

    private fun createDataDiffCallback(
        oldData: List<CompanyPriceDataWrapper>?,
        newData: List<CompanyPriceDataWrapper>?
    ): DiffUtil.Callback =
        CompanyPriceDataDiffCallback(oldData, newData)
    //endregion

    //region RecyclerView.Adapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogFoodResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dog_food_result_preview_view, parent, false)
        return DogFoodResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: DogFoodResultViewHolder, position: Int) {
        val item = data[position]
        holder.companyNameTextView.text = item.companyName
        holder.dogFoodPriceTextView.text = item.companyPrice
    }

    override fun getItemCount(): Int = data.size
    //endregion
}