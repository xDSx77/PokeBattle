package fr.epita.android.pokebattle.webservices.pokeapi.resourcelist

import fr.epita.android.pokebattle.webservices.pokeapi.utils.NamedAPIResource

data class NamedAPIResourceList(
    var count : Int,
    var next : String,
    var previous : Boolean,
    var results : List<NamedAPIResource>
)