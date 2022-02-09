package fr.epita.android.pokebattle.battle.lobby

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import fr.epita.android.pokebattle.R
import fr.epita.android.pokebattle.Utils
import fr.epita.android.pokebattle.main.MainActivity
import fr.epita.android.pokebattle.pokedex.PokedexEntryAdapter
import fr.epita.android.pokebattle.webservices.pokeapi.PokeAPIInterface
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.Pokemon
import fr.epita.android.pokebattle.webservices.surleweb.api.PokedexEntry
import fr.epita.android.pokebattle.webservices.surleweb.api.SurLeWebServiceHelper.surLeWebService
import kotlinx.android.synthetic.main.fragment_battle_lobby.*
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BattleLobbyFragment : Fragment() {

    private lateinit var opponentPokemon : Pokemon
    private var selectedPokemon : Pokemon? = null

    private val pokemonSlots : ArrayList<Pokemon?> = arrayListOf(null, null, null)
    private val pokedexEntries : ArrayList<PokedexEntry> = ArrayList()

    private val pokemonListCallback : Callback<List<PokedexEntry>> =
        Utils.surLeWebAPICallback { surLeWebAPIResponse ->
            val pokedexEntriesResponse : List<PokedexEntry> = surLeWebAPIResponse.body()!!

            pokedexEntries.clear()

            Utils.filterPokedexEntriesByGeneration(pokedexEntriesResponse, pokedexEntries)

            val opponentEntry = pokedexEntries.random()

            val pokeAPIRetrofit = Retrofit.Builder()
                .baseUrl(PokeAPIInterface.Constants.url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val pokeAPIService : PokeAPIInterface =
                pokeAPIRetrofit.create(PokeAPIInterface::class.java)

            val opponentPokemonCallback : Callback<Pokemon> =
                Utils.pokeAPICallback { pokeAPIResponse ->
                    opponentPokemon = pokeAPIResponse.body()!!

                    NextOpponentNameTextView.text = Utils.firstLetterUpperCase(opponentEntry.name)
                    NextOpponentImageView.visibility = View.VISIBLE
                    Glide
                        .with(this@BattleLobbyFragment)
                        .load(opponentPokemon.sprites.front_default)
                        .into(NextOpponentImageView)

                    if (opponentEntry.types.size == 1) {
                        NextOpponentType1ImageView.visibility = View.VISIBLE
                        NextOpponentType2ImageView.visibility = View.INVISIBLE
                        NextOpponentType1ImageView.setImageResource(Utils.typeToRDrawable(opponentEntry.types[0].name))
                        NextOpponentType1ImageView.setOnClickListener {
                            (activity as MainActivity).typeHelp(opponentEntry.types[0].name)
                        }
                    } else if (opponentEntry.types.size == 2) {
                        NextOpponentType1ImageView.visibility = View.VISIBLE
                        NextOpponentType2ImageView.visibility = View.VISIBLE
                        NextOpponentType1ImageView.setImageResource(Utils.typeToRDrawable(opponentEntry.types[1].name))
                        NextOpponentType1ImageView.setOnClickListener {
                            (activity as MainActivity).typeHelp(opponentEntry.types[1].name)
                        }
                        NextOpponentType2ImageView.setImageResource(Utils.typeToRDrawable(opponentEntry.types[0].name))
                        NextOpponentType2ImageView.setOnClickListener {
                            (activity as MainActivity).typeHelp(opponentEntry.types[0].name)
                        }
                    }
                }
            pokeAPIService.getPokemon(opponentEntry.id).enqueue(opponentPokemonCallback)

            val entryClickListener = View.OnClickListener {
                val position = it.tag as Int

                val entry = pokedexEntries[position]

                SelectedPokemonTextView.text = Utils.firstLetterUpperCase(entry.name)
                SelectedPokemonImageView.visibility = View.VISIBLE
                Glide
                    .with(this@BattleLobbyFragment)
                    .load(entry.sprite)
                    .into(SelectedPokemonImageView)
                if (entry.types.size == 1) {
                    SelectedPokemonType1ImageView.visibility = View.VISIBLE
                    SelectedPokemonType2ImageView.visibility = View.INVISIBLE
                    SelectedPokemonType1ImageView.setImageResource(Utils.typeToRDrawable(entry.types[0].name))
                    SelectedPokemonType1ImageView.setOnClickListener {
                        (activity as MainActivity).typeHelp(entry.types[0].name)
                    }
                } else if (entry.types.size == 2) {
                    SelectedPokemonType1ImageView.visibility = View.VISIBLE
                    SelectedPokemonType2ImageView.visibility = View.VISIBLE
                    SelectedPokemonType1ImageView.setImageResource(Utils.typeToRDrawable(entry.types[1].name))
                    SelectedPokemonType1ImageView.setOnClickListener {
                        (activity as MainActivity).typeHelp(entry.types[1].name)
                    }
                    SelectedPokemonType2ImageView.setImageResource(Utils.typeToRDrawable(entry.types[0].name))
                    SelectedPokemonType2ImageView.setOnClickListener {
                        (activity as MainActivity).typeHelp(entry.types[0].name)
                    }
                }

                val selectedPokemonCallback : Callback<Pokemon> =
                    Utils.pokeAPICallback { response ->
                        selectedPokemon = response.body()!!
                    }

                pokeAPIService.getPokemon(entry.id).enqueue(selectedPokemonCallback)
            }

            PokemonList.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = PokedexEntryAdapter(pokedexEntries, entryClickListener)
            }
        }

    private fun pokemonSlotClickListener(slotIndex : Int, pokemonSlotNameTextView : TextView, pokemonSlotImageView : ImageView) : View.OnClickListener {
        return View.OnClickListener {
            if (selectedPokemon != null) {
                pokemonSlots[slotIndex] = selectedPokemon
                pokemonSlotNameTextView.text = Utils.firstLetterUpperCase(selectedPokemon!!.name)
                Glide
                    .with(this@BattleLobbyFragment)
                    .load(selectedPokemon!!.sprites.front_default)
                    .into(pokemonSlotImageView)

                if (!pokemonSlots.contains(null))
                    FightButton.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_battle_lobby, container, false)
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        surLeWebService.pokemonList().enqueue(pokemonListCallback)

        FightButton.setOnClickListener {
            (activity as MainActivity).loadBattle(arrayListOf(opponentPokemon.id, pokedexEntries.random().id, pokedexEntries.random().id), pokemonSlots)
        }

        val slot1ClickListener = pokemonSlotClickListener(0, pokemonSlotNameTextView, pokemonSlotImageView)
        val slot2ClickListener = pokemonSlotClickListener(1, pokemonSlotNameTextView2, pokemonSlotImageView2)
        val slot3ClickListener = pokemonSlotClickListener(2, pokemonSlotNameTextView3, pokemonSlotImageView3)

        pokemonSlotIdButton.setOnClickListener(slot1ClickListener)
        pokemonSlotIdButton2.setOnClickListener(slot2ClickListener)
        pokemonSlotIdButton3.setOnClickListener(slot3ClickListener)
    }
}
