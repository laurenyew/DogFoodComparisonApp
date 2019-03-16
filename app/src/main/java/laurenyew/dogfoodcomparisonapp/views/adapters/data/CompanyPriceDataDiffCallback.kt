package laurenyew.dogfoodcomparisonapp.views.adapters.data

import android.support.v7.util.DiffUtil

/**
 * @author Lauren Yew
 *
 * DiffUtil.Callback that compares data wrappers
 */
open class CompanyPriceDataDiffCallback(
    private val oldData: List<CompanyPriceDataWrapper>?,
    private val newData: List<CompanyPriceDataWrapper>?
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldData?.size ?: 0

    override fun getNewListSize(): Int = newData?.size ?: 0

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldData?.get(oldItemPosition)
        val newItem = newData?.get(newItemPosition)

        return oldItem?.id == newItem?.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldData?.get(oldItemPosition)
        val newItem = newData?.get(newItemPosition)

        return oldItem?.companyName == newItem?.companyName && oldItem?.companyPrice == newItem?.companyPrice
    }
}