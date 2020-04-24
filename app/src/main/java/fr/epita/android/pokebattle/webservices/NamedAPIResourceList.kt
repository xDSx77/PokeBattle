package fr.epita.android.pokebattle.webservices

data class NamedAPIResourceList(
    var count : Int,
    var next : String,
    var previous : Boolean,
    var results : List<NamedAPIResource>
)