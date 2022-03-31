package fr.epita.android.pokebattle.typehelp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.epita.android.pokebattle.R
import fr.epita.android.pokebattle.Utils
import fr.epita.android.pokebattle.webservices.pokeapi.PokeAPIHelper
import fr.epita.android.pokebattle.webservices.pokeapi.PokeAPIHelper.pokeAPIInterface
import fr.epita.android.pokebattle.webservices.pokeapi.models.pokemon.type.TypeRelations
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_type_help.*

class TypeHelpFragment : Fragment() {

    private val doubleDamageToTypes : ArrayList<String> = ArrayList()
    private val doubleDamageFromTypes : ArrayList<String> = ArrayList()
    private val halfDamageToTypes : ArrayList<String> = ArrayList()
    private val halfDamageFromTypes : ArrayList<String> = ArrayList()
    private val noDamageToTypes : ArrayList<String> = ArrayList()
    private val noDamageFromTypes : ArrayList<String> = ArrayList()

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_type_help, container, false)
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val typeName : String = requireArguments().getString("typeName") ?: return

        pokeAPIInterface.getTypeByName(typeName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(PokeAPIHelper.pokeApiObserver { type ->
                TypeImageView.setImageResource(Utils.typeToRDrawable(typeName))
                clearTypesList()
                setTypesList(type.damage_relations)
                setGridView()
            })
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

    private fun setTypesList(damageRelations : TypeRelations) {
        for (doubleDamageToType in damageRelations.double_damage_to)
            doubleDamageToTypes.add(doubleDamageToType.name)
        for (doubleDamageFromType in damageRelations.double_damage_from)
            doubleDamageFromTypes.add(doubleDamageFromType.name)
        for (halfDamageToType in damageRelations.half_damage_to)
            halfDamageToTypes.add(halfDamageToType.name)
        for (halfDamageFromType in damageRelations.half_damage_from)
            halfDamageFromTypes.add(halfDamageFromType.name)
        for (noDamageToType in damageRelations.no_damage_to)
            noDamageToTypes.add(noDamageToType.name)
        for (noDamageFromType in damageRelations.no_damage_from)
            noDamageFromTypes.add(noDamageFromType.name)
    }
}
