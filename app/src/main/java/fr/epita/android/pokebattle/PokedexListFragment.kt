package fr.epita.android.pokebattle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.epita.android.pokebattle.Utils.filterPokedexEntriesByGeneration
import fr.epita.android.pokebattle.Utils.surLeWebAPICallback
import fr.epita.android.pokebattle.webservices.surleweb.api.PokedexEntry
import fr.epita.android.pokebattle.webservices.surleweb.api.SurLeWebServiceFragment
import kotlinx.android.synthetic.main.fragment_pokedex_list.*
import retrofit2.Callback

class PokedexListFragment : SurLeWebServiceFragment() {

    private lateinit var recyclerView : RecyclerView
    private lateinit var viewAdapter : RecyclerView.Adapter<*>
    private lateinit var viewManager : RecyclerView.LayoutManager

    private val pokedexEntries : ArrayList<PokedexEntry> = ArrayList()

    private val pokemonListCallback : Callback<List<PokedexEntry>> =
        surLeWebAPICallback { response ->
            val pokedexEntriesResponse : List<PokedexEntry> = response.body()!!

            pokedexEntries.clear()

            filterPokedexEntriesByGeneration(pokedexEntriesResponse, pokedexEntries)

            val entryClickListener = View.OnClickListener {
                val position = it.tag as Int
                (activity as MainActivity).PokedexDetails(pokedexEntries[position].id)
            }

            viewManager = LinearLayoutManager(context)
            viewAdapter = PokedexEntryAdapter(pokedexEntries, entryClickListener)
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pokedex_list, container, false)
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        surLeWebService.pokemonList().enqueue(pokemonListCallback)
    }
}
