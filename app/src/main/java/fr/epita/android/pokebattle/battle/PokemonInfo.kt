package fr.epita.android.pokebattle.battle

import fr.epita.android.pokebattle.Globals
import fr.epita.android.pokebattle.enums.NonVolatileStatusEffect
import fr.epita.android.pokebattle.webservices.pokeapi.models.moves.Move
import fr.epita.android.pokebattle.webservices.pokeapi.models.pokemon.Pokemon
import fr.epita.android.pokebattle.webservices.pokeapi.models.pokemon.PokemonType
import fr.epita.android.pokebattle.webservices.pokeapi.models.pokemon.nature.Nature

data class PokemonInfo(
    var pokemon : Pokemon? = null,
    var types : MutableList<PokemonType> = mutableListOf(),
    var level : Int = Globals.POKEMON_LEVEL,
    var nature : Nature? = null,
    var moves : MutableList<Move?> = mutableListOf(),
    var hp : Int = 0,
    var accuracy : Int = 0,
    var evasion : Int = 0,
    var currentNonVolatileStatus : NonVolatileStatusEffect? = null,
    var numberTurnStatusRemaining : Int = 0
)