package fr.epita.android.pokebattle

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import fr.epita.android.pokebattle.Utils.firstLetterUpperCase
import fr.epita.android.pokebattle.Utils.typeToRDrawable
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
import kotlin.collections.ArrayList

class BattleLobbyFragment : Fragment() {

    private lateinit var opponentPokemon : Pokemon
    private var pokemonSlots: ArrayList<Pokemon?>  = arrayListOf(null, null, null)
    private var selectedPokemon : Pokemon? = null
    private val pokedexEntries : ArrayList<PokedexEntry> = ArrayList()

    val jsonConverter: GsonConverterFactory = GsonConverterFactory.create(GsonBuilder().create())

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(SurLeWebAPIInterface.Constants.url)
        .addConverterFactory(jsonConverter)
        .build()

    private val service : SurLeWebAPIInterface = retrofit.create(SurLeWebAPIInterface::class.java)

    private val pokemonListCallback: Callback<List<PokedexEntry>> = object : Callback<List<PokedexEntry>> {
        override fun onFailure(call: Call<List<PokedexEntry>>, t: Throwable) {
            Log.w("WebServices", "SurLeWeb API call failed" + t.message)
        }

        override fun onResponse(call: Call<List<PokedexEntry>>, response: Response<List<PokedexEntry>>) {
            Log.w("WebServices", "SurLeWeb API call success")
            if (response.code() == 200) {
                // We got our data !
                val pokedexEntriesResponse : List<PokedexEntry> = response.body()!!

                pokedexEntries.clear()

                pokedexEntries.addAll(pokedexEntriesResponse.filter { p -> p.id < 10000 })

                val opponentEntry = pokedexEntries.random()

                val pokeAPIretrofit = Retrofit.Builder()
                    .baseUrl(PokeAPIInterface.Constants.url)
                    .addConverterFactory(jsonConverter)
                    .build()

                val pokeAPIservice: PokeAPIInterface = pokeAPIretrofit.create(PokeAPIInterface::class.java)

                val opponentPokemonCallback: Callback<Pokemon> = object : Callback<Pokemon> {
                    override fun onFailure(call: Call<Pokemon>, t: Throwable) {
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

                            if (opponentEntry.types.size == 1) {
                                NextOpponentType1ImageView.setImageResource(typeToRDrawable(opponentEntry.types[0].name))
                                NextOpponentType1ImageView.setOnClickListener {
                                    (activity as MainActivity).TypeHelp(opponentEntry.types[0].name)
                                }
                                NextOpponentType2ImageView.visibility = View.INVISIBLE
                            }
                            else if (opponentEntry.types.size == 2) {
                                NextOpponentType2ImageView.visibility = View.VISIBLE
                                NextOpponentType1ImageView.setImageResource(typeToRDrawable(opponentEntry.types[1].name))
                                NextOpponentType1ImageView.setOnClickListener {
                                    (activity as MainActivity).TypeHelp(opponentEntry.types[1].name)
                                }
                                NextOpponentType2ImageView.setImageResource(typeToRDrawable(opponentEntry.types[0].name))
                                NextOpponentType2ImageView.setOnClickListener {
                                    (activity as MainActivity).TypeHelp(opponentEntry.types[0].name)
                                }
                            }
                        }
                    }
                }
                pokeAPIservice.getPokemon(opponentEntry.id).enqueue(opponentPokemonCallback)

                val entryClickListener = View.OnClickListener {
                    val position = it.tag as Int

                    val entry = pokedexEntries[position]

                    SelectedPokemonTextView.text = firstLetterUpperCase(entry.name)
                    Glide
                        .with(this@BattleLobbyFragment)
                        .load(entry.sprite)
                        .into(SelectedPokemonImageView)
                    if (entry.types.size == 1) {
                        SelectedPokemonType2ImageView.visibility = View.INVISIBLE
                        SelectedPokemonType1ImageView.setImageResource(typeToRDrawable(entry.types[0].name))
                        SelectedPokemonType1ImageView.setOnClickListener {
                            (activity as MainActivity).TypeHelp(entry.types[0].name)
                        }
                    }

                    else if (entry.types.size == 2) {
                        SelectedPokemonType2ImageView.visibility = View.VISIBLE
                        SelectedPokemonType1ImageView.setImageResource(typeToRDrawable(entry.types[1].name))
                        SelectedPokemonType1ImageView.setOnClickListener {
                            (activity as MainActivity).TypeHelp(entry.types[1].name)
                        }
                        SelectedPokemonType2ImageView.setImageResource(typeToRDrawable(entry.types[0].name))
                        SelectedPokemonType2ImageView.setOnClickListener {
                            (activity as MainActivity).TypeHelp(entry.types[0].name)
                        }
                    }

                    val selectedPokemonCallback: Callback<Pokemon> = object : Callback<Pokemon> {
                        override fun onFailure(call: Call<Pokemon>, t: Throwable) {
                            Log.w("WebServices", "PokeAPI call failed" + t.message)
                        }

                        override fun onResponse(call: Call<Pokemon>, response: Response<Pokemon>) {
                            Log.w("WebServices", "PokeAPI call success")
                            if (response.code() == 200) {
                                selectedPokemon = response.body()!!
                            }
                        }
                    }

                    pokeAPIservice.getPokemon(entry.id).enqueue(selectedPokemonCallback)
                }

                PokemonList.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(context)
                    adapter = PokedexEntryAdapter(pokedexEntries, entryClickListener)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_battle_lobby, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        service.pokemonList().enqueue(pokemonListCallback)

        FightButton.setOnClickListener {
            (activity as MainActivity).Battle(arrayListOf(opponentPokemon.id, pokedexEntries.random().id, pokedexEntries.random().id), pokemonSlots)
        }

        val slot1ClickListener = View.OnClickListener {
            if (selectedPokemon != null) {
                pokemonSlots[0] = selectedPokemon
                pokemonSlotNameTextView.text = firstLetterUpperCase(selectedPokemon!!.name)
                Glide
                    .with(this@BattleLobbyFragment)
                    .load(selectedPokemon!!.sprites.front_default)
                    .into(pokemonSlotImageView)

                if (!pokemonSlots.contains(null))
                    FightButton.isVisible = true
            }
        }

        val slot2ClickListener = View.OnClickListener {
            if (selectedPokemon != null) {
                pokemonSlots[1] = selectedPokemon
                pokemonSlotNameTextView2.text = firstLetterUpperCase(selectedPokemon!!.name)
                Glide
                    .with(this@BattleLobbyFragment)
                    .load(selectedPokemon!!.sprites.front_default)
                    .into(pokemonSlotImageView2)

                if (!pokemonSlots.contains(null))
                    FightButton.isVisible = true
            }
        }

        val slot3ClickListener = View.OnClickListener {
            if (selectedPokemon != null) {
                pokemonSlots[2] = selectedPokemon
                pokemonSlotNameTextView3.text = firstLetterUpperCase(selectedPokemon!!.name)
                Glide
                    .with(this@BattleLobbyFragment)
                    .load(selectedPokemon!!.sprites.front_default)
                    .into(pokemonSlotImageView3)

                if (!pokemonSlots.contains(null))
                    FightButton.isVisible = true
            }
        }

        pokemonSlotIdButton.setOnClickListener(slot1ClickListener)
        pokemonSlotIdButton2.setOnClickListener(slot2ClickListener)
        pokemonSlotIdButton3.setOnClickListener(slot3ClickListener)
    }
}
