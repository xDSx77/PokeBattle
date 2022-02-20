package fr.epita.android.pokebattle.webservices.pokeapi

import fr.epita.android.pokebattle.webservices.pokeapi.moves.Move
import fr.epita.android.pokebattle.webservices.pokeapi.moves.MoveCategory
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.Pokemon
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.nature.Nature
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.type.Type
import fr.epita.android.pokebattle.webservices.pokeapi.resourcelist.NamedAPIResourceList
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeAPIInterface {
    object Constants {
        const val url : String = "https://pokeapi.co/"
        const val baseEndpoint : String = "/api/v2"
    }

    @GET("${Constants.baseEndpoint}/pokemon/{id}")
    fun getPokemon(@Path("id") id : Int) : Observable<Pokemon>

    @GET("${Constants.baseEndpoint}/type")
    fun getTypes() : Observable<NamedAPIResourceList>

    @GET("${Constants.baseEndpoint}/type/{name}")
    fun getTypeByName(@Path("name") name : String) : Observable<Type>

    @GET("${Constants.baseEndpoint}/move-category/damage")
    fun getAllDamageMoves() : Observable<MoveCategory>

    @GET("${Constants.baseEndpoint}/move/{name}")
    fun getMove(@Path("name") name : String) : Observable<Move>

    @GET("${Constants.baseEndpoint}/nature")
    fun getNatures() : Observable<NamedAPIResourceList>

    @GET("${Constants.baseEndpoint}/nature/{name}/?limit=30")
    fun getNatureByName(@Path("name") name : String) : Observable<Nature>
}