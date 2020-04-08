package fr.epita.android.pokebattle

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

class PokedexDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pokedex_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val id : Int? = arguments!!.getInt("pokemonId")
        if (id != null) {
            //TODO: set the fields in the pokedex details layout
            // Set "TypeImageView" if the pokemon has just 1 type and hide "Type1ImageView" and "Type2ImageView"
            // Set "Type1ImageView" and "Type2ImageView" if the pokemon has 2 types and hide "TypeImageView"
        }
    }
}
