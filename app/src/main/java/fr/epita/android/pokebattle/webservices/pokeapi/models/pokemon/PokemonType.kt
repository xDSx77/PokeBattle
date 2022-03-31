package fr.epita.android.pokebattle.webservices.pokeapi.models.pokemon

import fr.epita.android.pokebattle.webservices.pokeapi.models.utils.NamedAPIResource

data class PokemonType(
    var slot : Int, // The order the Pokemon's types are listed in.
    var type : NamedAPIResource // The type the referenced Pokemon has.
)