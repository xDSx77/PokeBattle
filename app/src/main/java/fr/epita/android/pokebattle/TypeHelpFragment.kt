package fr.epita.android.pokebattle

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import com.google.gson.GsonBuilder
import fr.epita.android.pokebattle.webservices.PokeAPIInterface
import fr.epita.android.pokebattle.webservices.pokemon.type.Type
import fr.epita.android.pokebattle.webservices.pokemon.type.TypeRelations
import kotlinx.android.synthetic.main.fragment_type_help.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TypeHelpFragment : Fragment() {

    var doubleDamageToTypes : ArrayList<Int> = ArrayList()
    var doubleDamageFromTypes : ArrayList<Int> = ArrayList()
    var halfDamageToTypes : ArrayList<Int> = ArrayList()
    var halfDamageFromTypes : ArrayList<Int> = ArrayList()
    var noDamageToTypes : ArrayList<Int> = ArrayList()
    var noDamageFromTypes : ArrayList<Int> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_type_help, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val typeName : String = arguments!!.getString("typeName") ?: return
        val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())

        val retrofit = Retrofit.Builder()
            .baseUrl(PokeAPIInterface.Constants.url)
            .addConverterFactory(jsonConverter)
            .build()

        val service : PokeAPIInterface = retrofit.create(PokeAPIInterface::class.java)

        val typeCallback : Callback<Type> = object : Callback<Type> {
            override fun onFailure(call: Call<Type>, t: Throwable) {
                Log.w("WebServices", "PokeAPI call failed " + t.message)
            }

            override fun onResponse(call: Call<Type>, response: Response<Type>) {
                Log.w("WebServices", "PokeAPI call success")
                if (response.code() != 200) return
                val type : Type = response.body()!!
                TypeImageView.setImageResource(getTypeImageResource(typeName))
                clearTypesList()
                setTypesList(type.damage_relations)
                setGridView();
            }
        }
        service.getTypeByName(typeName).enqueue(typeCallback)
    }

    private fun clearTypesList() {
        doubleDamageFromTypes.clear()
        doubleDamageToTypes.clear()
        halfDamageFromTypes.clear()
        halfDamageToTypes.clear()
        noDamageFromTypes.clear()
        noDamageToTypes.clear()
    }

    private fun setGridView() {
        DoubleDamageToGridView.adapter = TypeEntryAdapter(activity as Context, doubleDamageToTypes)
        DoubleDamageFromGridView.adapter = TypeEntryAdapter(activity as Context, doubleDamageFromTypes)

        HalfDamageToGridView.adapter = TypeEntryAdapter(activity as Context, halfDamageToTypes)
        HalfDamageFromGridView.adapter = TypeEntryAdapter(activity as Context, halfDamageFromTypes)

        NoDamageToGridView.adapter = TypeEntryAdapter(activity as Context, noDamageToTypes)
        NoDamageFromGridView.adapter = TypeEntryAdapter(activity as Context, noDamageFromTypes)
    }

    private fun setTypesList(damageRelations : TypeRelations)
    {
        for (doubleDamageToType in damageRelations.double_damage_to)
            doubleDamageToTypes.add(getTypeImageResource(doubleDamageToType.name))
        for (doubleDamageFromType in damageRelations.double_damage_from)
            doubleDamageFromTypes.add(getTypeImageResource(doubleDamageFromType.name))
        for (halfDamageToType in damageRelations.half_damage_to)
            halfDamageToTypes.add(getTypeImageResource(halfDamageToType.name))
        for (halfDamageFromType in damageRelations.half_damage_from)
            halfDamageFromTypes.add(getTypeImageResource(halfDamageFromType.name))
        for (noDamageToType in damageRelations.no_damage_to)
            noDamageToTypes.add(getTypeImageResource(noDamageToType.name))
        for (noDamageFromType in damageRelations.no_damage_from)
            noDamageFromTypes.add(getTypeImageResource(noDamageFromType.name))
    }

    private fun getTypeImageResource(typeName: String) =
        when (typeName) {
            "bug" -> R.drawable.bug
            "dark" -> R.drawable.dark
            "dragon" -> R.drawable.dragon
            "electric" -> R.drawable.electric
            "fairy" -> R.drawable.fairy
            "fighting" -> R.drawable.fighting
            "fire" -> R.drawable.fire
            "flying" -> R.drawable.flying
            "ghost" -> R.drawable.ghost
            "grass" -> R.drawable.grass
            "ground" -> R.drawable.ground
            "ice" -> R.drawable.ice
            "normal" -> R.drawable.normal
            "poison" -> R.drawable.poison
            "psychic" -> R.drawable.psychic
            "rock" -> R.drawable.rock
            "steel" -> R.drawable.steel
            "water" -> R.drawable.water
            else -> -1
        }



}
