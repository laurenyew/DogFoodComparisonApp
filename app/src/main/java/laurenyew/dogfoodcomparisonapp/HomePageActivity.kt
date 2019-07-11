package laurenyew.dogfoodcomparisonapp

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_home_page.message
import kotlinx.android.synthetic.main.activity_home_page.navigation
import kotlinx.android.synthetic.main.activity_home_page.openSearchForDogFoodButton
import kotlinx.android.synthetic.main.activity_home_page.openSlowSearchForDogFoodButton
import laurenyew.dogfoodcomparisonapp.networking.mock.MockServer
import laurenyew.dogfoodcomparisonapp.views.SearchDogFoodActivity

/**
 * @author Lauren Yew
 * Base Home Page: Allow user to choose which set of logic they'd like to use
 * and choose the UI pages to view sample behavior
 */
class HomePageActivity : FragmentActivity() {
    private var techType = TechType.KotlinCoroutines

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_kotlin_coroutines -> {
                techType = TechType.KotlinCoroutines
                message.text = getString(R.string.message_using_kotlin_coroutines)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_rxjava -> {
                techType = TechType.RxJava
                message.text = getString(R.string.message_using_rxjava)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_live_data -> {
                techType = TechType.LiveData
                message.text = getString(R.string.message_using_live_data)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        //Setup our mock server w/ mock responses
        MockServer.setup(applicationContext)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        openSearchForDogFoodButton.setOnClickListener { openSearchForDogFoodPage() }
        openSlowSearchForDogFoodButton.setOnClickListener { openSlowSearchForDogFoodPage() }
    }

    /**
     * Open the feature to search for dog food
     */
    private fun openSearchForDogFoodPage() {
        val intent = SearchDogFoodActivity.newInstance(baseContext, techType)
        startActivity(intent)
    }

    /**
     * Open the feature for a slow network call searching for dog food
     */
    private fun openSlowSearchForDogFoodPage() {
        val intent = SearchDogFoodActivity.newInstance(baseContext, techType, true)
        startActivity(intent)
    }
}
