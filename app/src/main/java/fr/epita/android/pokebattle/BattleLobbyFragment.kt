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
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import fr.epita.android.pokebattle.Utils.firstLetterUpperCase
import fr.epita.android.pokebattle.webservices.pokeapi.PokeAPIInterface
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.Pokemon
import fr.epita.android.pokebattle.webservices.surleweb.api.PokedexEntry
import fr.epita.android.pokebattle.webservices.surleweb.api.SurLeWebAPIInterface
import kotlinx.android.synthetic.main.fragment_battle_lobby.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class BattleLobbyFragment : Fragment() {

    private lateinit var opponentPokemon : Pokemon
    private var pokemonSlots: ArrayList<Pokemon?>  = arrayListOf(null, null, null)
    private var selectedPokemon : Pokemon? = null
    private val pokedexEntries : ArrayList<PokedexEntry> = ArrayList()

    val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())

    val retrofit = Retrofit.Builder()
        .baseUrl(SurLeWebAPIInterface.Constants.url)
        .addConverterFactory(jsonConverter)
        .build()

    val service : SurLeWebAPIInterface = retrofit.create(SurLeWebAPIInterface::class.java)

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
                val pokedexEntriesResponse : List<PokedexEntry> = response.body()!!

                pokedexEntries.clear()

                for (pokedexEntry in pokedexEntriesResponse) {
                    pokedexEntries.add(pokedexEntry)
                }

                pokedexEntries.sort()

                val opponentEntry = pokedexEntries.random()

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
                            opponentPokemon = response.body()!!

                            NextOpponentNameTextView.text = firstLetterUpperCase(opponentEntry.name)
                            Glide
                                .with(this@BattleLobbyFragment)
                                .load(opponentPokemon.sprites.front_default)
                                .into(NextOpponentImageView)
                            NextOpponentType1ImageView.setImageResource(opponentEntry.types[0].toRDrawable())
                            NextOpponentType1ImageView.setOnClickListener {
                                (activity as MainActivity).TypeHelp(opponentEntry.types[0].name)
                            }
                            if (opponentEntry.types.size > 1) {
                                NextOpponentType2ImageView.isVisible = true
                                NextOpponentType2ImageView.setImageResource(opponentEntry.types[1].toRDrawable())
                                NextOpponentType2ImageView.setOnClickListener {
                                    (activity as MainActivity).TypeHelp(opponentEntry.types[1].name)
                                }
                            } else {
                                NextOpponentType2ImageView.isVisible = false
                            }
                        }
                    }
                }
                pokeAPIservice.getPokemon(opponentEntry.id).enqueue(pokemonCallback)

                val entryClickListener = View.OnClickListener {
                    val position = it.tag as Int

                    val entry = pokedexEntries[position]

                    val opponentPokemonCallback: Callback<Pokemon> = object : Callback<Pokemon> {
                        override fun onFailure(call: Call<Pokemon>, t: Throwable) {
                            // Code here what happens if calling the WebService fails
                            Log.w("WebServices", "PokeAPI call failed" + t.message)
                        }

                        override fun onResponse(call: Call<Pokemon>, response: Response<Pokemon>) {
                            Log.w("WebServices", "PokeAPI call success")
                            if (response.code() == 200) {
                                selectedPokemon = response.body()!!

                                SelectedPokemonTextView.text = firstLetterUpperCase(entry.name)
                                SelectedPokemonType1ImageView.setImageResource(entry.types[0].toRDrawable())
                                if (entry.types.size > 1) {
                                    SelectedPokemonType2ImageView.isVisible = true
                                    SelectedPokemonType2ImageView.setImageResource(entry.types[1].toRDrawable())
                                } else {
                                    SelectedPokemonType2ImageView.isVisible = false
                                }
                            }
                        }
                    }

                    pokeAPIservice.getPokemon(entry.id).enqueue(opponentPokemonCallback)
                }

                PokemonList.apply {
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

        FightButton.setOnClickListener {
            (activity as MainActivity).Battle(arrayListOf(opponentPokemon.id, pokedexEntries.random().id, pokedexEntries.random().id), pokemonSlots)
        }

        val slotClickListener = View.OnClickListener {
            if (selectedPokemon != null) {
                val position = (it.parent as ConstraintLayout).tag as Int
                pokemonSlots[position] = selectedPokemon
                PokemonSlotList.adapter?.notifyDataSetChanged()

                if (!pokemonSlots.contains(null))
                    FightButton.isVisible = true
            }
        }

        PokemonSlotList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = PokemonSlotAdapter(pokemonSlots, slotClickListener)
        }
    }
}
