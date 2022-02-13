package fr.epita.android.pokebattle.webservices.pokeapi.pokemon.nature

import fr.epita.android.pokebattle.webservices.pokeapi.moves.MoveBattleStylePreference
import fr.epita.android.pokebattle.webservices.pokeapi.utils.Name
import fr.epita.android.pokebattle.webservices.pokeapi.utils.NamedAPIResource

data class Nature(
    var id : Int, // The identifier for this resource.
    var name : String, // The name for this resource.
    var decreased_stat : NamedAPIResource, // The stat decreased by 10% in Pokémon with this nature.
    var increased_stat : NamedAPIResource, // The stat increased by 10% in Pokémon with this nature.
    var hates_flavor : NamedAPIResource, // The flavor hated by Pokémon with this nature.
    var likes_flavor : NamedAPIResource, // The flavor liked by Pokémon with this nature.
    var pokeathlon_stat_changes : List<NatureStatChange>, // A list of Pokéathlon stats this nature effects and how much it effects them.
    var move_battle_style_preferences : List<MoveBattleStylePreference>, // A list of battle styles and how likely a Pokémon with this nature is to use them in the Battle Palace or Battle Tent.
    var names : List<Name>, // The name of this resource listed in different languages.
)
