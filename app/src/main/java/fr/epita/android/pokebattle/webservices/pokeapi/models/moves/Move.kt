package fr.epita.android.pokebattle.webservices.pokeapi.models.moves

import fr.epita.android.pokebattle.webservices.pokeapi.models.pokemon.abilities.AbilityEffectChange
import fr.epita.android.pokebattle.webservices.pokeapi.models.utils.Name
import fr.epita.android.pokebattle.webservices.pokeapi.models.utils.NamedAPIResource
import fr.epita.android.pokebattle.webservices.pokeapi.models.utils.VerboseEffect

data class Move(
    var id : Int, // The identifier for this resource.
    var name : String, // The name for this resource.
    var accuracy : Int, // The percent value of how likely this move is to be successful.
    var effect_chance : Int, // The percent value of how likely it is this moves effect will happen.
    var pp : Int, // Power points. The number of times this move can be used.
    var priority : Int, // A value between -8 and 8. Sets the order in which moves are executed during battle. See http://bulbapedia.bulbagarden.net/wiki/Priority for greater detail.
    var power : Int?, // The base power of this move with a value of 0 if it does not have a base power.
    var damage_class : NamedAPIResource, // The type of damage the move inflicts on the target, e.g. physical.
    var effect_entries : List<VerboseEffect>, // The effect of this move listed in different languages.
    var effect_changes : List<AbilityEffectChange>, // The list of previous effects this move has had across version groups of the games.
    var flavor_text_entries : List<MoveFlavorText>, // The flavor text of this move listed in different languages.
    var generation : NamedAPIResource, // The generation in which this move was introduced.
    var meta : MoveMetaData, // Metadata about this move
    var names : List<Name>, // The name of this resource listed in different languages.
    var past_values : List<PastMoveStatValues>, // A list of move resource value changes across version groups of the game.
    var stat_changes : List<MoveStatChange>, // A list of stats this moves effects and how much it effects them.
    //var target : NamedAPIResource, // The type of target that will receive the effects of the attack.
    var type : NamedAPIResource // The elemental type of this move.
)