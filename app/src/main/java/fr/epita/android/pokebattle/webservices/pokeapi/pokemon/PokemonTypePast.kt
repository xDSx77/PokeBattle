package fr.epita.android.pokebattle.webservices.pokeapi.pokemon

import fr.epita.android.pokebattle.webservices.pokeapi.utils.NamedAPIResource

data class PokemonTypePast(
    var generation : NamedAPIResource, // The last generation in which the referenced pokémon had the listed types.
    var types : List<PokemonType> // The types the referenced pokémon had up to and including the listed generation.
)