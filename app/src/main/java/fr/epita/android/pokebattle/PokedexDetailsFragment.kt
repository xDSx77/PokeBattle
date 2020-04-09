package fr.epita.android.pokebattle

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import fr.epita.android.pokebattle.webservices.PokeAPIInterface
import fr.epita.android.pokebattle.webservices.Pokemon
import fr.epita.android.pokebattle.webservices.PokemonStat
import kotlinx.android.synthetic.main.fragment_pokedex_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class PokedexDetailsFragment : Fragment() {
    lateinit var pokemon : Pokemon
    lateinit var pokemonResponse : Response<Pokemon>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pokedex_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val id : Int? = arguments!!.getInt("pokemonId")
        if (id != null) {
            // Use GSON library to create our JSON parser
            val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())

            // Create a Retrofit client object targeting the provided URL
            // and add a JSON converter (because we are expecting json responses)
            val retrofit = Retrofit.Builder()
                .baseUrl(PokeAPIInterface.Constants.url)
                .addConverterFactory(jsonConverter)
                .build()

            // Use the client to create a service:
            // an object implementing the interface to the WebService
            val service : PokeAPIInterface = retrofit.create(PokeAPIInterface::class.java)

            val pokemonCallback : Callback<Pokemon> = object : Callback<Pokemon> {
                override fun onFailure(call: Call<Pokemon>, t: Throwable) {
                    // Code here what happens if calling the WebService fails
                    Log.w("WebServices", "PokeAPI call failed" + t.message)
                }

                override fun onResponse(call: Call<Pokemon>, response: Response<Pokemon>) {
                    Log.w("WebServices", "PokeAPI call success")
                    if (response.code() == 200) {
                        // We got our data !
                        pokemonResponse = response
                        pokemon = response.body()!!;
                        Glide
                            .with(this@PokedexDetailsFragment)
                            .load(pokemon.sprites.front_default)
                            .into(PokemonImageView)
                        PokemonNameTextView.text = pokemon.name.substring(0, 1).toUpperCase() + pokemon.name.substring(1)
                        HeightValueTextView.text = pokemon.height.toString()
                        WeightValueTextView.text = pokemon.weight.toString()
                        for (stat: PokemonStat in pokemon.stats) {
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
                                TypeImageView.isVisible = false
                                Type1ImageView.isVisible = true
                                setImageResource(pokemon.types[0].type.name, Type1ImageView)
                                Type2ImageView.isVisible = true
                                setImageResource(pokemon.types[1].type.name, Type2ImageView)
                            }
                            1 -> {
                                Type1ImageView.isVisible = false
                                Type2ImageView.isVisible = false
                                TypeImageView.isVisible = true
                                setImageResource(pokemon.types[0].type.name, TypeImageView)
                            }
                            else -> Log.w("Pokemon types", "No type found for this pokemon")
                        }
                    }
                }

                private fun setImageResource(typeName: String, imageView: ImageView) {
                    when (typeName) {
                        "bug" -> imageView.setImageResource(R.drawable.bug)
                        "dark" -> imageView.setImageResource(R.drawable.dark)
                        "dragon" -> imageView.setImageResource(R.drawable.dragon)
                        "electric" -> imageView.setImageResource(R.drawable.electric)
                        "fairy" -> imageView.setImageResource(R.drawable.fairy)
                        "fighting" -> imageView.setImageResource(R.drawable.fighting)
                        "fire" -> imageView.setImageResource(R.drawable.fire)
                        "flying" -> imageView.setImageResource(R.drawable.flying)
                        "ghost" -> imageView.setImageResource(R.drawable.ghost)
                        "grass" -> imageView.setImageResource(R.drawable.grass)
                        "ground" -> imageView.setImageResource(R.drawable.ground)
                        "ice" -> imageView.setImageResource(R.drawable.ice)
                        "normal" -> imageView.setImageResource(R.drawable.normal)
                        "poison" -> imageView.setImageResource(R.drawable.poison)
                        "psychic" -> imageView.setImageResource(R.drawable.psychic)
                        "rock" -> imageView.setImageResource(R.drawable.rock)
                        "steel" -> imageView.setImageResource(R.drawable.steel)
                        "water" -> imageView.setImageResource(R.drawable.water)
                    }
                }
            }
            service.getPokemon(id).enqueue(pokemonCallback)
        }
    }
}
