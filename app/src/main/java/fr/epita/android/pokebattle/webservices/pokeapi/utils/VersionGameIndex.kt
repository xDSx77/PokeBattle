package fr.epita.android.pokebattle.webservices.pokeapi.utils

import fr.epita.android.pokebattle.webservices.pokeapi.utils.NamedAPIResource

data class VersionGameIndex(
    var game_index : Int, // The internal id of an API resource within game data.
    var version : NamedAPIResource // The version relevant to this game index.

)