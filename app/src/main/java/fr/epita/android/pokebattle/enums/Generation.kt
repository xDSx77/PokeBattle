package fr.epita.android.pokebattle.enums

enum class Generation(val generation : Int, val maxIdPokedex : Int, val maxIdMove : Int) {
    ONE(1, 151, 165),
    TWO(2, 251, 251),
    THREE(3, 386, 354),
    FOUR(4, 493, 467),
    FIVE(5, 649, 559),
    SIX(6, 721, 621),
    SEVEN(7, 809, 742),
    EIGHT(8, 898, 826);
}