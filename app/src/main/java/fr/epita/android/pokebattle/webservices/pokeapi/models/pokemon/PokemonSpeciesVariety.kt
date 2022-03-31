package fr.epita.android.pokebattle.webservices.pokeapi.models.pokemon

import fr.epita.android.pokebattle.webservices.pokeapi.models.utils.NamedAPIResource

data class PokemonSpeciesVariety(
    var is_default : Boolean, // Whether this variety is the default variety.
    var pokemon : NamedAPIResource // The Pokémon variety.
)
