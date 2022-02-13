package fr.epita.android.pokebattle.webservices.pokeapi.pokemon

import fr.epita.android.pokebattle.webservices.pokeapi.utils.NamedAPIResource

data class PokemonStat(
    var stat : NamedAPIResource, // The stat the Pokemon has.
    var effort : Int, // The effort points (EV) the Pokemon has in the stat.
    var iv: Int, // The individual value (IV) the Pokemon has in the stat.
    var base_stat : Int, // The base value of the stat.
    var modified_stat : Double // The value of the state, modified with the nature.
)