package fr.epita.android.pokebattle

import java.util.*

object Utils {
    fun firstLetterUpperCase(string : String) : String {
        return string.substring(0, 1).toUpperCase(Locale.getDefault())
            .plus(string.substring(1))
    }
}