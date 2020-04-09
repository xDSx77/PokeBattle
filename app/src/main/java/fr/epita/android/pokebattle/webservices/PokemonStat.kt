package fr.epita.android.pokebattle.webservices

data class PokemonStat(
    var stat : NamedAPIResource, // The stat the Pokemon has.
    var effort : Int, // The effort points (EV) the Pokemon has in the stat.
    var base_stat : Int // The base value of the stat.
)