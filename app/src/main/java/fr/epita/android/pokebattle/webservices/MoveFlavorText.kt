package fr.epita.android.pokebattle.webservices

data class MoveFlavorText(
    var flavor_text : String,
    var language : NamedAPIResource,
    var version_group : NamedAPIResource
)