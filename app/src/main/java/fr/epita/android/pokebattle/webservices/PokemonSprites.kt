package fr.epita.android.pokebattle.webservices

data class PokemonSprites(
    var front_default : String, // The default depiction of this Pokemon from the front in battle.
    var front_shiny : String?, // The shiny depiction of this Pokemon from the front in battle.
    var front_female : String?, // The female depiction of this Pokemon from the front in battle.
    var front_shiny_female : String?, // The shiny female depiction of this Pokemon from the front in battle.
    var back_default : String?, // The default depiction of this Pokemon from the back in battle.
    var back_shiny : String?, // The shiny depiction of this Pokemon from the back in battle.
    var back_female : String?, // The female depiction of this Pokemon from the back in battle.
    var back_shiny_female : String? // The shiny female depiction of this Pokemon from the back in battle.
)