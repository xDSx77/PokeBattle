package fr.epita.android.pokebattle.webservices.pokeapi.moves

import fr.epita.android.pokebattle.webservices.pokeapi.utils.Description
import fr.epita.android.pokebattle.webservices.pokeapi.utils.NamedAPIResource

data class MoveCategory (
    var id : Int,
    var name : String,
    var moves : List<NamedAPIResource>,
    var descriptions : List<Description>
)