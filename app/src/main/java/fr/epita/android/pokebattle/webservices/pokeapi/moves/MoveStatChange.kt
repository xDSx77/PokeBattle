package fr.epita.android.pokebattle.webservices.pokeapi.moves

import fr.epita.android.pokebattle.webservices.pokeapi.utils.NamedAPIResource

data class MoveStatChange(
    var change : Int,
    var stat : NamedAPIResource
)