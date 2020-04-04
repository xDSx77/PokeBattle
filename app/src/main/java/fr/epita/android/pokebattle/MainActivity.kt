package fr.epita.android.pokebattle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

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
}
