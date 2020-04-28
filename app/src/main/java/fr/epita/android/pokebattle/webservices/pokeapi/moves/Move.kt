package fr.epita.android.pokebattle.webservices.pokeapi.moves

import fr.epita.android.pokebattle.R
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.abilities.AbilityEffectChange
import fr.epita.android.pokebattle.webservices.pokeapi.utils.*

class Move(
    var id : Int,
    var name : String,
    var accuracy : Int,
    var effect_chance : Int,
    var pp : Int,
    var priority : Int,
    var power : Int?,
    var contest_combos : ContestComboSets,
    var contest_type : NamedAPIResource,
    var contest_effect : APIResource,
    var damage_class : NamedAPIResource,
    var effect_entries : List<VerboseEffect>,
    var effect_changes : List<AbilityEffectChange>,
    var flavor_text_entries : List<MoveFlavorText>,
    var generation : NamedAPIResource,
    var machines : List<MachineVersionDetail>,
    var meta : MoveMetaData,
    var names : List<Name>,
    var past_values : List<PastMoveStatValues>,
    var stat_changes : List<MoveStatChange>,
    var super_contest_effect : APIResource,
    var target : NamedAPIResource,
    var type : NamedAPIResource
)