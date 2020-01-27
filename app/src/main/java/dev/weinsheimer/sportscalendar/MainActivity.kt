package dev.weinsheimer.sportscalendar

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.work.Configuration
import androidx.work.WorkInfo
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import dev.weinsheimer.sportscalendar.databinding.ActivityMainBinding
import dev.weinsheimer.sportscalendar.viewmodels.SharedViewModel
import javax.inject.Inject


class MainActivity : DaggerAppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<SharedViewModel> { viewModelFactory }

    override fun onDestroy() {
        super.onDestroy()
        println("MAINACTIVITY IS DESTROYED")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        drawerLayout = binding.drawerLayout
        val navController = this.findNavController(R.id.navHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navigationView, navController)



        // !!!
        //debug()
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
