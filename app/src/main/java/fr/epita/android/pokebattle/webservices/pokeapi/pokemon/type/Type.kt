package fr.epita.android.pokebattle.webservices.pokeapi.pokemon.type

import fr.epita.android.pokebattle.webservices.pokeapi.utils.GenerationGameIndex
import fr.epita.android.pokebattle.webservices.pokeapi.utils.Name
import fr.epita.android.pokebattle.webservices.pokeapi.utils.NamedAPIResource

data class Type(
    var id : Int,
    var name : String,
    var damage_relations : TypeRelations,
    var game_indices : List<GenerationGameIndex>,
    var generation : NamedAPIResource,
    var move_damage_class : NamedAPIResource,
    var names : List<Name>,
    var pokemon : List<TypePokemon>,
    var moves : List<NamedAPIResource>
)