package fr.epita.android.pokebattle.webservices.pokeapi.pokemon

import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.abilities.AbilityPokemon
import fr.epita.android.pokebattle.webservices.pokeapi.utils.NamedAPIResource
import fr.epita.android.pokebattle.webservices.pokeapi.utils.VersionGameIndex

data class Pokemon(
    var abilities : List<AbilityPokemon>, // A list of abilities this Pokemon could potentially have.
    var base_experience : Int, // A list of abilities this Pokemon could potentially have.
    var forms : List<NamedAPIResource>, // A list of abilities this Pokemon could potentially have.
    var game_indices : List<VersionGameIndex>, // A list of game indices relevant to Pokemon item by generation.
    var height : Int, // The height of this Pokemon in decimetres.
    var held_items : List<PokemonHeldItem>, // A list of items this Pokemon may be holding when encountered.
    var id : Int, // The identifier for this resource.
    var is_default : Boolean, // Set for exactly one Pokemon used as the default for each species.
    var location_area_encounters : String, // A link to a list of location areas, as well as encounter details pertaining to specific versions.
    var moves : List<PokemonMove>, // A list of moves along with learn methods and level details pertaining to specific version groups.
    var name : String, // The name for this resource.
    var order : Int, // Order for sorting. Almost national order, except families are grouped together.
    var species : NamedAPIResource, // The species this Pokemon belongs to.
    var sprites : PokemonSprites, // A set of sprites used to depict this Pokemon in the game.
    var stats : List<PokemonStat>, // A list of base stat values for this Pokemon.
    var types : List<PokemonType>, // A list of details showing types this Pokemon has.
    var weight : Int // The weight of this Pokemon in hectograms.
)