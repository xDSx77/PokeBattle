package fr.epita.android.pokebattle.webservices.pokeapi.models.moves

import fr.epita.android.pokebattle.webservices.pokeapi.models.utils.NamedAPIResource

data class MoveStatChange(
    var change : Int,
    var stat : NamedAPIResource
)