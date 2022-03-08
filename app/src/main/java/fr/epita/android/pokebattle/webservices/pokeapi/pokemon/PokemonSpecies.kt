package fr.epita.android.pokebattle.webservices.pokeapi.pokemon

import fr.epita.android.pokebattle.webservices.pokeapi.utils.Name
import fr.epita.android.pokebattle.webservices.pokeapi.utils.NamedAPIResource

data class PokemonSpecies(
    var id : Int, // The identifier for this resource.
    var name : String, // The name for this resource.
    var order : Int, // The order in which species should be sorted. Based on National Dex order, except families are grouped together and sorted by stage.
    var gender_rate : Int, // The chance of this Pokémon being female, in eighths; or -1 for genderless.
    var base_happiness : Int, // The happiness when caught by a normal Pokéball; up to 255. The higher the number, the happier the Pokémon.
    var generation : NamedAPIResource, // The generation this Pokémon species was introduced in.
    var names : List<Name>, // The name of this resource listed in different languages.
    var varieties : List<PokemonSpeciesVariety>, // A list of the Pokémon that exist within this Pokémon species.
)