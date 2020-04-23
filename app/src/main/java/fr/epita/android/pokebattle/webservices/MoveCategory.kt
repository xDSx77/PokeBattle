package fr.epita.android.pokebattle.webservices

data class MoveCategory (
    var id : Int,
    var name : String,
    var moves : List<NamedAPIResource>,
    var descriptions : List<Description>
)