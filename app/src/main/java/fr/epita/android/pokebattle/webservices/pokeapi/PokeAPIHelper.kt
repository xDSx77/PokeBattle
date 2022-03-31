package fr.epita.android.pokebattle.webservices.pokeapi

import android.util.Log
import fr.epita.android.pokebattle.Constants
import fr.epita.android.pokebattle.Constants.WEB_SERVICES
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object PokeAPIHelper {

    private val pokeAPIRetrofit : Retrofit = Retrofit.Builder()
        .baseUrl(PokeAPIInterface.Constants.url)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val pokeAPIInterface : PokeAPIInterface = pokeAPIRetrofit.create(PokeAPIInterface::class.java)

    @JvmStatic
    fun <T : Any> pokeApiObserver(onComplete : () -> Unit = {}, onNext : (T) -> Unit) : Observer<T> {
        return object : Observer<T> {
            override fun onSubscribe(d : Disposable) {
                Log.d(WEB_SERVICES, "PokeAPI Observer correctly subscribed to Observable : $d")
            }

            override fun onError(e : Throwable) {
                Log.e(WEB_SERVICES, "Error with PokeAPI call: $e")
            }

            override fun onNext(t : T) {
                Log.v(WEB_SERVICES, "PokeAPI call success, response is: $t")
                onNext(t)
            }

            override fun onComplete() {
                Log.d(WEB_SERVICES, "PokeAPI all calls complete")
                onComplete()
            }

        }
    }

    @JvmStatic
    fun <T : Any> pokeApiSingleObserver(onError : (Throwable) -> Unit, onSuccess : (T) -> Unit) : SingleObserver<T> {
        return object : SingleObserver<T> {
            override fun onSubscribe(d : Disposable) {
                Log.d(WEB_SERVICES, "PokeAPI Observer correctly subscribed to Observable : $d")
            }

            override fun onError(e : Throwable) {
                Log.e(WEB_SERVICES, "Error with PokeAPI call: $e")
                onError(e)
            }

            override fun onSuccess(t : T) {
                Log.d(WEB_SERVICES, "PokeAPI all calls complete")
                onSuccess(t)
            }

        }
    }
}