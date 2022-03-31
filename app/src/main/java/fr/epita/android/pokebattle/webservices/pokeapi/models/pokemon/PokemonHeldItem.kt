package fr.epita.android.pokebattle.webservices.pokeapi.models.pokemon

import fr.epita.android.pokebattle.webservices.pokeapi.models.utils.NamedAPIResource

data class PokemonHeldItem(
    var item : NamedAPIResource, // The item the referenced Pokemon holds.
    var version_details : List<PokemonHeldItemVersion> // The details of the different versions in which the item is held.
)