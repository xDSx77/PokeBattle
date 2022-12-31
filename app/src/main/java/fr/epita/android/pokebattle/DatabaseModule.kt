package fr.epita.android.pokebattle

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fr.epita.android.pokebattle.webservices.pokeapi.repository.pokemon.PokemonRepository
import fr.epita.android.pokebattle.webservices.pokeapi.repository.pokemon.PokemonSpeciesRepository
import fr.epita.android.pokebattle.webservices.pokeapi.repository.resourcelist.NamedAPIResourceListRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun providesAppDatabase(@ApplicationContext applicationContext : Context) : PokeBattleDatabase {
        return Room.databaseBuilder(
            applicationContext,
            PokeBattleDatabase::class.java,
            Constants.POKEBATTLE_DB
        ).build()
    }

    @Provides
    fun provideNamedAPIResourceListRepository(pokeBattleDatabase : PokeBattleDatabase) : NamedAPIResourceListRepository {
        return pokeBattleDatabase.namedAPIResourceListRepository()
    }

    @Provides
    fun providePokemonSpeciesRepository(pokeBattleDatabase : PokeBattleDatabase) : PokemonSpeciesRepository {
        return pokeBattleDatabase.pokemonSpeciesRepository()
    }

    @Provides
    fun providePokemonRepository(pokeBattleDatabase : PokeBattleDatabase) : PokemonRepository {
        return pokeBattleDatabase.pokemonRepository()
    }
}