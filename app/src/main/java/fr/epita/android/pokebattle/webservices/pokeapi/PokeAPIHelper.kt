package fr.epita.android.pokebattle.webservices.pokeapi

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
}