package fr.epita.android.pokebattle.webservices

import fr.epita.android.pokebattle.webservices.pokemon.type.Type
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeAPIInterface {
    object Constants {
        @JvmField   // for access in java code
        val url : String = "https://pokeapi.co/api/v2/"
    }

    @GET("pokemon/{id}")
    fun getPokemon(@Path("id") id : Int) : Call<Pokemon>

    @GET("type")
    fun getTypes() : Call<NamedAPIResourceList>

    @GET("type/{name}")
    fun getTypeByName(@Path("name") name : String) : Call<Type>

    @GET("type/{id}")
    fun getTypeById(@Path("id") id : Int) : Call<Type>

    @GET("move-category/damage")
    fun getAllDamageMoves() : Call<MoveCategory>

    @GET("move/{name}")
    fun getMove(@Path("name") name : String) : Call<Move>
}