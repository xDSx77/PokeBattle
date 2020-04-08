package fr.epita.android.pokebattle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.epita.android.pokebattle.webservices.PokedexEntry
import kotlinx.android.synthetic.main.pokedex_entry.view.*

class PokedexEntryAdapter(private val pokedexEntries : List<PokedexEntry>)
    : RecyclerView.Adapter<PokedexEntryAdapter.PokedexEntryViewHolder>() {

    class  PokedexEntryViewHolder(val view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokedexEntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pokedex_entry, parent, false)
        return PokedexEntryViewHolder(view);
    }

    override fun getItemCount() = pokedexEntries.size

    override fun onBindViewHolder(holder: PokedexEntryViewHolder, position: Int) {
        var pokedexEntry = pokedexEntries[position];
        holder.view.name_textView.text = pokedexEntry.name;
        holder.view.type1_imageView.setImageResource(pokedexEntry.types[0].ToRDrawable());
        if (pokedexEntry.types.size > 1)
            holder.view.type2_imageView.setImageResource(pokedexEntry.types[1].ToRDrawable());
    }
}