package fr.epita.android.pokebattle.webservices.pokeapi

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PokeAPIServiceHelper {

    private val pokeAPIRetrofit : Retrofit = Retrofit.Builder()
        .baseUrl(PokeAPIInterface.Constants.url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val pokeAPIService : PokeAPIInterface = pokeAPIRetrofit.create(PokeAPIInterface::class.java)

}