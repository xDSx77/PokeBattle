package fr.epita.android.pokebattle.enums

enum class Generation(val generation : Int, val maxIdPokedex : Int, val maxIdMove : Int, val maxEvPerStat : Int) {
    ONE(1, 151, 165, 255),
    TWO(2, 251, 251, 255),
    THREE(3, 386, 354, 255),
    FOUR(4, 493, 467, 255),
    FIVE(5, 649, 559, 255),
    SIX(6, 721, 621, 252),
    SEVEN(7, 809, 742, 252),
    EIGHT(8, 898, 826, 252);
}