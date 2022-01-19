package fr.epita.android.pokebattle.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.epita.android.pokebattle.R
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        BattleButton.setOnClickListener {
            (activity as MainActivity).BattleLobby()
        }
        PokedexButton.setOnClickListener {
            (activity as MainActivity).PokedexList()
        }
    }
}
