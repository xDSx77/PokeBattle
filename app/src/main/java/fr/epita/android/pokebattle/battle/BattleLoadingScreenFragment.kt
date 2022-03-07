package fr.epita.android.pokebattle.battle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.epita.android.pokebattle.Globals
import fr.epita.android.pokebattle.R
import fr.epita.android.pokebattle.Utils
import fr.epita.android.pokebattle.battle.BattleFragment.Companion.opponentPokemon1
import fr.epita.android.pokebattle.battle.BattleFragment.Companion.opponentPokemon2
import fr.epita.android.pokebattle.battle.BattleFragment.Companion.opponentPokemon3
import fr.epita.android.pokebattle.battle.BattleFragment.Companion.pokemon1
import fr.epita.android.pokebattle.battle.BattleFragment.Companion.pokemon2
import fr.epita.android.pokebattle.battle.BattleFragment.Companion.pokemon3
import fr.epita.android.pokebattle.enums.Stat
import fr.epita.android.pokebattle.main.MainActivity
import fr.epita.android.pokebattle.webservices.pokeapi.PokeAPIServiceHelper
import fr.epita.android.pokebattle.webservices.pokeapi.moves.Move
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.Pokemon
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.PokemonMove
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.PokemonStat
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.nature.Nature
import fr.epita.android.pokebattle.webservices.pokeapi.utils.NamedAPIResource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlin.math.min


class BattleLoadingScreenFragment : Fragment() {

    companion object {
        val allNaturesList : MutableList<Nature> = mutableListOf()
    }

    private val allDamageMovesList : MutableList<NamedAPIResource> = mutableListOf()
    private val all6PokemonMovesList : MutableList<Move> = mutableListOf()

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_battle_loading_screen, container, false)
    }

    private fun setPokemonModifiedStats(pokemonInfo : PokemonInfo) {
        val pokemon : Pokemon = pokemonInfo.pokemon!!
        val decreasedStat = pokemon.stats.find { it.stat == pokemonInfo.nature!!.decreased_stat }
        val increasedStat = pokemon.stats.find { it.stat == pokemonInfo.nature!!.increased_stat }
        // https://bulbapedia.bulbagarden.net/wiki/Effort_values#Generation_III
        var maxEv = Globals.GENERATION.maxEvPerStat
        for (stat : PokemonStat in pokemon.stats.shuffled()) {
            // https://bulbapedia.bulbagarden.net/wiki/Individual_values#Generation_III_onward
            stat.iv = (0..31).random()
            val ev = min((0..maxEv).random(), 255)
            stat.effort = ev
            maxEv -= ev
            // https://bulbapedia.bulbagarden.net/wiki/Stat#Generation_III_onward
            val commonPart : Int = (((2 * stat.base_stat + stat.iv + stat.effort / 4) * pokemonInfo.level) / 100)
            if (stat.stat.name == Stat.HP.statName) {
                stat.modified_stat = commonPart + pokemonInfo.level + 10
            } else {
                stat.modified_stat = commonPart + 5
                // https://bulbapedia.bulbagarden.net/wiki/Nature
                if (decreasedStat != null && stat.stat.name == decreasedStat.stat.name) {
                    stat.modified_stat = (stat.modified_stat * 0.9).toInt()
                } else if (increasedStat != null && stat.stat.name == increasedStat.stat.name) {
                    stat.modified_stat = (stat.modified_stat * 1.1).toInt()
                }
            }
        }
    }

    private fun buildPokemon(pokemonId : Int, pokemonInfo : PokemonInfo) {
        PokeAPIServiceHelper.pokeAPIService.getPokemonById(pokemonId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(PokeAPIServiceHelper.pokeApiObserver { pokemon ->
                pokemonInfo.pokemon = pokemon
                pokemonInfo.moves = mutableListOf()
                pokemonInfo.nature = allNaturesList.random()
                setPokemonModifiedStats(pokemonInfo)
                val pokemonHp = Utils.getPokemonStat(pokemon, Stat.HP).modified_stat
                pokemonInfo.hp = pokemonHp
                val pokemonDamageMoves : List<NamedAPIResource> = pokemon.moves
                    .shuffled()
                    .map(PokemonMove::move)
                    .intersect(allDamageMovesList)
                    .toList()
                Observable.merge(
                    PokeAPIServiceHelper.pokeAPIService.getMove(pokemonDamageMoves[0].name),
                    PokeAPIServiceHelper.pokeAPIService.getMove(pokemonDamageMoves[1].name),
                    PokeAPIServiceHelper.pokeAPIService.getMove(pokemonDamageMoves[2].name),
                    PokeAPIServiceHelper.pokeAPIService.getMove(pokemonDamageMoves[3].name)
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(PokeAPIServiceHelper.pokeApiObserver(
                        fun() {
                            if (all6PokemonMovesList.size == 24) {
                                (activity as MainActivity).battle()
                            }
                        },
                        fun(move) {
                            pokemonInfo.moves.add(move)
                            all6PokemonMovesList.add(move)
                        }
                    ))
            }
            )
    }

    private fun buildAllDamageMovesListAndPokemons(opponentPokemonIds : List<Int>, playerPokemonIds : List<Int>) {
        PokeAPIServiceHelper.pokeAPIService.getAllDamageMoves()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(PokeAPIServiceHelper.pokeApiObserver { damageMoveCategory ->
                allDamageMovesList.addAll(damageMoveCategory.moves.filterIndexed { index, _ ->
                    index <= Globals.GENERATION.maxIdMove
                })
                // 1st opponent pokémon
                buildPokemon(opponentPokemonIds[0], opponentPokemon1)
                // 2nd opponent pokémon
                buildPokemon(opponentPokemonIds[1], opponentPokemon2)
                // 3rd opponent pokémon
                buildPokemon(opponentPokemonIds[2], opponentPokemon3)

                // 1st player pokémon
                buildPokemon(playerPokemonIds[0], pokemon1)
                // 2nd player pokémon
                buildPokemon(playerPokemonIds[1], pokemon2)
                // 3rd player pokémon
                buildPokemon(playerPokemonIds[2], pokemon3)

            })
    }

    private fun buildTypesDamageRelations() {
        PokeAPIServiceHelper.pokeAPIService.getTypes()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(PokeAPIServiceHelper.pokeApiObserver { typesResponse ->
                for (typeResource in typesResponse.results) {
                    PokeAPIServiceHelper.pokeAPIService.getTypeByName(typeResource.name)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(PokeAPIServiceHelper.pokeApiObserver { type ->
                            DamageHelper.allMovesTypesDamageRelations[type.name] = type.damage_relations
                        })
                }
            })
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { bundle ->
            val opponentPokemonIds =
                listOf(bundle.getInt("opponentPokemon0"), bundle.getInt("opponentPokemon1"), bundle.getInt("opponentPokemon2"))
            val playerPokemonIds =
                listOf(bundle.getInt("pokemon0"), bundle.getInt("pokemon1"), bundle.getInt("pokemon2"))
            buildAllDamageMovesListAndPokemons(opponentPokemonIds, playerPokemonIds)
            buildTypesDamageRelations()
        }
    }
}