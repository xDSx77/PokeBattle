package fr.epita.android.pokebattle.webservices.pokeapi.models.pokemon

import fr.epita.android.pokebattle.webservices.pokeapi.models.utils.NamedAPIResource

data class PokemonMove(
    var move : NamedAPIResource, // The move the Pokemon can learn.
    var version_group_details : List<PokemonMoveVersion> // The details of the version in which the Pokemon can learn the move.
)