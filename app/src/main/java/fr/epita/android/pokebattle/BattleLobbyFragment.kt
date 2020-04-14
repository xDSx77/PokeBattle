package fr.epita.android.pokebattle

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import fr.epita.android.pokebattle.webservices.*
import kotlinx.android.synthetic.main.battle_lobby_pokemon_slot.*
import kotlinx.android.synthetic.main.battle_lobby_pokemon_slot.view.*
import kotlinx.android.synthetic.main.fragment_battle_lobby.*
import kotlinx.android.synthetic.main.fragment_pokedex_details.*
import kotlinx.android.synthetic.main.fragment_pokedex_list.*
import kotlinx.android.synthetic.main.pokedex_entry.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class BattleLobbyFragment : Fragment() {

    private var pokemonSlots: ArrayList<Pokemon?>  = arrayListOf(null, null, null);
    private var selectedPokemon : Pokemon? = null;
    private val pokedexEntries : ArrayList<PokedexEntry> = ArrayList()

    val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())

    val retrofit = Retrofit.Builder()
        .baseUrl(SurLeWebAPIInterface.Constants.url)
        .addConverterFactory(jsonConverter)
        .build()

    val service : SurLeWebAPIInterface = retrofit.create(
        SurLeWebAPIInterface::class.java)

    val pokemonListCallback: Callback<List<PokedexEntry>> = object : Callback<List<PokedexEntry>> {
        override fun onFailure(call: Call<List<PokedexEntry>>, t: Throwable) {
            // Code here what happens if calling the WebService fails
            Log.w("WebServices", "SurLeWeb API call failed" + t.message)
        }

        override fun onResponse(
            call: Call<List<PokedexEntry>>,
            response: Response<List<PokedexEntry>>
        ) {
            Log.w("WebServices", "SurLeWeb API call success")
            if (response.code() == 200) {
                // We got our data !
                val pokedexEntriesResponse : List<PokedexEntry> = response.body()!!;

                pokedexEntries.clear();

                for (pokedexEntry in pokedexEntriesResponse) {
                    pokedexEntries.add(pokedexEntry)
                }

                pokedexEntries.sort()

                val entryClickListener = View.OnClickListener {
                    val position = it.tag as Int

                    var entry = pokedexEntries[position];

                    val pokeAPIretrofit = Retrofit.Builder()
                        .baseUrl(PokeAPIInterface.Constants.url)
                        .addConverterFactory(jsonConverter)
                        .build()

                    val pokeAPIservice: PokeAPIInterface =
                        pokeAPIretrofit.create(PokeAPIInterface::class.java)

                    val pokemonCallback: Callback<Pokemon> = object : Callback<Pokemon> {
                        override fun onFailure(call: Call<Pokemon>, t: Throwable) {
                            // Code here what happens if calling the WebService fails
                            Log.w("WebServices", "PokeAPI call failed" + t.message)
                        }

                        override fun onResponse(call: Call<Pokemon>, response: Response<Pokemon>) {
                            Log.w("WebServices", "PokeAPI call success")
                            if (response.code() == 200) {
                                selectedPokemon = response.body()!!;

                                battle_lobby_selected_pokemon_textView.text = entry.name;
                                battle_lobby_selected_pokemon_type1_imageView.setImageResource(entry.types[0].toRDrawable());
                                if (entry.types.size > 1) {
                                    battle_lobby_selected_pokemon_type_2_imageView.isVisible = true;
                                    battle_lobby_selected_pokemon_type_2_imageView.setImageResource(entry.types[1].toRDrawable());
                                } else {
                                    battle_lobby_selected_pokemon_type_2_imageView.isVisible = false;
                                }
                            }
                        }
                    }

                    pokeAPIservice.getPokemon(entry.id).enqueue(pokemonCallback);
                }

                battle_lobby_pokemon_list.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(context)
                    adapter = PokedexEntryAdapter(pokedexEntries, entryClickListener)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_battle_lobby, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        service.pokemonList().enqueue(pokemonListCallback)

        val slotClickListener = View.OnClickListener {
            if (selectedPokemon != null) {
                val position = (it.parent as ConstraintLayout).tag as Int
                pokemonSlots[position] = selectedPokemon;
                battle_lobby_pokemon_slot_list.adapter?.notifyDataSetChanged();

                if (!pokemonSlots.contains(null))
                    battle_lobby_fight_button.isVisible = true;
            }
        }

        battle_lobby_pokemon_slot_list.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            adapter = PokemonSlotAdapter(pokemonSlots, slotClickListener)
        }
    }
}
