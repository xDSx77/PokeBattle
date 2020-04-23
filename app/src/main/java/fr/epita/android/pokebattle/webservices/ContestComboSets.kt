package fr.epita.android.pokebattle.webservices

import com.google.gson.annotations.SerializedName

data class ContestComboSets(
    var normal : ContestComboDetail,
    @SerializedName("super") var isSuper : ContestComboDetail
)