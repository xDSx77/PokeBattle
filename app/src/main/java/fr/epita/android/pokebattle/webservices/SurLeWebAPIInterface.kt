package fr.epita.android.pokebattle.webservices

import retrofit2.Call
import retrofit2.http.GET

interface SurLeWebAPIInterface {
    object Constants {
        @JvmField   // for access in java code
        val url : String = "https://www.surleweb.xyz/api/"
    }

    @GET("pokemons.json")
    fun pokemonList() : Call<List<PokedexEntry>>
}