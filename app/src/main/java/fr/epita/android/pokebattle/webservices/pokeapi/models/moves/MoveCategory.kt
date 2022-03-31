package fr.epita.android.pokebattle.webservices.pokeapi.models.moves

import fr.epita.android.pokebattle.webservices.pokeapi.models.utils.Description
import fr.epita.android.pokebattle.webservices.pokeapi.models.utils.NamedAPIResource

data class MoveCategory (
    var id : Int,
    var name : String,
    var moves : List<NamedAPIResource>,
    var descriptions : List<Description>
)