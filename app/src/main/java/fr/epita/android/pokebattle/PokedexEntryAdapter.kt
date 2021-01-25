package fr.epita.android.pokebattle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.epita.android.pokebattle.Utils.firstLetterUpperCase
import fr.epita.android.pokebattle.Utils.typeToRDrawable
import fr.epita.android.pokebattle.webservices.surleweb.api.PokedexEntry
import kotlinx.android.synthetic.main.pokedex_entry.view.*

class PokedexEntryAdapter(private val pokedexEntries : List<PokedexEntry>,
                          private val entryClickListener: View.OnClickListener)
    : RecyclerView.Adapter<PokedexEntryAdapter.PokedexEntryViewHolder>() {

    class PokedexEntryViewHolder(val view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokedexEntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pokedex_entry, parent, false)
        view.setOnClickListener(entryClickListener)
        return PokedexEntryViewHolder(view)
    }

    override fun getItemCount() = pokedexEntries.size

    override fun onBindViewHolder(holder: PokedexEntryViewHolder, position: Int) {
        val pokedexEntry = pokedexEntries[position]
        holder.view.tag = position
        val pokemonNumber = pokedexEntry.id
        when {
            pokemonNumber >= 100 -> holder.view.pokemonNumber_textView.text = pokemonNumber.toString()
            pokemonNumber in 10..99 -> holder.view.pokemonNumber_textView.text =
                holder.view.context.getString(R.string.pokemonNumber10_99, pokemonNumber)
            else -> holder.view.pokemonNumber_textView.text =
                holder.view.context.getString(R.string.pokemonNumber0_9, pokemonNumber)
        }

        Glide
            .with(holder.view)
            .load(pokedexEntry.sprite)
            .into(holder.view.pokemon_imageView)
        holder.view.name_textView.text = firstLetterUpperCase(pokedexEntry.name)

        if (pokedexEntry.types.size == 1) {
            holder.view.type1_imageView.setImageResource(typeToRDrawable(pokedexEntry.types[0].name))
            holder.view.type1_imageView.setOnClickListener {
                (holder.view.context as MainActivity).TypeHelp(pokedexEntry.types[0].name)
            }
            holder.view.type2_imageView.visibility = View.INVISIBLE
        }
        else if (pokedexEntry.types.size == 2) {
            holder.view.type2_imageView.visibility = View.VISIBLE
            holder.view.type1_imageView.setImageResource(typeToRDrawable(pokedexEntry.types[1].name))
            holder.view.type1_imageView.setOnClickListener {
                (holder.view.context as MainActivity).TypeHelp(pokedexEntry.types[1].name)
            }
            holder.view.type2_imageView.setImageResource(typeToRDrawable(pokedexEntry.types[0].name))
            holder.view.type2_imageView.setOnClickListener {
                (holder.view.context as MainActivity).TypeHelp(pokedexEntry.types[0].name)
            }
        }
    }
}