package fr.epita.android.pokebattle.webservices.pokeapi.repository.resourcelist

import androidx.room.Dao
import androidx.room.Query
import fr.epita.android.pokebattle.webservices.pokeapi.models.resourcelist.NamedAPIResourceList
import fr.epita.android.pokebattle.webservices.pokeapi.repository.Repository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
abstract class NamedAPIResourceListRepository : Repository<NamedAPIResourceList>() {

    @Query("DELETE FROM NamedAPIResourceList")
    abstract override fun deleteAll() : Completable

    @Query("SELECT * FROM NamedAPIResourceList")
    abstract override fun getAll() : Single<List<NamedAPIResourceList>>

    @Query("SELECT * FROM NamedAPIResourceList WHERE type = :type")
    abstract fun findByType(type : String) : Single<NamedAPIResourceList>
}