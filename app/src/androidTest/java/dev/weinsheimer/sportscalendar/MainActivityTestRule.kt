package dev.weinsheimer.sportscalendar

import android.content.Context
import androidx.core.content.edit
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import dev.weinsheimer.sportscalendar.database.SpocalDB
import dev.weinsheimer.sportscalendar.di.databaseTestModule
import dev.weinsheimer.sportscalendar.util.Constants
import dev.weinsheimer.sportscalendar.util.asString
import org.koin.core.KoinComponent
import org.koin.core.context.loadKoinModules
import org.koin.core.inject
import org.koin.test.inject
import timber.log.Timber
import java.util.*

class MainActivityTestRule :
    ActivityTestRule<MainActivity>(MainActivity::class.java, true, false), KoinComponent {

    private val calendar = Calendar.getInstance()

    override fun beforeActivityLaunched() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        prepareSharedPreferences(context)
        super.beforeActivityLaunched()
    }

    private fun prepareSharedPreferences(context: Context) {
        context.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE).edit(commit = true) {
            putString("refreshDate", calendar.time.asString())
            putString("lastStart", calendar.time.asString())
        }
    }

}