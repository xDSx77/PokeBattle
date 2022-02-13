package fr.epita.android.pokebattle.battle

import fr.epita.android.pokebattle.enums.NonVolatileStatusEffect
import fr.epita.android.pokebattle.webservices.pokeapi.moves.Move
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.Pokemon
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.nature.Nature

data class PokemonInfo(
    var pokemon : Pokemon?,
    var nature : Nature?,
    var moves : MutableList<Move?>,
    var hp : Int,
    var accuracy : Int,
    var evasion : Int,
    var currentNonVolatileStatus : NonVolatileStatusEffect?
)