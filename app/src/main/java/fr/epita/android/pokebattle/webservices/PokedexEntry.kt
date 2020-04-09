package fr.epita.android.pokebattle.webservices

import fr.epita.android.pokebattle.R

class PokedexEntry (var id : Int, var name : String, var sprite : String, var types : List<Type>) : Comparable<PokedexEntry> {
    class Type (var id : Int, var  name : String) {
        fun toRDrawable() : Int {
            when (name) {
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

    }

    override fun compareTo(other: PokedexEntry): Int = name.compareTo(other.name)
}
