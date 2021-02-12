package fr.epita.android.pokebattle

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_pokedex_list.*
import com.google.gson.GsonBuilder
import fr.epita.android.pokebattle.Utils.filterPokedexEntriesByGeneration
import fr.epita.android.pokebattle.webservices.surleweb.api.PokedexEntry
import fr.epita.android.pokebattle.webservices.surleweb.api.SurLeWebAPIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PokedexListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val pokedexEntries: ArrayList<PokedexEntry> = ArrayList()

    // Use GSON library to create our JSON parser
    private val jsonConverter: GsonConverterFactory = GsonConverterFactory.create(GsonBuilder().create())

    // Create a Retrofit client object targeting the provided URL
    // and add a JSON converter (because we are expecting json responses)
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(SurLeWebAPIInterface.Constants.url)
        .addConverterFactory(jsonConverter)
        .build()

    // Use the client to create a service:
    // an object implementing the interface to the WebService
    private val service: SurLeWebAPIInterface = retrofit.create(
        SurLeWebAPIInterface::class.java)

    private val pokemonListCallback: Callback<List<PokedexEntry>> = object : Callback<List<PokedexEntry>> {
        override fun onFailure(call: Call<List<PokedexEntry>>, t: Throwable) {
            // Code here what happens if calling the WebService fails
            Log.w("WebServices", "SurLeWeb API call failed" + t.message)
        }

        override fun onResponse(call: Call<List<PokedexEntry>>, response: Response<List<PokedexEntry>>) {
            Log.w("WebServices", "SurLeWeb API call success")
            if (response.code() == 200) {
                // We got our data !
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
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pokedex_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        service.pokemonList().enqueue(pokemonListCallback)
    }
}
