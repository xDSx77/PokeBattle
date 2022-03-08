package fr.epita.android.pokebattle.enums

enum class Generation(val generation : Int, val pokeApiName : String, val maxIdPokedex : Int, val maxIdMove : Int, val maxEvPerStat : Int, val gameVersionsName : List<String>) {
    ONE(1, "generation-i", 151, 165, 255, listOf("red-blue", "yellow")),
    TWO(2, "generation-ii", 251, 251, 255, listOf("gold-silver", "crystal")),
    THREE(3, "generation-iii", 386, 354, 255, listOf("ruby-sapphire", "emerald", "firered-leafgreen", "colosseum", "xd")),
    FOUR(4, "generation-iv", 493, 467, 255, listOf("diamond-pearl", "platinum", "heartgold-soulsilver")),
    FIVE(5, "generation-v", 649, 559, 255, listOf("black-white", "black-2-white-2")),
    SIX(6, "generation-vi", 721, 621, 252, listOf("x-y", "omega-ruby-alpha-sapphire")),
    SEVEN(7, "generation-vii", 809, 742, 252, listOf("sun-moon", "ultra-sun-ultra-moon", "lets-go-pikachu-lets-go-eevee")),
    EIGHT(8, "generation-viii", 898, 826, 252, listOf("sword-shield"));

    companion object {
        @JvmStatic
        fun getFromPokeApiName(pokeApiName : String) : Generation {
            for (generation in values()) {
                if (generation.pokeApiName == pokeApiName) {
                    return generation
                }
            }
            return ONE
        }
    }
}