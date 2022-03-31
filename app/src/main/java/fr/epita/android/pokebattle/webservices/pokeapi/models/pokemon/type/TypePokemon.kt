package fr.epita.android.pokebattle.webservices.pokeapi.models.pokemon.type

import fr.epita.android.pokebattle.webservices.pokeapi.models.utils.NamedAPIResource

data class TypePokemon(
    var slot : Int,
    var pokemon : NamedAPIResource
)