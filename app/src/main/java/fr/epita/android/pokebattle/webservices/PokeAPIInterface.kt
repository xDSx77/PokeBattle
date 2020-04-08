package fr.epita.android.pokebattle.webservices

import retrofit2.Call
import retrofit2.http.GET

interface PokeAPIInterface {
    object Constants {
        @JvmField   // for access in java code
        val url: String = "https://pokeapi.co/api/v2/"
    }
}