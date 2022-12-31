package fr.epita.android.pokebattle

import android.util.Log
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable

object ObserverHelper {

    @JvmStatic
    fun <T : Any> pokeApiObserver(onComplete : () -> Unit = {}, onNext : (T) -> Unit) : Observer<T> {
        return object : Observer<T> {
            override fun onSubscribe(d : Disposable) {
                Log.d(Constants.WEB_SERVICES, "PokeAPI Observer correctly subscribed to Observable : $d")
            }

            override fun onError(e : Throwable) {
                Log.e(Constants.WEB_SERVICES, "Error with PokeAPI call: $e")
            }

            override fun onNext(t : T) {
                Log.v(Constants.WEB_SERVICES, "PokeAPI call success, response is: $t")
                onNext(t)
            }

            override fun onComplete() {
                Log.d(Constants.WEB_SERVICES, "PokeAPI all calls complete")
                onComplete()
            }

        }
    }

    @JvmStatic
    fun <T : Any> pokeBattleDbSingleObserver(onError : (Throwable) -> Unit, onSuccess : (T) -> Unit) : SingleObserver<T> {
        return object : SingleObserver<T> {
            override fun onSubscribe(d : Disposable) {
                Log.d(Constants.POKEBATTLE_DATABASE, "PokeBattleDb Observer correctly subscribed to Observable : $d")
            }

            override fun onError(e : Throwable) {
                Log.e(Constants.POKEBATTLE_DATABASE, "Error with PokeBattleDb call: $e")
                onError(e)
            }

            override fun onSuccess(t : T) {
                Log.d(Constants.POKEBATTLE_DATABASE, "PokeBattleDb all calls complete")
                onSuccess(t)
            }

        }
    }
}