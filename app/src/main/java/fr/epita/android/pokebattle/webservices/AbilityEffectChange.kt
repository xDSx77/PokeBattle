package fr.epita.android.pokebattle.webservices

data class AbilityEffectChange (
    var effect_entries : List<Effect>,
    var version_group : NamedAPIResource
)