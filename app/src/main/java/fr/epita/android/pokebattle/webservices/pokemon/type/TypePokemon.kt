package fr.epita.android.pokebattle.webservices.pokemon.type

import fr.epita.android.pokebattle.webservices.NamedAPIResource

data class TypePokemon(
    var slot : Int,
    var pokemon : NamedAPIResource
)