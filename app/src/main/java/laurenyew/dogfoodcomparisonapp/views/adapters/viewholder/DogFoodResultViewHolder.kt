package laurenyew.dogfoodcomparisonapp.views.adapters.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import laurenyew.dogfoodcomparisonapp.R

class DogFoodResultViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val companyNameTextView = view.findViewById<TextView>(R.id.companyNameTextView)
    val dogFoodPriceTextView = view.findViewById<TextView>(R.id.dogFoodPriceTextView)
}