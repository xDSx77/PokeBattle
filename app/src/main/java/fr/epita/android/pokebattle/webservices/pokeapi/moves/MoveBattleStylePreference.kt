package fr.epita.android.pokebattle.webservices.pokeapi.moves

import fr.epita.android.pokebattle.webservices.pokeapi.utils.NamedAPIResource

data class MoveBattleStylePreference (
    var low_hp_preference : Int, // Chance of using the move, in percent, if HP is under one half.
    var high_hp_preference : Int, // Chance of using the move, in percent, if HP is over one half.
    var move_battle_style : NamedAPIResource, // The move battle style.
)
