package fr.epita.android.pokebattle.webservices.pokeapi.models.pokemon.type

import fr.epita.android.pokebattle.webservices.pokeapi.models.utils.GenerationGameIndex
import fr.epita.android.pokebattle.webservices.pokeapi.models.utils.Name
import fr.epita.android.pokebattle.webservices.pokeapi.models.utils.NamedAPIResource

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