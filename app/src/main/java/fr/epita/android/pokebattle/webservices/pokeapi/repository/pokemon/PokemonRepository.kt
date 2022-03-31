package fr.epita.android.pokebattle.webservices.pokeapi.repository.pokemon

import androidx.room.Dao
import androidx.room.Query
import fr.epita.android.pokebattle.webservices.pokeapi.models.pokemon.Pokemon
import fr.epita.android.pokebattle.webservices.pokeapi.repository.Repository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
abstract class PokemonRepository : Repository<Pokemon>() {

    @Query("DELETE FROM Pokemon")
    abstract override fun deleteAll() : Completable

    @Query("SELECT * FROM Pokemon")
    abstract override fun getAll() : Single<List<Pokemon>>

    @Query("SELECT * FROM Pokemon WHERE name = :name")
    abstract fun findByName(name : String) : Single<Pokemon>

    @Query("SELECT * FROM Pokemon WHERE id = :id")
    abstract fun findById(id : Int) : Single<Pokemon>
}