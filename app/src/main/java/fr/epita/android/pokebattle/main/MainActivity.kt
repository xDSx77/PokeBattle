package fr.epita.android.pokebattle.main

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import fr.epita.android.pokebattle.Constants.BATTLE
import fr.epita.android.pokebattle.Constants.BATTLE_LOBBY
import fr.epita.android.pokebattle.Constants.LOADING
import fr.epita.android.pokebattle.Constants.POKEDEX
import fr.epita.android.pokebattle.Constants.POKEDEX_DETAILS
import fr.epita.android.pokebattle.Constants.START
import fr.epita.android.pokebattle.Constants.TYPE_HELP
import fr.epita.android.pokebattle.R
import fr.epita.android.pokebattle.battle.BattleFragment
import fr.epita.android.pokebattle.battle.BattleLoadingScreenFragment
import fr.epita.android.pokebattle.battle.lobby.BattleLobbyFragment
import fr.epita.android.pokebattle.pokedex.details.PokedexDetailsFragment
import fr.epita.android.pokebattle.pokedex.list.PokedexListFragment
import fr.epita.android.pokebattle.typehelp.TypeHelpFragment
import fr.epita.android.pokebattle.webservices.pokeapi.models.pokemon.Pokemon
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainContainer, MainFragment())
            .commit()
    }

    fun battleLobby() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainContainer, BattleLobbyFragment())
            .addToBackStack(BATTLE_LOBBY)
            .commit()
    }

    fun pokedexList() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainContainer, PokedexListFragment())
            .addToBackStack(POKEDEX)
            .commit()
    }

    fun pokedexDetails(pokemonId : Int?) {
        val bundle = Bundle()
        if (pokemonId != null)
            bundle.putInt("pokemonId", pokemonId)
        val pokedexDetailsFragment = PokedexDetailsFragment()
        pokedexDetailsFragment.arguments = bundle
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainContainer, pokedexDetailsFragment)
            .addToBackStack(POKEDEX_DETAILS)
            .commit()
    }

    fun typeHelp(typeName : String?) {
        val bundle = Bundle()
        if (typeName != null)
            bundle.putString("typeName", typeName)
        val typeHelpFragment = TypeHelpFragment()
        typeHelpFragment.arguments = bundle
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainContainer, typeHelpFragment)
            .addToBackStack(TYPE_HELP)
            .commit()
    }

    fun battle() {
        val battleFragment = BattleFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainContainer, battleFragment)
            .addToBackStack(BATTLE)
            .commit()
    }

    fun loadBattle(opponentPokemon : ArrayList<Int>?, pokemonSlots : ArrayList<Pokemon?>?) {
        val bundle = Bundle()
        opponentPokemon?.forEachIndexed { index, id ->
            bundle.putInt("opponentPokemon$index", id)
        }
        pokemonSlots?.forEachIndexed { index, pok ->
            bundle.putInt("pokemon$index", pok!!.id)
        }
        val battleLoadingScreenFragment = BattleLoadingScreenFragment()
        battleLoadingScreenFragment.arguments = bundle
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainContainer, battleLoadingScreenFragment)
            .addToBackStack(LOADING)
            .commit()
    }

    fun home() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainContainer, MainFragment())
            .addToBackStack(START)
            .commit()
    }

    override fun onBackPressed() {
        val backStackEntryCount = supportFragmentManager.backStackEntryCount
        val lastBackStackEntryName =
            (if (backStackEntryCount == 0) null else supportFragmentManager.getBackStackEntryAt(backStackEntryCount - 1))?.name
        if (lastBackStackEntryName != null && (lastBackStackEntryName == LOADING || lastBackStackEntryName == BATTLE)) {
            Toast.makeText(this, "Cannot go back now", Toast.LENGTH_SHORT).show()
        } else if (lastBackStackEntryName != null && lastBackStackEntryName == START) {
            exitProcess(0)
        } else {
            super.onBackPressed()
        }
    }
}
