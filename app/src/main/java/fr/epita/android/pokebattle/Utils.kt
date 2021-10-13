package fr.epita.android.pokebattle

import android.util.Log
import fr.epita.android.pokebattle.webservices.surleweb.api.PokedexEntry
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

object Utils {
    fun firstLetterUpperCase(string : String) : String {
        return string.substring(0, 1).uppercase()
            .plus(string.substring(1))
    }

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
    
    fun filterPokedexEntriesByGeneration(pokedexEntries : List<PokedexEntry>, filteredPokedexEntries : ArrayList<PokedexEntry>) {
        when (Globals.GENERATION) {
            1 -> filteredPokedexEntries.addAll(pokedexEntries.filter { p -> p.id < 152 })
            2 -> filteredPokedexEntries.addAll(pokedexEntries.filter { p -> p.id < 252 })
            3 -> filteredPokedexEntries.addAll(pokedexEntries.filter { p -> p.id < 387 })
            4 -> filteredPokedexEntries.addAll(pokedexEntries.filter { p -> p.id < 494 })
            5 -> filteredPokedexEntries.addAll(pokedexEntries.filter { p -> p.id < 650 })
            6 -> filteredPokedexEntries.addAll(pokedexEntries.filter { p -> p.id < 722 })
            7 -> filteredPokedexEntries.addAll(pokedexEntries.filter { p -> p.id < 810 })
            8 -> filteredPokedexEntries.addAll(pokedexEntries.filter { p -> p.id < 899 })
            else -> filteredPokedexEntries.addAll(pokedexEntries)
        }
    }

    fun <T> pokeAPICallback(onResponse : (Response<T>) -> Unit) : Callback<T> {
        return object : Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) {
                Log.w("WebServices", "Poke API call failed" + t.message)
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                Log.w("WebServices", "Poke API call success")
                if (response.code() == 200)
                    onResponse(response)
            }
        }
    }

    fun <T> surLeWebAPICallback(onResponse : (Response<T>) -> Unit) : Callback<T> {
        return object : Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) {
                Log.w("WebServices", "SurLeWeb API call failed" + t.message)
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                Log.w("WebServices", "SurLeWeb API call success")
                if (response.code() == 200)
                    onResponse(response)
            }
        }
    }
}