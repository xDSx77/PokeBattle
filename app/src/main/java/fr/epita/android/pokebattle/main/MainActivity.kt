package fr.epita.android.pokebattle.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.epita.android.pokebattle.*
import fr.epita.android.pokebattle.battle.BattleFragment
import fr.epita.android.pokebattle.battle.lobby.BattleLobbyFragment
import fr.epita.android.pokebattle.pokedex.details.PokedexDetailsFragment
import fr.epita.android.pokebattle.pokedex.list.PokedexListFragment
import fr.epita.android.pokebattle.typehelp.TypeHelpFragment
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.Pokemon

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainContainer, MainFragment())
            .commit()
    }

    fun BattleLobby() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainContainer, BattleLobbyFragment())
            .addToBackStack(null)
            .commit()
    }

    fun PokedexList() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainContainer, PokedexListFragment())
            .addToBackStack(null)
            .commit()
    }

    fun PokedexDetails(pokemonId: Int?) {
        val bundle = Bundle()
        if (pokemonId != null)
            bundle.putInt("pokemonId", pokemonId)
        val pokedexDetailsFragment = PokedexDetailsFragment()
        pokedexDetailsFragment.arguments = bundle
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainContainer, pokedexDetailsFragment)
            .addToBackStack(null)
            .commit()
    }

    fun TypeHelp(typeName : String?) {
        val bundle = Bundle()
        if (typeName != null)
            bundle.putString("typeName", typeName)
        val typeHelpFragment = TypeHelpFragment()
        typeHelpFragment.arguments = bundle
        supportFragmentManager
            .beginTransaction()
            .hide(supportFragmentManager.findFragmentById(R.id.mainContainer)!!)
            .add(R.id.mainContainer, typeHelpFragment)
            .addToBackStack(null)
            .commit()
    }

    fun Battle(opponentPokemon : ArrayList<Int>?, pokemonSlots : ArrayList<Pokemon?>?) {
        val bundle = Bundle()
        opponentPokemon?.forEachIndexed{ index, id ->
            bundle.putInt("opponentPokemon$index", id)
        }
        pokemonSlots?.forEachIndexed{ index, pok ->
            bundle.putInt("pokemon$index", pok!!.id)
        }
        val battleFragment = BattleFragment()
        battleFragment.arguments = bundle
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainContainer, battleFragment)
            .addToBackStack(null)
            .commit()
    }

    fun Home() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainContainer, MainFragment())
            .addToBackStack(null)
            .commit()
    }
}
