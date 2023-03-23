package dev.haqim.productdummy.favorites.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dev.haqim.productdummy.di.FavoriteModuleDependencies
import dev.haqim.productdummy.favorites.FavoritesFragment

@Component(dependencies = [FavoriteModuleDependencies::class])
interface FavoritesComponent {

    fun inject(favoriteUserActivity: FavoritesFragment)

    @Component.Builder
    interface Builder {
        fun context(@BindsInstance context: Context) : Builder
        fun appDependencies(favoriteModuleDependencies: FavoriteModuleDependencies) : Builder
        fun build() : FavoritesComponent
    }

}