package dev.weinsheimer.sportscalendar.di

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Binds
import dagger.Component
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import dev.weinsheimer.sportscalendar.network.RefreshWorker
import javax.inject.Inject
import javax.inject.Provider
import kotlin.reflect.KClass



class DaggerWorkerFactory @Inject constructor(
    private val workerFactories: Map<Class<out ListenableWorker>, @JvmSuppressWildcards Provider<ChildWorkerFactory>>
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParams: WorkerParameters
    ): ListenableWorker? {
        val foundEntry =
            workerFactories.entries.find { Class.forName(workerClassName).isAssignableFrom(it.key) }
        val factoryProvider = foundEntry?.value
            ?: throw IllegalArgumentException("unknown worker class name: $workerClassName")
        return factoryProvider.get().create(appContext, workerParams)
    }
}

interface ChildWorkerFactory {
    fun create(appContext: Context, workerParams: WorkerParameters): ListenableWorker
}




