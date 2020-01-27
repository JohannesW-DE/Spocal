package dev.weinsheimer.sportscalendar.di

import androidx.work.ListenableWorker
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import dev.weinsheimer.sportscalendar.network.RefreshWorker
import kotlin.reflect.KClass

@Module(includes = [AssistedInject_AppAssistedInjectModule::class])
@AssistedModule
abstract class AppAssistedInjectModule

@MapKey
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class WorkerKey(val value: KClass<out ListenableWorker>)

@Module
abstract class WorkerModule {
    @Binds
    @IntoMap
    @WorkerKey(RefreshWorker::class)
    abstract fun refreshWorker(factory: RefreshWorker.Factory): ChildWorkerFactory
}
