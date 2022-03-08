package fr.epita.android.pokebattle.webservices.pokeapi.pokemon

import fr.epita.android.pokebattle.webservices.pokeapi.utils.NamedAPIResource

data class PokemonSpeciesVariety(
    var is_default : Boolean, // Whether this variety is the default variety.
    var pokemon : NamedAPIResource // The Pokémon variety.
)
