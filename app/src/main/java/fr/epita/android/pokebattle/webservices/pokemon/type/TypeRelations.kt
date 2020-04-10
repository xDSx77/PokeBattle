package fr.epita.android.pokebattle.webservices.pokemon.type

import fr.epita.android.pokebattle.webservices.NamedAPIResource

data class TypeRelations(
    var no_damage_to : List<NamedAPIResource>,
    var half_damage_to : List<NamedAPIResource>,
    var double_damage_to : List<NamedAPIResource>,
    var no_damage_from: List<NamedAPIResource>,
    var half_damage_from: List<NamedAPIResource>,
    var double_damage_from: List<NamedAPIResource>
)