package fr.epita.android.pokebattle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.epita.android.pokebattle.webservices.PokedexEntry
import kotlinx.android.synthetic.main.pokedex_entry.view.*
import java.util.*

class PokedexEntryAdapter(private val pokedexEntries : List<PokedexEntry>,
                          private val entryClickListener: View.OnClickListener)
    : RecyclerView.Adapter<PokedexEntryAdapter.PokedexEntryViewHolder>() {

    class  PokedexEntryViewHolder(val view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokedexEntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pokedex_entry, parent, false)
        view.setOnClickListener(entryClickListener)
        return PokedexEntryViewHolder(view)
    }

    override fun getItemCount() = pokedexEntries.size

    override fun onBindViewHolder(holder: PokedexEntryViewHolder, position: Int) {
        var pokedexEntry = pokedexEntries[position]
        holder.view.tag = position
        holder.view.name_textView.text = pokedexEntry.name.substring(0, 1).toUpperCase(Locale.getDefault()).plus(pokedexEntry.name.substring(1))
        holder.view.type1_imageView.setImageResource(pokedexEntry.types[0].toRDrawable())
        if (pokedexEntry.types.size > 1)
            holder.view.type2_imageView.setImageResource(pokedexEntry.types[1].toRDrawable())
    }
}