package fr.epita.android.pokebattle.webservices.pokeapi.models.moves

import fr.epita.android.pokebattle.webservices.pokeapi.models.utils.NamedAPIResource

data class MoveFlavorText(
    var flavor_text : String,
    var language : NamedAPIResource,
    var version_group : NamedAPIResource
)