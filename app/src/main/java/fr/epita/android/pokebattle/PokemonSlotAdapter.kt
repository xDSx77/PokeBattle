package fr.epita.android.pokebattle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.epita.android.pokebattle.Utils.firstLetterUpperCase
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.Pokemon
import kotlinx.android.synthetic.main.battle_lobby_pokemon_slot.view.*

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
            holder.view.PokemonSlotImageView.setImageResource(0)
            holder.view.PokemonSlotNameTextView.text = ""
            holder.view.PokemonSlotIdButton.text = (position + 1).toString()
        }
        else
        {
            holder.view.PokemonSlotNameTextView.text = firstLetterUpperCase(pokemon.name)
            Glide
                .with(holder.view.context)
                .load(pokemon.sprites.front_default)
                .into(holder.view.PokemonSlotImageView)
        }
    }

}