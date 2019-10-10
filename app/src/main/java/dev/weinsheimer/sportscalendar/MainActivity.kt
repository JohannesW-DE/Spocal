package dev.weinsheimer.sportscalendar

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.work.*
import com.google.android.material.snackbar.Snackbar
import dev.weinsheimer.sportscalendar.databinding.ActivityMainBinding
import dev.weinsheimer.sportscalendar.network.RefreshWorker
import dev.weinsheimer.sportscalendar.util.asDate
import dev.weinsheimer.sportscalendar.util.asString
import dev.weinsheimer.sportscalendar.viewmodels.SharedViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    val viewModel: SharedViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        drawerLayout = binding.drawerLayout
        val navController = this.findNavController(R.id.navHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navigationView, navController)

        // !!!
        debug()
        // !!!

        // display information/errors/warnings
        viewModel.toast.observe(this, Observer { resourceId ->
            Toast.makeText(this, resourceId, Toast.LENGTH_SHORT).show()
        })

        // watch out for refresh status if the worker was started
        viewModel.refreshWorkInfo?.observe(this, Observer { workInfo ->
            if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                Handler().postDelayed({
                    Toast.makeText(
                        applicationContext,
                        R.string.toast_refresh_complete,
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.updateRefreshDate()
                }, 3000)
            }
        })
    }

    private fun debug() {
        deleteDatabase("spocal")
        with (getSharedPreferences("spocal", Context.MODE_PRIVATE).edit()) {
            clear()
            commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.overflow_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, findNavController(R.id.navHostFragment)) || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.navHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }
}
