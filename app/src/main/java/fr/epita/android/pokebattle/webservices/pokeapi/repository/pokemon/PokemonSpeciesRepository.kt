package fr.epita.android.pokebattle.webservices.pokeapi.repository.pokemon

import androidx.room.Dao
import androidx.room.Query
import fr.epita.android.pokebattle.webservices.pokeapi.models.pokemon.PokemonSpecies
import fr.epita.android.pokebattle.webservices.pokeapi.repository.Repository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
abstract class PokemonSpeciesRepository : Repository<PokemonSpecies>() {

    @Query("DELETE FROM PokemonSpecies")
    abstract override fun deleteAll() : Completable

    @Query("SELECT * FROM PokemonSpecies")
    abstract override fun getAll() : Single<List<PokemonSpecies>>

    @Query("SELECT * FROM PokemonSpecies WHERE name = :name")
    abstract fun findByName(name : String) : Single<PokemonSpecies>
}