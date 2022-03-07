package fr.epita.android.pokebattle.battle.lobby

import android.content.Context
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
import fr.epita.android.pokebattle.battle.BattleLoadingScreenFragment
import fr.epita.android.pokebattle.main.MainActivity
import fr.epita.android.pokebattle.pokedex.PokedexEntryAdapter
import fr.epita.android.pokebattle.webservices.pokeapi.PokeAPIServiceHelper.pokeAPIService
import fr.epita.android.pokebattle.webservices.pokeapi.PokeAPIServiceHelper.pokeApiObserver
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.Pokemon
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_battle_lobby.*

class BattleLobbyFragment : Fragment() {

    private lateinit var opponentPokemon : Pokemon
    private var selectedPokemon : Pokemon? = null

    private val pokemonSlots : ArrayList<Pokemon?> = arrayListOf(null, null, null)

    private fun loadPokemonInfoIntoViews(pokemon : Pokemon?,
                                         pokemonNameTextView : TextView,
                                         pokemonImageView : ImageView,
                                         pokemonType1ImageView : ImageView,
                                         pokemonType2ImageView : ImageView) {
        pokemonNameTextView.text = Utils.firstLetterUpperCase(pokemon?.name!!)
        pokemonImageView.visibility = View.VISIBLE
        Glide
            .with(this@BattleLobbyFragment)
            .load(pokemon.sprites.front_default)
            .into(pokemonImageView)

        Utils.loadTypeIntoRightImageView(pokemon, activity as Context, pokemonType1ImageView, pokemonType2ImageView)
    }

    private fun showPokemons(pokemons : List<Pokemon>) {
        opponentPokemon = pokemons.random()

        loadPokemonInfoIntoViews(opponentPokemon, NextOpponentNameTextView, NextOpponentImageView, NextOpponentType1ImageView, NextOpponentType2ImageView)

        val entryClickListener = View.OnClickListener {
            val position = it.tag as Int
            val pokemon = pokemons[position]

            loadPokemonInfoIntoViews(pokemon, SelectedPokemonTextView, SelectedPokemonImageView, SelectedPokemonType1ImageView, SelectedPokemonType2ImageView)
            selectedPokemon = pokemon
        }

        FightButton.setOnClickListener {
            (activity as MainActivity).loadBattle(arrayListOf(opponentPokemon.id, pokemons.random().id, pokemons.random().id), pokemonSlots)
        }

        PokemonList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = PokedexEntryAdapter(pokemons, entryClickListener)
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

    private fun buildAllNatures() {
        pokeAPIService.getNatures()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(pokeApiObserver { naturesResourceList ->
                for (natureResource in naturesResourceList.results) {
                    pokeAPIService.getNatureByName(natureResource.name)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(pokeApiObserver { nature ->
                            BattleLoadingScreenFragment.allNaturesList.add(nature)
                        })
                }
            })
    }

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_battle_lobby, container, false)
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buildAllNatures()

        Utils.buildAllPokemonSpecies { pokemons -> showPokemons(pokemons) }

        val slot1ClickListener = pokemonSlotClickListener(0, pokemonSlotNameTextView, pokemonSlotImageView)
        val slot2ClickListener = pokemonSlotClickListener(1, pokemonSlotNameTextView2, pokemonSlotImageView2)
        val slot3ClickListener = pokemonSlotClickListener(2, pokemonSlotNameTextView3, pokemonSlotImageView3)

        pokemonSlotIdButton.setOnClickListener(slot1ClickListener)
        pokemonSlotIdButton2.setOnClickListener(slot2ClickListener)
        pokemonSlotIdButton3.setOnClickListener(slot3ClickListener)
    }
}
