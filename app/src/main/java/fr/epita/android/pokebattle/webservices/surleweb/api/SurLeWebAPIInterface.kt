package fr.epita.android.pokebattle.webservices.surleweb.api

import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET

interface SurLeWebAPIInterface {
    object Constants {
        const val url : String = "https://www.surleweb.xyz/"
    }

    @GET("api/pokemons.json")
    fun pokemonList() : Observable<List<PokedexEntry>>
}