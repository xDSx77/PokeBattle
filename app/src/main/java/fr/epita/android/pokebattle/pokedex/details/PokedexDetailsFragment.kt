package fr.epita.android.pokebattle.pokedex.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import fr.epita.android.pokebattle.R
import fr.epita.android.pokebattle.Utils
import fr.epita.android.pokebattle.main.MainActivity
import fr.epita.android.pokebattle.webservices.pokeapi.PokeAPIServiceHelper
import fr.epita.android.pokebattle.webservices.pokeapi.PokeAPIServiceHelper.pokeAPIService
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.Pokemon
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.PokemonStat
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_pokedex_details.*

class PokedexDetailsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pokedex_details, container, false)
    }

    private fun showPokemonDetails(pokemon : Pokemon) {
        Glide
            .with(this@PokedexDetailsFragment)
            .load(pokemon.sprites.front_default)
            .into(PokemonImageView)
        PokemonNameTextView.text = Utils.firstLetterUpperCase(pokemon.name)
        HeightValueTextView.text = pokemon.height.toString()
        WeightValueTextView.text = pokemon.weight.toString()
        for (stat : PokemonStat in pokemon.stats) {
            when (stat.stat.name) {
                "speed" -> SpeedValueTextView.text = stat.base_stat.toString()
                "hp" -> HPValueTextView.text = stat.base_stat.toString()
                "attack" -> AttackValueTextView.text = stat.base_stat.toString()
                "defense" -> DefenseValueTextView.text = stat.base_stat.toString()
                "special-attack" -> SpecialAttackValueTextView.text = stat.base_stat.toString()
                "special-defense" -> SpecialDefenseValueTextView.text = stat.base_stat.toString()
            }
        }
        when (pokemon.types.size) {
            2 -> {
                TypeImageView.visibility = View.INVISIBLE
                Type1ImageView.visibility = View.VISIBLE
                Type1ImageView.setImageResource(Utils.typeToRDrawable(pokemon.types[0].type.name))
                Type1ImageView.setOnClickListener {
                    (view?.context as MainActivity).typeHelp(pokemon.types[0].type.name)
                }
                Type2ImageView.visibility = View.VISIBLE
                Type2ImageView.setImageResource(Utils.typeToRDrawable(pokemon.types[1].type.name))
                Type2ImageView.setOnClickListener {
                    (view?.context as MainActivity).typeHelp(pokemon.types[1].type.name)
                }
            }
            1 -> {
                Type1ImageView.visibility = View.INVISIBLE
                Type2ImageView.visibility = View.INVISIBLE
                TypeImageView.visibility = View.VISIBLE
                TypeImageView.setImageResource(Utils.typeToRDrawable(pokemon.types[0].type.name))
                TypeImageView.setOnClickListener {
                    (view?.context as MainActivity).typeHelp(pokemon.types[0].type.name)
                }
            }
            else -> Log.w("Pokemon types", "No type found for this pokemon")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val id : Int = requireArguments().getInt("pokemonId")
        if (id != 0) {
            pokeAPIService.getPokemon(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(PokeAPIServiceHelper.pokeApiObserver { pokemon ->
                    showPokemonDetails(pokemon)
                })
        }
    }
}
