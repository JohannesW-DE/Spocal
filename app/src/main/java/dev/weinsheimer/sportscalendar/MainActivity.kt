package dev.weinsheimer.sportscalendar

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import dev.weinsheimer.sportscalendar.databinding.ActivityMainBinding
import dev.weinsheimer.sportscalendar.viewmodels.SharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    val viewModel: SharedViewModel by viewModel()

    var error = false

    private var calendarUpdateCompleted = true
    private var refreshCompleted = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        drawerLayout = binding.drawerLayout
        val navController = this.findNavController(R.id.navHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navigationView, navController)

        applicationContext.deleteDatabase("spocal") // DEBUGGING

        // display information/errors/warnings
        viewModel.toast.observe(this, Observer { resourceId ->
            error = true
            Toast.makeText(applicationContext, resourceId, Toast.LENGTH_SHORT).show()
            invalidateOptionsMenu()
        })

        // watch out for calendar update status
        viewModel.calendarUpdateCompleted.observe(this, Observer { completed ->
            calendarUpdateCompleted = completed
            if (completed) {
                Toast.makeText(applicationContext, R.string.calendar_update_success, Toast.LENGTH_SHORT).show()
            }
            invalidateOptionsMenu()
        })

        // watch out for refresh status
        viewModel.refreshCompleted.observe(this, Observer { completed ->
            refreshCompleted = completed
            if (completed) {
                Toast.makeText(applicationContext, R.string.toast_refresh_complete, Toast.LENGTH_SHORT).show()
            }
            invalidateOptionsMenu()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.overflow_menu, menu)
        if (!calendarUpdateCompleted || !refreshCompleted)
            menu.getItem(0).isVisible = true
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //return NavigationUI.onNavDestinationSelected(item, findNavController(R.id.navHostFragment)) || super.onOptionsItemSelected(item)
        return when (item.itemId) {
            R.id.action_error -> {
                Toast.makeText(applicationContext, R.string.error_actionbar, Toast.LENGTH_LONG).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.navHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }
}
