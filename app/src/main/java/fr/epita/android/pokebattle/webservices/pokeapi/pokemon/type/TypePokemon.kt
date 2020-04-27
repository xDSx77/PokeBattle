package fr.epita.android.pokebattle.webservices.pokeapi.pokemon.type

import fr.epita.android.pokebattle.webservices.pokeapi.utils.NamedAPIResource

data class TypePokemon(
    var slot : Int,
    var pokemon : NamedAPIResource
)