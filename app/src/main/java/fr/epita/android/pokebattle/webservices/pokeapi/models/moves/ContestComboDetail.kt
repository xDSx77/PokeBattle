package fr.epita.android.pokebattle.webservices.pokeapi.models.moves

import fr.epita.android.pokebattle.webservices.pokeapi.models.utils.NamedAPIResource

data class ContestComboDetail(
    var use_before : List<NamedAPIResource>,
    var use_after : List<NamedAPIResource>
)