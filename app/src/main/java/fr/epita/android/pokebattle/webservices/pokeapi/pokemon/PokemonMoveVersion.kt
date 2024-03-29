package fr.epita.android.pokebattle.webservices.pokeapi.pokemon

import fr.epita.android.pokebattle.webservices.pokeapi.utils.NamedAPIResource

data class PokemonMoveVersion(
    var move_learn_method : NamedAPIResource, // The method by which the move is learned.
    var version_group : NamedAPIResource, // The version group in which the move is learned.
    var level_learned_at : Int // The version group in which the move is learned.
)