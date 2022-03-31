package fr.epita.android.pokebattle.webservices.pokeapi.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
abstract class Repository<T : Any> {

    @Insert
    abstract fun insertAll(vararg entities : T) : Completable

    @Insert
    abstract fun insertAll(entities : List<T>) : Completable

    @Insert
    abstract fun insert(entity : T) : Completable

    abstract fun deleteAll() : Completable

    @Delete
    abstract fun deleteAll(vararg entities : T) : Completable

    @Delete
    abstract fun deleteAll(entities : List<T>) : Completable

    @Delete
    abstract fun delete(entity : T) : Completable

    abstract fun getAll() : Single<List<T>>

}