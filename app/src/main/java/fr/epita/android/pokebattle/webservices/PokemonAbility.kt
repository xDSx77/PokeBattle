package fr.epita.android.pokebattle.webservices

data class PokemonAbility(
    var is_hidden : Boolean, // Whether or not this is a hidden ability.
    var slot : Int, // The slot this ability occupies in this Pokemon species.
    var ability : NamedAPIResource // The ability the Pokemon may have.
)