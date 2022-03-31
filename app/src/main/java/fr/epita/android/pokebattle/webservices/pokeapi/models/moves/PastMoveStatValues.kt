package fr.epita.android.pokebattle.webservices.pokeapi.models.moves

import fr.epita.android.pokebattle.webservices.pokeapi.models.utils.NamedAPIResource
import fr.epita.android.pokebattle.webservices.pokeapi.models.utils.VerboseEffect

data class PastMoveStatValues (
    var accuracy : Int,
    var effect_chance : Int,
    var power : Int,
    var pp : Int,
    var effect_entries : List<VerboseEffect>,
    var type : NamedAPIResource,
    var version_group : NamedAPIResource
)