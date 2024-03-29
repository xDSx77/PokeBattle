package fr.epita.android.pokebattle.pokedex

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.epita.android.pokebattle.R
import fr.epita.android.pokebattle.Utils
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.Pokemon
import kotlinx.android.synthetic.main.pokedex_entry.view.*

class PokedexEntryAdapter(private val pokemons : List<Pokemon>,
                          private val entryClickListener: View.OnClickListener)
    : RecyclerView.Adapter<PokedexEntryAdapter.PokedexEntryViewHolder>() {

    class PokedexEntryViewHolder(val view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokedexEntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pokedex_entry, parent, false)
        view.setOnClickListener(entryClickListener)
        return PokedexEntryViewHolder(view)
    }

    override fun getItemCount() = pokemons.size

    override fun onBindViewHolder(holder: PokedexEntryViewHolder, position: Int) {
        val pokemon = pokemons[position]
        val view : View = holder.view
        view.tag = position
        val pokemonNumber = pokemon.id
        when {
            pokemonNumber >= 100 -> view.pokemonNumber_textView.text = pokemonNumber.toString()
            pokemonNumber in 10..99 -> view.pokemonNumber_textView.text =
                holder.view.context.getString(R.string.pokemonNumber10_99, pokemonNumber)
            else -> holder.view.pokemonNumber_textView.text =
                holder.view.context.getString(R.string.pokemonNumber0_9, pokemonNumber)
        }

        Glide
            .with(view)
            .load(pokemon.sprites.front_default)
            .into(view.pokemon_imageView)
        view.name_textView.text = Utils.firstLetterUpperCase(pokemon.name)

        Utils.loadTypeIntoRightImageView(pokemon, view.context, view.type1_imageView, view.type2_imageView)
    }
}