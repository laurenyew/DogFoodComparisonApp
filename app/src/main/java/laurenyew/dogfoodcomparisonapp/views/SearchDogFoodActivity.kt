package laurenyew.dogfoodcomparisonapp.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import laurenyew.dogfoodcomparisonapp.R
import laurenyew.dogfoodcomparisonapp.TechType

/**
 * @author Lauren Yew
 *
 * Basic Search Dog Food Features +
 * Extra arguments for showing different features
 */
class SearchDogFoodActivity : AppCompatActivity() {
    private var techType = TechType.KotlinCoroutines
    private var isSlow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search_dog_food)

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        intent.extras?.let {
            techType = it.get(techTypeKey) as? TechType ?: TechType.KotlinCoroutines
            isSlow = it.getBoolean(isSlowKey, false)
        }

        setupFragment()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Respond to the action bar's Up/Home button
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupFragment() {
        val fragment =
            if (isSlow) {
                SearchDogFoodFragment.newInstance(techType, slowDelay) //delay of 3 mins
            } else {
                SearchDogFoodFragment.newInstance(techType)
            }

        supportFragmentManager.beginTransaction()
            .add(R.id.searchDogFoodFrameLayout, fragment)
            .commit()
    }

    companion object {
        private const val techTypeKey = "techTypeKey"
        private const val isSlowKey = "isSlowKey"

        @JvmStatic
        fun newInstance(context: Context, techType: TechType, isSlow: Boolean = false): Intent =
            Intent(context, SearchDogFoodActivity::class.java).apply {
                putExtra(techTypeKey, techType)
                putExtra(isSlowKey, isSlow)
            }

        private const val slowDelay: Long = 6 * 1000
    }
}