package fr.epita.android.pokebattle.webservices.pokeapi.pokemon.abilities

import fr.epita.android.pokebattle.webservices.pokeapi.utils.Effect
import fr.epita.android.pokebattle.webservices.pokeapi.utils.NamedAPIResource

data class AbilityEffectChange (
    var effect_entries : List<Effect>,
    var version_group : NamedAPIResource
)