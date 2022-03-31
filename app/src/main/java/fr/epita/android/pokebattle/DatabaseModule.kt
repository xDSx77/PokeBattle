package fr.epita.android.pokebattle

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fr.epita.android.pokebattle.webservices.pokeapi.PokeAPIDatabase
import fr.epita.android.pokebattle.webservices.pokeapi.repository.pokemon.PokemonRepository
import fr.epita.android.pokebattle.webservices.pokeapi.repository.pokemon.PokemonSpeciesRepository
import fr.epita.android.pokebattle.webservices.pokeapi.repository.resourcelist.NamedAPIResourceListRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun providesAppDatabase(@ApplicationContext applicationContext : Context) : PokeAPIDatabase {
        return Room.databaseBuilder(
            applicationContext,
            PokeAPIDatabase::class.java,
            Constants.POKEAPI_DB
        ).build()
    }

    @Provides
    fun provideNamedAPIResourceListRepository(pokeAPIDatabase : PokeAPIDatabase) : NamedAPIResourceListRepository {
        return pokeAPIDatabase.namedAPIResourceListRepository()
    }

    @Provides
    fun providePokemonSpeciesRepository(pokeAPIDatabase : PokeAPIDatabase) : PokemonSpeciesRepository {
        return pokeAPIDatabase.pokemonSpeciesRepository()
    }

    @Provides
    fun providePokemonRepository(pokeAPIDatabase : PokeAPIDatabase) : PokemonRepository {
        return pokeAPIDatabase.pokemonRepository()
    }
}