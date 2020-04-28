package fr.epita.android.pokebattle.webservices.pokeapi.pokemon

import fr.epita.android.pokebattle.webservices.pokeapi.utils.NamedAPIResource

data class PokemonHeldItemVersion(
    var version : NamedAPIResource, // The version in which the item is held.
    var rarity : Int // How often the item is held.
)