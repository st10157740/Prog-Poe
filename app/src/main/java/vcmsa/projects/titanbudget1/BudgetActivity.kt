package vcmsa.projects.titanbudget1

import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import vcmsa.projects.titanbudget1.databinding.ActivityBudgetBinding

class BudgetActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityBudgetBinding
    private lateinit var txtCurrentUser: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        // Apply theme before anything else
        val prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val theme = prefs.getString("app_theme", "light")

        if (theme == "dark") {
            setTheme(R.style.Base_Theme_TitanBudget1_Dark)
        } else {
            setTheme(R.style.Base_Theme_TitanBudget1)
        }

        super.onCreate(savedInstanceState)

        binding = ActivityBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.appBarBudget.toolbar)


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_budget)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_goals, R.id.nav_totalCategoriesSummary, R.id.nav_viewCategories
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.budget, menu)
        txtCurrentUser = findViewById(R.id.txtCurrentUser)

        txtCurrentUser.text = FirebaseAuth.getInstance().currentUser?.email?: ""
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_budget)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}