package fr.epita.android.pokebattle.pokedex.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import fr.epita.android.pokebattle.R
import fr.epita.android.pokebattle.main.MainActivity
import fr.epita.android.pokebattle.pokedex.PokedexEntryAdapter
import fr.epita.android.pokebattle.webservices.pokeapi.models.pokemon.Pokemon
import fr.epita.android.pokebattle.webservices.pokeapi.services.PokeAPIService
import kotlinx.android.synthetic.main.fragment_pokedex_list.*
import javax.inject.Inject

@AndroidEntryPoint
class PokedexListFragment : Fragment() {

    @Inject lateinit var pokeAPIService : PokeAPIService

    private lateinit var recyclerView : RecyclerView
    private lateinit var viewAdapter : RecyclerView.Adapter<*>
    private lateinit var viewManager : RecyclerView.LayoutManager

    private fun showPokemons(pokemons : List<Pokemon>) {
        val entryClickListener = View.OnClickListener {
            val position = it.tag as Int
            (activity as MainActivity).pokedexDetails(pokemons[position].id)
        }

        viewManager = LinearLayoutManager(context)
        viewAdapter = PokedexEntryAdapter(pokemons, entryClickListener)
        recyclerView = PokedexListView.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }
    }

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        //namedAPIResourceListRepository = (activity as MainActivity).pokeAPIDatabase.namedAPIResourceListRepository()
        //pokemonSpeciesRepository = (activity as MainActivity).pokeAPIDatabase.pokemonSpeciesRepository()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pokedex_list, container, false)
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        pokeAPIService.buildAllPokemons { pokemons ->
            showPokemons(pokemons)
        }
    }
}
