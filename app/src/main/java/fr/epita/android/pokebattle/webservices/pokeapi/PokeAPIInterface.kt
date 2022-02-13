package fr.epita.android.pokebattle.webservices.pokeapi

import fr.epita.android.pokebattle.webservices.pokeapi.moves.Move
import fr.epita.android.pokebattle.webservices.pokeapi.moves.MoveCategory
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.Pokemon
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.nature.Nature
import fr.epita.android.pokebattle.webservices.pokeapi.resourcelist.NamedAPIResourceList
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.type.Type
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeAPIInterface {
    object Constants {
        const val url : String = "https://pokeapi.co/api/v2/"
    }

    @GET("pokemon/{id}")
    fun getPokemon(@Path("id") id : Int) : Call<Pokemon>

    @GET("type")
    fun getTypes() : Call<NamedAPIResourceList>

    @GET("type/{name}")
    fun getTypeByName(@Path("name") name : String) : Call<Type>

    @GET("move-category/damage")
    fun getAllDamageMoves() : Call<MoveCategory>

    @GET("move/{name}")
    fun getMove(@Path("name") name : String) : Call<Move>

    @GET("nature")
    fun getNatures() : Call<NamedAPIResourceList>

    @GET("nature/{name}/?limit=30")
    fun getNatureByName(@Path("name") name : String) : Call<Nature>
}