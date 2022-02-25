package fr.epita.android.pokebattle.webservices.pokeapi.pokemon

import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.abilities.AbilityPokemon

data class Pokemon(
    var abilities : List<AbilityPokemon>, // A list of abilities this Pokemon could potentially have.
    //var forms : List<NamedAPIResource>, // A list of forms this Pokémon can take on.
    var height : Int, // The height of this Pokemon in decimetres.
    //var held_items : List<PokemonHeldItem>, // A list of items this Pokemon may be holding when encountered.
    var id : Int, // The identifier for this resource.
    //var is_default : Boolean, // Set for exactly one Pokemon used as the default for each species.
    var moves : List<PokemonMove>, // A list of moves along with learn methods and level details pertaining to specific version groups.
    var name : String, // The name for this resource.
    var past_types : List<PokemonTypePast>, // A list of details showing types this pokémon had in previous generations.
    //var species : NamedAPIResource, // The species this Pokemon belongs to.
    var sprites : PokemonSprites, // A set of sprites used to depict this Pokemon in the game.
    var stats : List<PokemonStat>, // A list of base stat values for this Pokemon.
    var types : List<PokemonType>, // A list of details showing types this Pokemon has.
    var weight : Int // The weight of this Pokemon in hectograms.
)