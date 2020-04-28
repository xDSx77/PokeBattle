package fr.epita.android.pokebattle.webservices.pokeapi.moves

import fr.epita.android.pokebattle.webservices.pokeapi.utils.NamedAPIResource

data class ContestComboDetail(
    var use_before : List<NamedAPIResource>,
    var use_after : List<NamedAPIResource>
)