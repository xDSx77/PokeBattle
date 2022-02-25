package fr.epita.android.pokebattle.enums

enum class Generation(val generation : Int, val maxIdPokedex : Int, val maxIdMove : Int, val maxEvPerStat : Int, val gameVersionsName : List<String>) {
    ONE(1, 151, 165, 255, listOf("red-blue", "yellow")),
    TWO(2, 251, 251, 255, listOf("gold-silver", "crystal")),
    THREE(3, 386, 354, 255, listOf("ruby-sapphire", "emerald", "firered-leafgreen", "colosseum", "xd")),
    FOUR(4, 493, 467, 255, listOf("diamond-pearl", "platinum", "heartgold-soulsilver")),
    FIVE(5, 649, 559, 255, listOf("black-white", "black-2-white-2")),
    SIX(6, 721, 621, 252, listOf("x-y", "omega-ruby-alpha-sapphire")),
    SEVEN(7, 809, 742, 252, listOf("sun-moon", "ultra-sun-ultra-moon", "lets-go-pikachu-lets-go-eevee")),
    EIGHT(8, 898, 826, 252, listOf("sword-shield"));
}