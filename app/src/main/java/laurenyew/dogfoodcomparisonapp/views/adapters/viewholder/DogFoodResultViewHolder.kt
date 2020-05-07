package laurenyew.dogfoodcomparisonapp.views.adapters.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import laurenyew.dogfoodcomparisonapp.R

class DogFoodResultViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val companyNameTextView = view.findViewById<TextView>(R.id.companyNameTextView)
    val dogFoodPriceTextView = view.findViewById<TextView>(R.id.dogFoodPriceTextView)
}