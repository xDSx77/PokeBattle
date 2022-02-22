package fr.epita.android.pokebattle.battle

import fr.epita.android.pokebattle.Globals
import fr.epita.android.pokebattle.Utils
import fr.epita.android.pokebattle.enums.NonVolatileStatusEffect
import fr.epita.android.pokebattle.enums.Stat
import fr.epita.android.pokebattle.webservices.pokeapi.moves.Move
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.PokemonStat
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.PokemonType
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.type.TypeRelations
import fr.epita.android.pokebattle.webservices.pokeapi.utils.NamedAPIResource
import kotlin.math.max
import kotlin.math.min

object DamageHelper {

    var allMovesTypesDamageRelations = mutableMapOf<String, TypeRelations>()

    @JvmStatic
    fun getEfficiency(moveType : NamedAPIResource, pokemonTypes : List<PokemonType>) : Double {
        var efficiency = 1.0
        val moveTypesDamageRelations = allMovesTypesDamageRelations[moveType.name]!!
        for (pokemonType in pokemonTypes) {
            if (moveTypesDamageRelations.no_damage_to.any { it.name == pokemonType.type.name })
                efficiency *= 0.0
            if (moveTypesDamageRelations.double_damage_to.any { it.name == pokemonType.type.name })
                efficiency *= 2.0
            if (moveTypesDamageRelations.half_damage_to.any { it.name == pokemonType.type.name })
                efficiency *= 0.5
        }
        return efficiency
    }

    @JvmStatic
    fun moveHit(move : Move, attacker : PokemonInfo, defender : PokemonInfo) : Boolean {
        // see https://bulbapedia.bulbagarden.net/wiki/Accuracy + https://bulbapedia.bulbagarden.net/wiki/Stat#Stage_multipliers
        val otherMods = 1.00
        val generation = Globals.GENERATION.generation
        val stageAccuracyMultiplier : Double = when (min(max(attacker.accuracy - defender.evasion, -6), +6)) {
            -6 -> if (generation <= 4) 0.33 else 1.00 / 3.00
            -5 -> if (generation <= 4) 0.36 else 3.00 / 8.00
            -4 -> if (generation <= 4) 0.43 else 3.00 / 7.00
            -3 -> 0.50
            -2 -> 0.60
            -1 -> 0.75
            0 -> 1.00
            +1 -> if (generation <= 4) 1.33 else 4.00 / 3.00
            +2 -> if (generation <= 4) 1.66 else 5.00 / 3.00
            +3 -> 2.00
            +4 -> if (generation <= 2) 2.33 else if (generation in (3..4)) 2.50 else 7.00 / 3.00
            +5 -> if (generation <= 4) 2.66 else 8.00 / 3.00
            +6 -> if (generation <= 4) 3.00 else 9.00 / 3.00
            else -> 1.00
        }
        val threshold = move.accuracy * stageAccuracyMultiplier * otherMods
        val random = (1..100).random()
        return random < threshold
    }

    @JvmStatic
    fun criticalGen1(speed : PokemonStat) : Boolean {
        // see https://bulbapedia.bulbagarden.net/wiki/Critical_hit in generation 1
        val threshold : Int = speed.modified_stat / 2
        val random = (0..255).random()
        return random < threshold
    }

    @JvmStatic
    fun critical() : Boolean {
        // see https://bulbapedia.bulbagarden.net/wiki/Critical_hit generation 2 onwards
        val threshold = 1
        val random = (1..16).random()
        return random == threshold
    }

    @JvmStatic
    fun computeDamage(attacker : PokemonInfo, defender : PokemonInfo, move : Move, critical : Boolean, efficiency : Double) : Number {
        // calculation based on https://bulbapedia.bulbagarden.net/wiki/Damage
        val targets = 1
        val weather = 1
        val badge = 1
        val generation = Globals.GENERATION.generation
        val criticalValue : Double = if (critical && generation in (2..5)) 2.0 else if (critical && generation >= 6) 1.5 else 1.0
        val random : Double = if (generation in (1..2)) ((217..255).random()/255).toDouble() else (85..101).random().toDouble() / 100
        val stab : Double = if (move.type in attacker.pokemon!!.types.map { t -> t.type }) 1.5 else 1.0
        val burn : Double = if (generation >= 3 && attacker.currentNonVolatileStatus == NonVolatileStatusEffect.BURN && move.damage_class.name == "physical") 0.5 else 1.0
        val other = 1

        val modifier : Double = targets * weather * badge * criticalValue * random * stab * efficiency * burn * other

        val (att : Int, def : Int) = when (move.damage_class.name) {
            "physical" -> Pair(
                Utils.getPokemonStat(attacker.pokemon!!, Stat.ATTACK).modified_stat,
                Utils.getPokemonStat(defender.pokemon!!, Stat.DEFENSE).modified_stat
            )
            "special" -> Pair(
                Utils.getPokemonStat(attacker.pokemon!!, Stat.SPECIAL_ATTACK).modified_stat,
                Utils.getPokemonStat(defender.pokemon!!, Stat.SPECIAL_DEFENSE).modified_stat
            )
            else -> Pair(0, 1)
        }

        val level = if (generation == 1 && critical) attacker.level * 2 else attacker.level
        return (((((2 * level) / 5) + 2 * move.power!! * (att / def)) / 50) + 2) * modifier
    }

}