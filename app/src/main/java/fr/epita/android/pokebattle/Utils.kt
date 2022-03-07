package fr.epita.android.pokebattle

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.View
import android.widget.ImageView
import fr.epita.android.pokebattle.enums.Stat
import fr.epita.android.pokebattle.main.MainActivity
import fr.epita.android.pokebattle.webservices.pokeapi.PokeAPIServiceHelper.pokeAPIService
import fr.epita.android.pokebattle.webservices.pokeapi.PokeAPIServiceHelper.pokeApiObserver
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.Pokemon
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.PokemonStat
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

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
    fun loadTypeIntoRightImageView(pokemon : Pokemon, context : Context, type1ImageView : ImageView, type2ImageView : ImageView) {
        val pokemonTypes : List<String> = if (pokemon.types.size == 2) listOf(pokemon.types[0].type.name, pokemon.types[1].type.name) else listOf(pokemon.types[0].type.name)
        type1ImageView.visibility = View.VISIBLE
        if (pokemonTypes.size == 1) {

            type1ImageView.setImageResource(typeToRDrawable(pokemonTypes[0]))
            type1ImageView.setOnClickListener {
                (context as MainActivity).typeHelp(pokemonTypes[0])
            }
            type2ImageView.visibility = View.INVISIBLE
        }
        else if (pokemonTypes.size == 2) {
            type2ImageView.visibility = View.VISIBLE
            type1ImageView.setImageResource(typeToRDrawable(pokemonTypes[0]))
            type1ImageView.setOnClickListener {
                (context as MainActivity).typeHelp(pokemonTypes[0])
            }
            type2ImageView.setImageResource(typeToRDrawable(pokemonTypes[1]))
            type2ImageView.setOnClickListener {
                (context as MainActivity).typeHelp(pokemonTypes[1])
            }
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
    fun getPokemonStat(pokemon : Pokemon, stat : Stat) : PokemonStat {
        return pokemon.stats.find { it.stat.name == stat.statName }!!
    }

    @JvmStatic
    fun buildAllPokemonSpecies(actionWithPokemons : (List<Pokemon>) -> Unit) {
        val pokemonsNames : MutableList<String> = mutableListOf()
        pokeAPIService.getAllPokemonSpecies(Globals.GENERATION.maxIdPokedex)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(pokeApiObserver(
                fun() {
                    Observable.fromIterable(pokemonsNames)
                        .flatMap { pokemonName -> pokeAPIService.getPokemonByName(pokemonName) }
                        .toList()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { pokemons ->
                            val pokemonsSortedById = pokemons.sortedBy { it.id }
                            actionWithPokemons(pokemonsSortedById)
                        }
                },
                fun(pokemonSpecies) {
                    for (pokemonSpecie in pokemonSpecies.results) {
                        pokemonsNames.add(pokemonSpecie.name)
                    }
                }
            ))
    }
}