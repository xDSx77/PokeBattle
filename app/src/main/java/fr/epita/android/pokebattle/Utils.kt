package fr.epita.android.pokebattle

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.widget.ImageView
import fr.epita.android.pokebattle.enums.Stat
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.Pokemon
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.PokemonStat
import fr.epita.android.pokebattle.webservices.surleweb.api.PokedexEntry

object Utils {

    @JvmStatic
    fun firstLetterUpperCase(string : String) : String {
        return string.substring(0, 1).uppercase()
            .plus(string.substring(1))
    }

    @JvmStatic
    fun typeToRDrawable(typeName : String) : Int {
        when (typeName) {
            "bug" -> return R.drawable.bug
            "dark" -> return R.drawable.dark
            "dragon" -> return R.drawable.dragon
            "electric" -> return R.drawable.electric
            "fairy" -> return R.drawable.fairy
            "fighting" -> return R.drawable.fighting
            "fire" -> return R.drawable.fire
            "flying" -> return R.drawable.flying
            "ghost" -> return R.drawable.ghost
            "grass" -> return R.drawable.grass
            "ground" -> return R.drawable.ground
            "ice" -> return R.drawable.ice
            "normal" -> return R.drawable.normal
            "poison" -> return R.drawable.poison
            "psychic" -> return R.drawable.psychic
            "rock" -> return R.drawable.rock
            "steel" -> return R.drawable.steel
            "water" -> return R.drawable.water
            else -> return -1
        }
    }

    @JvmStatic
    fun greyImage(img : ImageView) {
        val matrix = ColorMatrix()
        matrix.setSaturation(0f) // 0 means grayscale
        val cf = ColorMatrixColorFilter(matrix)
        img.colorFilter = cf
        img.imageAlpha = 128 // 128 = 0.5
    }

    @JvmStatic
    fun filterPokedexEntriesByGeneration(pokedexEntries : List<PokedexEntry>, filteredPokedexEntries : ArrayList<PokedexEntry>) {
        filteredPokedexEntries.addAll(pokedexEntries.filter { p -> p.id <= Globals.GENERATION.maxIdPokedex })
    }

    @JvmStatic
    fun getPokemonStat(pokemon : Pokemon, stat : Stat) : PokemonStat {
        return pokemon.stats.find { it.stat.name == stat.statName }!!
    }
}