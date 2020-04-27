package fr.epita.android.pokebattle.webservices.pokeapi.moves

import fr.epita.android.pokebattle.webservices.pokeapi.utils.NamedAPIResource

data class MoveFlavorText(
    var flavor_text : String,
    var language : NamedAPIResource,
    var version_group : NamedAPIResource
)