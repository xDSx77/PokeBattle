package fr.epita.android.pokebattle.webservices

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeAPIInterface {
    object Constants {
        @JvmField   // for access in java code
        val url : String = "https://pokeapi.co/api/v2/"
    }

    @GET("pokemon/{id}")
    fun getPokemon(@Path("id") id : Int) : Call<Pokemon>

}