package fr.epita.android.pokebattle.webservices.surleweb.api

import retrofit2.Call
import retrofit2.http.GET

interface SurLeWebAPIInterface {
    object Constants {
        const val url : String = "https://www.surleweb.xyz/api/"
    }

    @GET("pokemons.json")
    fun pokemonList() : Call<List<PokedexEntry>>
}