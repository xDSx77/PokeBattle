package fr.epita.android.pokebattle.webservices

data class MoveMetaData(
    var ailment : NamedAPIResource,
    var category : NamedAPIResource,
    var min_hits : Int,
    var max_hits : Int,
    var min_turns : Int,
    var max_turns : Int,
    var drain : Int,
    var healing : Int,
    var crit_rate : Int,
    var ailment_chance : Int,
    var flinch_chance : Int,
    var stat_chance : Int
)