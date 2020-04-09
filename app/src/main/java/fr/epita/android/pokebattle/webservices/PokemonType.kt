package fr.epita.android.pokebattle.webservices

data class PokemonType(
    var slot : Int, // The order the Pokemon's types are listed in.
    var type : NamedAPIResource // The type the referenced Pokemon has.
)