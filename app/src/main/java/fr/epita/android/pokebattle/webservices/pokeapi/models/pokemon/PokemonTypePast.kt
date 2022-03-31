package fr.epita.android.pokebattle.webservices.pokeapi.models.pokemon

import fr.epita.android.pokebattle.webservices.pokeapi.models.utils.NamedAPIResource

data class PokemonTypePast(
    var generation : NamedAPIResource, // The last generation in which the referenced pokémon had the listed types.
    var types : List<PokemonType> // The types the referenced pokémon had up to and including the listed generation.
)