package fr.epita.android.pokebattle.webservices.surleweb.api

import android.util.Log
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object SurLeWebServiceHelper {

    private val surLeWebRetrofit : Retrofit = Retrofit.Builder()
        .baseUrl(SurLeWebAPIInterface.Constants.url)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val surLeWebService : SurLeWebAPIInterface = surLeWebRetrofit.create(SurLeWebAPIInterface::class.java)

    @JvmStatic
    fun <T : Any> surLeWebAPIObserver(onComplete : () -> Unit = {}, onNext : (T) -> Unit) : Observer<T> {
        return object : Observer<T> {
            override fun onSubscribe(d : Disposable) {
                Log.w("WebServices", "SurLeWebAPI Observer correctly subscribed to Observable : $d")
            }

            override fun onError(e : Throwable) {
                Log.w("WebServices", "Error with SurLeWebAPI call: $e")
            }

            override fun onNext(t : T) {
                Log.w("WebServices", "SurLeWebAPI call success, response is: $t")
                onNext(t)
            }

            override fun onComplete() {
                Log.w("WebServices", "SurLeWebAPI all calls complete")
                onComplete()
            }

        }
    }
}