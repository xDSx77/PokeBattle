package fr.epita.android.pokebattle.webservices.pokeapi.models.resourcelist

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.Relation
import fr.epita.android.pokebattle.webservices.pokeapi.models.utils.NamedAPIResource

@Entity
data class NamedAPIResourceList(
    @PrimaryKey var type : String,
    var count : Int,
    var next : String,
    var previous : Boolean,
    var results : List<NamedAPIResource>
)