package fr.epita.android.pokebattle.battle

import fr.epita.android.pokebattle.Globals
import fr.epita.android.pokebattle.enums.NonVolatileStatusEffect
import fr.epita.android.pokebattle.webservices.pokeapi.moves.Move
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.Pokemon
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.nature.Nature

data class PokemonInfo(
    var pokemon : Pokemon? = null,
    var level : Int = Globals.POKEMON_LEVEL,
    var nature : Nature? = null,
    var moves : MutableList<Move?> = mutableListOf(),
    var hp : Int = 0,
    var accuracy : Int = 0,
    var evasion : Int = 0,
    var currentNonVolatileStatus : NonVolatileStatusEffect? = null
)