package fr.epita.android.pokebattle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.epita.android.pokebattle.webservices.Pokemon
import kotlinx.android.synthetic.main.battle_lobby_pokemon_slot.view.*
import java.util.*

class PokemonSlotAdapter(private val pokemonSlots : List<Pokemon?>,
                         private val slotClickListener : View.OnClickListener)
    : RecyclerView.Adapter<PokemonSlotAdapter.PokemonSlotViewHolder>() {

    class PokemonSlotViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonSlotViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.battle_lobby_pokemon_slot, parent, false)
        view.PokemonSlotIdButton.setOnClickListener(slotClickListener)
        return PokemonSlotViewHolder(view)
    }

    override fun getItemCount() = pokemonSlots.size

    override fun onBindViewHolder(holder: PokemonSlotViewHolder, position: Int) {
        var pokemon = pokemonSlots[position]
        if (pokemon == null) {
            holder.view.tag = position
            holder.view.PokemonSlotImageView.setImageResource(0);
            holder.view.PokemonSlotNameTextView.text = ""
            holder.view.PokemonSlotIdButton.text = (position + 1).toString()
        }
        else
        {
            holder.view.PokemonSlotNameTextView.text =
                pokemon.name.substring(0, 1).toUpperCase(Locale.getDefault())
                    .plus(pokemon.name.substring(1))
            Glide
                .with(holder.view.context)
                .load(pokemon.sprites.front_default)
                .into(holder.view.PokemonSlotImageView)
        }
    }

}