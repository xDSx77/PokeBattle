package fr.epita.android.pokebattle.webservices

data class PastMoveStatValues (
    var accuracy : Int,
    var effect_chance : Int,
    var power : Int,
    var pp : Int,
    var effect_entries : List<VerboseEffect>,
    var type : NamedAPIResource,
    var version_group : NamedAPIResource
)