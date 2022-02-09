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
import fr.epita.android.pokebattle.main.MainActivity
import fr.epita.android.pokebattle.webservices.pokeapi.PokeAPIServiceHelper
import fr.epita.android.pokebattle.webservices.pokeapi.moves.Move
import fr.epita.android.pokebattle.webservices.pokeapi.moves.MoveCategory
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.Pokemon
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.type.Type
import fr.epita.android.pokebattle.webservices.pokeapi.resourcelist.NamedAPIResourceList
import fr.epita.android.pokebattle.webservices.pokeapi.utils.NamedAPIResource
import retrofit2.Callback

class BattleLoadingScreenFragment : Fragment() {

    private val allDamageMovesList : MutableList<NamedAPIResource> = mutableListOf()

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_battle_loading_screen, container, false)
    }

    private fun buildPokemonMoves(pokemonInfo : PokemonInfo, isLast : Boolean) {
        val allPokemonMoves = pokemonInfo.pokemon!!.moves.shuffled()
        var movesCount = 0
        val moveCallback : Callback<Move> = Utils.pokeAPICallback { response ->
            pokemonInfo.moves.add(response.body()!!)
            if (pokemonInfo.moves.size == 4 && isLast) {
                //loadingGroup.visibility = View.GONE
                (activity as MainActivity).battle()
            }
        }
        for (pokemonMove in allPokemonMoves) {
            if (movesCount == 4)
                break
            if (allDamageMovesList.any { it.name == pokemonMove.move.name }) {
                PokeAPIServiceHelper.pokeAPIService.getMove(pokemonMove.move.name).enqueue(moveCallback)
                movesCount++
            }
        }
    }

    private fun buildOpponentPokemon(pokemonId : Int, opponentPokemonInfo : PokemonInfo) {
        val opponentCallback : Callback<Pokemon> = Utils.pokeAPICallback { opponentResponse ->
            val opponentPokemon = opponentResponse.body()!!
            val opponentHp = opponentPokemon.stats.find { it.stat.name == "hp" }!!.base_stat
            opponentPokemonInfo.pokemon = opponentPokemon
            opponentPokemonInfo.hp = opponentHp
            opponentPokemonInfo.moves = mutableListOf()
            buildPokemonMoves(opponentPokemonInfo, false)
        }
        PokeAPIServiceHelper.pokeAPIService.getPokemon(pokemonId).enqueue(opponentCallback)

    }

    private fun buildPlayerPokemon(pokemonId : Int, pokemonInfo : PokemonInfo, isLast : Boolean) {
        val pokemonCallback : Callback<Pokemon> = Utils.pokeAPICallback { pokemonResponse ->
            val pokemon = pokemonResponse.body()!!
            val pokemonHp = pokemon.stats.find { it.stat.name == "hp" }!!.base_stat
            pokemonInfo.pokemon = pokemon
            pokemonInfo.hp = pokemonHp
            pokemonInfo.moves = mutableListOf()
            buildPokemonMoves(pokemonInfo, isLast)
        }
        PokeAPIServiceHelper.pokeAPIService.getPokemon(pokemonId).enqueue(pokemonCallback)
    }

    private fun buildAllDamageMovesListAndPokemons(opponentPokemonIds : List<Int>, playerPokemonIds : List<Int>) {
        val movesCallback : Callback<MoveCategory> = Utils.pokeAPICallback { movesResponse ->
            val allDamageMovesGenerationsList : MoveCategory = movesResponse.body()!!
            allDamageMovesList.addAll(allDamageMovesGenerationsList.moves.filterIndexed { index, _ ->
                index <= Globals.GENERATION.maxIdMove
            })
            // 1st opponent pokémon
            buildOpponentPokemon(opponentPokemonIds[0], opponentPokemon1)
            // 2nd opponent pokémon
            buildOpponentPokemon(opponentPokemonIds[1], opponentPokemon2)
            // 3rd opponent pokémon
            buildOpponentPokemon(opponentPokemonIds[2], opponentPokemon3)

            // 1st player pokémon
            buildPlayerPokemon(playerPokemonIds[0], pokemon1, false)
            // 2nd player pokémon
            buildPlayerPokemon(playerPokemonIds[1], pokemon2, false)
            // 3rd player pokémon
            buildPlayerPokemon(playerPokemonIds[2], pokemon3, true)
        }
        PokeAPIServiceHelper.pokeAPIService.getAllDamageMoves().enqueue(movesCallback)
    }

    private fun buildTypesDamageRelations() {
        val typesCallback : Callback<NamedAPIResourceList> = Utils.pokeAPICallback { typesResponse ->
            val typesResourceList : NamedAPIResourceList = typesResponse.body()!!
            for (typeResource in typesResourceList.results) {
                val typeCallback : Callback<Type> = Utils.pokeAPICallback { typeResponse ->
                    val type : Type = typeResponse.body()!!
                    DamageHelper.allMovesTypesDamageRelations[type.name] = type.damage_relations
                }
                PokeAPIServiceHelper.pokeAPIService.getTypeByName(typeResource.name).enqueue(typeCallback)
            }
        }
        PokeAPIServiceHelper.pokeAPIService.getTypes().enqueue(typesCallback)
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