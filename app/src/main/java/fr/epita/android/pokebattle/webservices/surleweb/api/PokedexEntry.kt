package fr.epita.android.pokebattle.webservices.surleweb.api

class PokedexEntry (var id : Int, var name : String, var sprite : String, var types : List<Type>) : Comparable<PokedexEntry> {

    override fun compareTo(other: PokedexEntry): Int = name.compareTo(other.name)
}
