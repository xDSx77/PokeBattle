package fr.epita.android.pokebattle.webservices.pokeapi.models.pokemon.abilities

import fr.epita.android.pokebattle.webservices.pokeapi.models.utils.Effect
import fr.epita.android.pokebattle.webservices.pokeapi.models.utils.NamedAPIResource

data class AbilityEffectChange (
    var effect_entries : List<Effect>,
    var version_group : NamedAPIResource
)