package fr.epita.android.pokebattle.webservices.pokeapi.models.pokemon.nature

import fr.epita.android.pokebattle.webservices.pokeapi.models.utils.NamedAPIResource

data class NatureStatChange(
    var max_change : Int, // The amount of change.
    var pokeathlon_stat : NamedAPIResource, // The stat being affected.
)
