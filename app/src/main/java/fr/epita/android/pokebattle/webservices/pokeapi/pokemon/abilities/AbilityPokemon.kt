package fr.epita.android.pokebattle.webservices.pokeapi.pokemon.abilities

import fr.epita.android.pokebattle.webservices.pokeapi.utils.NamedAPIResource

data class AbilityPokemon(
    var is_hidden : Boolean, // Whether or not this is a hidden ability.
    var slot : Int, // The slot this ability occupies in this Pokemon species.
    var ability : NamedAPIResource // The ability the Pokemon may have.
)