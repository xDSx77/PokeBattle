package fr.epita.android.pokebattle.webservices.pokeapi

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fr.epita.android.pokebattle.webservices.pokeapi.models.pokemon.Pokemon
import fr.epita.android.pokebattle.webservices.pokeapi.models.pokemon.PokemonSpecies
import fr.epita.android.pokebattle.webservices.pokeapi.models.resourcelist.NamedAPIResourceList
import fr.epita.android.pokebattle.webservices.pokeapi.repository.pokemon.PokemonRepository
import fr.epita.android.pokebattle.webservices.pokeapi.repository.pokemon.PokemonSpeciesRepository
import fr.epita.android.pokebattle.webservices.pokeapi.repository.resourcelist.NamedAPIResourceListRepository

@Database(entities = [NamedAPIResourceList::class, PokemonSpecies::class, Pokemon::class], version = 1, exportSchema = false)
@TypeConverters(PokeAPIConverters::class)
abstract class PokeAPIDatabase : RoomDatabase() {
    abstract fun namedAPIResourceListRepository() : NamedAPIResourceListRepository
    abstract fun pokemonSpeciesRepository() : PokemonSpeciesRepository
    abstract fun pokemonRepository() : PokemonRepository
}