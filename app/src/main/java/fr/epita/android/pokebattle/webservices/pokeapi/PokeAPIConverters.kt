package fr.epita.android.pokebattle.webservices.pokeapi

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.epita.android.pokebattle.webservices.pokeapi.models.pokemon.*
import fr.epita.android.pokebattle.webservices.pokeapi.models.pokemon.abilities.AbilityPokemon
import fr.epita.android.pokebattle.webservices.pokeapi.models.utils.Name
import fr.epita.android.pokebattle.webservices.pokeapi.models.utils.NamedAPIResource
import java.lang.reflect.Type

class PokeAPIConverters {

    private val gson = Gson()

    private inline fun <reified T> fromStringToT(valueStr : String) : T {
        val type : Type = object : TypeToken<T>(){}.type
        return gson.fromJson(valueStr, type)
    }

    private inline fun <reified T> fromTToString(value : T) : String {
        return gson.toJson(value)
    }

    private inline fun <reified T> fromStringToListT(valueStr : String) : List<T> {
        val type : Type = object : TypeToken<List<T>>(){}.type
        return gson.fromJson(valueStr, type)
    }

    private inline fun <reified T> fromListTToString(listValue : List<T>) : String {
        return gson.toJson(listValue)
    }

    // NamedAPIResource

    @TypeConverter
    fun fromStringToNamedAPIResource(valueStr : String) : NamedAPIResource = fromStringToT(valueStr)

    @TypeConverter
    fun fromNamedAPIResource(namedAPIResource : NamedAPIResource) : String = fromTToString(namedAPIResource)

    @TypeConverter
    fun fromStringToListNamedAPIResource(valueStr : String) : List<NamedAPIResource> = fromStringToListT(valueStr)

    @TypeConverter
    fun fromListNamedAPIResourceToString(list : List<NamedAPIResource>) : String = fromListTToString(list)

    // Name

    @TypeConverter
    fun fromStringToListName(valueStr : String) : List<Name> = fromStringToListT(valueStr)

    @TypeConverter
    fun fromListNameToString(list : List<Name>) : String = fromListTToString(list)

    // PokemonSpeciesVariety

    @TypeConverter
    fun fromStringToListPokemonSpeciesVariety(valueStr : String) : List<PokemonSpeciesVariety> = fromStringToListT(valueStr)

    @TypeConverter
    fun fromListPokemonSpeciesVarietyToString(list : List<PokemonSpeciesVariety>) : String = fromListTToString(list)

    // AbilityPokemon

    @TypeConverter
    fun fromStringToListAbilityPokemon(valueStr : String) : List<AbilityPokemon> = fromStringToListT(valueStr)

    @TypeConverter
    fun fromListAbilityPokemonToString(list : List<AbilityPokemon>) : String = fromListTToString(list)

    // PokemonMove

    @TypeConverter
    fun fromStringToListPokemonMove(valueStr : String) : List<PokemonMove> = fromStringToListT(valueStr)

    @TypeConverter
    fun fromListPokemonMoveToString(list : List<PokemonMove>) : String = fromListTToString(list)

    // PokemonTypePast

    @TypeConverter
    fun fromStringToListPokemonTypePast(valueStr : String) : List<PokemonTypePast> = fromStringToListT(valueStr)

    @TypeConverter
    fun fromListPokemonTypePastToString(list : List<PokemonTypePast>) : String = fromListTToString(list)

    // PokemonSprites

    @TypeConverter
    fun fromStringToPokemonSprites(valueStr : String) : PokemonSprites = fromStringToT(valueStr)

    @TypeConverter
    fun fromPokemonSpritesToString(pokemonSprites : PokemonSprites) : String = fromTToString(pokemonSprites)

    // PokemonStat

    @TypeConverter
    fun fromStringToListPokemonStat(valueStr : String) : List<PokemonStat> = fromStringToListT(valueStr)

    @TypeConverter
    fun fromListPokemonStatToString(list : List<PokemonStat>) : String = fromListTToString(list)

    // PokemonType

    @TypeConverter
    fun fromStringToListPokemonType(valueStr : String) : List<PokemonType> = fromStringToListT(valueStr)

    @TypeConverter
    fun fromListPokemonTypeToString(list : List<PokemonType>) : String = fromListTToString(list)
}