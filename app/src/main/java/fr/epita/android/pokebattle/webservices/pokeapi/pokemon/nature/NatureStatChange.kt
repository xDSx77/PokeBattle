package fr.epita.android.pokebattle.webservices.pokeapi.pokemon.nature

import fr.epita.android.pokebattle.webservices.pokeapi.utils.NamedAPIResource

data class NatureStatChange(
    var max_change : Int, // The amount of change.
    var pokeathlon_stat : NamedAPIResource, // The stat being affected.
)
