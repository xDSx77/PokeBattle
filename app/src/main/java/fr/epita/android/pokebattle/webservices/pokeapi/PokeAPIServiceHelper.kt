package fr.epita.android.pokebattle.webservices.pokeapi

import android.util.Log
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object PokeAPIServiceHelper {

    private val pokeAPIRetrofit : Retrofit = Retrofit.Builder()
        .baseUrl(PokeAPIInterface.Constants.url)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val pokeAPIService : PokeAPIInterface = pokeAPIRetrofit.create(PokeAPIInterface::class.java)

    @JvmStatic
    fun <T : Any> pokeApiObserver(onComplete : () -> Unit = {}, onNext : (T) -> Unit) : Observer<T> {
        return object : Observer<T> {
            override fun onSubscribe(d : Disposable) {
                Log.d("WebServices", "PokeAPI Observer correctly subscribed to Observable : $d")
            }

            override fun onError(e : Throwable) {
                Log.e("WebServices", "Error with PokeAPI call: $e")
            }

            override fun onNext(t : T) {
                Log.v("WebServices", "PokeAPI call success, response is: $t")
                onNext(t)
            }

            override fun onComplete() {
                Log.d("WebServices", "PokeAPI all calls complete")
                onComplete()
            }

        }
    }

}