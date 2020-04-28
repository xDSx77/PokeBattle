package fr.epita.android.pokebattle.webservices.pokeapi.pokemon

import fr.epita.android.pokebattle.webservices.pokeapi.utils.NamedAPIResource

data class PokemonType(
    var slot : Int, // The order the Pokemon's types are listed in.
    var type : NamedAPIResource // The type the referenced Pokemon has.
)