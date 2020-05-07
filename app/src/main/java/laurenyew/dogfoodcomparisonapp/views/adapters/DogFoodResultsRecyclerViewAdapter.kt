package laurenyew.dogfoodcomparisonapp.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import laurenyew.dogfoodcomparisonapp.R
import laurenyew.dogfoodcomparisonapp.views.adapters.data.CompanyPriceDataDiffCallback
import laurenyew.dogfoodcomparisonapp.views.adapters.data.CompanyPriceDataWrapper
import laurenyew.dogfoodcomparisonapp.views.adapters.viewholder.DogFoodResultViewHolder
import java.util.*
import kotlin.coroutines.CoroutineContext

/**
 * @author Lauren Yew
 *
 * RecyclerViewAdapter for showing the dog food results
 * With performance updates (update only parts of list that have changed)
 */
class DogFoodResultsRecyclerViewAdapter :
    RecyclerView.Adapter<DogFoodResultViewHolder>(), CoroutineScope {

    private val job = Job()
    private var data: MutableList<CompanyPriceDataWrapper> = ArrayList()
    private var pendingDataUpdates = ArrayDeque<List<CompanyPriceDataWrapper>>()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    //RecyclerView Diff.Util (List Updates)
    fun updateData(newData: List<CompanyPriceDataWrapper>?) {
        if (isActive) {
            val data = newData ?: ArrayList()
            pendingDataUpdates.add(data)
            if (pendingDataUpdates.size <= 1) {
                updateDataInternal(data)
            }
        }
    }

    //If the adapter is destroyed, cancel any running jobs
    fun onDestroy() {
        job.cancel()
        pendingDataUpdates.clear()
    }

    /**
     * Handle the diff util update on a background thread
     * (this can take O(n) time so we don't want it on the main thread)
     */
    private fun updateDataInternal(newData: List<CompanyPriceDataWrapper>?) {
        val oldData = ArrayList(data)

        launch {
            val diffCallback = createDataDiffCallback(oldData, newData)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            if (isActive) {
                withContext(Dispatchers.Main) {
                    applyDataDiffResult(newData, diffResult)
                }
            }
        }
    }

    /**
     * UI thread callback to apply the diff result to the adapter
     * and take in the latest update
     */
    private fun applyDataDiffResult(
        newData: List<CompanyPriceDataWrapper>?,
        diffResult: DiffUtil.DiffResult
    ) {
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
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.dog_food_result_preview_view, parent, false)
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