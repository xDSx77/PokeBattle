package fr.epita.android.pokebattle.battle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import fr.epita.android.pokebattle.Globals
import fr.epita.android.pokebattle.R
import fr.epita.android.pokebattle.Utils
import fr.epita.android.pokebattle.main.MainActivity
import fr.epita.android.pokebattle.webservices.pokeapi.moves.Move
import kotlinx.android.synthetic.main.fragment_battle.*
import java.util.*
import kotlin.math.max

class BattleFragment : Fragment() {

    private var playerTurn = false

    companion object {
        var opponentPokemon1 : PokemonInfo = PokemonInfo(null, null, mutableListOf(), 0, 0, 0, null)
        var opponentPokemon2 : PokemonInfo = PokemonInfo(null, null, mutableListOf(), 0, 0, 0, null)
        var opponentPokemon3 : PokemonInfo = PokemonInfo(null, null, mutableListOf(), 0, 0, 0, null)
        var pokemon1 : PokemonInfo = PokemonInfo(null, null, mutableListOf(), 0, 0, 0, null)
        var pokemon2 : PokemonInfo = PokemonInfo(null, null, mutableListOf(), 0, 0, 0, null)
        var pokemon3 : PokemonInfo = PokemonInfo(null, null, mutableListOf(), 0, 0, 0, null)
    }

    private lateinit var battlingPokemon : PokemonInfo
    private lateinit var opponentBattlingPokemon : PokemonInfo

    private val actions : Queue<() -> Unit> = LinkedList()

    private fun doOrAddAction(action : () -> Unit) {
        if (actions.isEmpty())
            action()
        else
            actions.add { action() }
    }

    private fun nextAction() {
        if (actions.isEmpty())
            showMoves()
        else
            actions.remove()()
    }

    private fun opponentTurn(next : () -> Unit = { nextAction(); }) {
        playerTurn = false
        var moveId = 1
        var maxDamage = 0
        for (i in 0 until opponentBattlingPokemon.moves.count()) {
            val move = opponentBattlingPokemon.moves[i]!!
            val efficiency = DamageHelper.getEfficiency(move.type, battlingPokemon.pokemon!!.types)
            if (move.power == null)
                move.power = 0

            val damage = DamageHelper.computeDamage(opponentBattlingPokemon, battlingPokemon, move, false, efficiency).toInt()
            if (damage > maxDamage) {
                maxDamage = damage
                moveId = i + 1
            }
        }
        attack(moveId, opponentBattlingPokemon, battlingPokemon, battlingPokemonHealth, next)
    }

    private fun showMessage(message : String) {
        MessageTextView.text = message
        MessageTextView.visibility = View.VISIBLE
        movesGroup.visibility = View.INVISIBLE
    }

    private fun showMoves() {
        MessageTextView.visibility = View.INVISIBLE
        movesGroup.visibility = View.VISIBLE
    }

    private fun setOpponentPokemon(pok : PokemonInfo) {
        opponentBattlingPokemon = pok
        Glide
            .with(this@BattleFragment)
            .load(pok.pokemon!!.sprites.front_default)
            .into(opponentPokemonImageView)
        opponentPokemonHealth.max = pok.pokemon!!.stats.find { it.stat.name == "hp" }!!.modified_stat.toInt()
        opponentPokemonHealth.progress = pok.hp
        opponentPokemonMaxPV.text = opponentPokemonHealth.max.toString()
        opponentPokemonCurrentPV.text = pok.hp.toString()
        opponentPokemonName.text = Utils.firstLetterUpperCase(pok.pokemon!!.name)
    }

    private fun setBattlingPokemon(pok : PokemonInfo) {
        battlingPokemon = pok
        val battlingSprite =
            if (pok.pokemon!!.sprites.back_default.isNullOrBlank())
                pok.pokemon!!.sprites.front_default
            else
                pok.pokemon!!.sprites.back_default
        Glide
            .with(this@BattleFragment)
            .load(battlingSprite)
            .into(battlingPokemonImageView)
        battlingPokemonHealth.max = pok.pokemon!!.stats.find { it.stat.name == "hp" }!!.modified_stat.toInt()
        battlingPokemonHealth.progress = pok.hp
        battlingPokemonMaxPV.text = battlingPokemonHealth.max.toString()
        battlingPokemonCurrentPV.text = pok.hp.toString()
        battlingPokemonName.text = Utils.firstLetterUpperCase(pok.pokemon!!.name)
        setBattlingMove(1, pok.moves[0]!!)
        setBattlingMove(2, pok.moves[1]!!)
        setBattlingMove(3, pok.moves[2]!!)
        setBattlingMove(4, pok.moves[3]!!)
    }

    private fun playerTurn(moveId : Int) {
        val opponentPokemonSpeed = opponentBattlingPokemon.pokemon!!.stats.find { it.stat.name == "speed" }!!.modified_stat
        val battlingPokemonSpeed = battlingPokemon.pokemon!!.stats.find { it.stat.name == "speed" }!!.modified_stat
        if (opponentPokemonSpeed > battlingPokemonSpeed)
            opponentTurn {
                playerTurn = true
                attack(moveId, battlingPokemon, opponentBattlingPokemon, opponentPokemonHealth) { playerTurn = true }
            }
        else
            attack(moveId, battlingPokemon, opponentBattlingPokemon, opponentPokemonHealth) {
                opponentTurn {
                    playerTurn = true
                }
            }
    }

    private fun handlePokemonKO(pok : PokemonInfo) {
        val pokemonName = Utils.firstLetterUpperCase(pok.pokemon!!.name)
        doOrAddAction { showMessage("$pokemonName fainted!"); }
        if (pok == battlingPokemon) {
            when (pok) {
                pokemon1 -> {
                    Utils.greyImage(pokemon1ImageView)
                    pokemon1ImageView.setOnClickListener(null)
                }
                pokemon2 -> {
                    Utils.greyImage(pokemon2ImageView)
                    pokemon2ImageView.setOnClickListener(null)
                }
                pokemon3 -> {
                    Utils.greyImage(pokemon3ImageView)
                    pokemon3ImageView.setOnClickListener(null)
                }
            }

            val pokemon = listOf(pokemon1, pokemon2, pokemon3)
            val newPokemon = pokemon.find { it.hp > 0 }
            if (newPokemon == null) {
                actions.add {
                    showMessage("You lost !")
                    MessageTextView.setOnClickListener { (activity as MainActivity).home(); }
                    Utils.greyImage(battlingPokemonImageView)
                }
            } else
                setBattlingPokemon(newPokemon)
        } else {
            val opponents = listOf(opponentPokemon1, opponentPokemon2, opponentPokemon3)
            val newOpponent = opponents.find { it.hp > 0 }
            if (newOpponent == null) {
                actions.add {
                    showMessage("You won !")
                    MessageTextView.setOnClickListener { (activity as MainActivity).home(); }
                    pokemon1ImageView.setOnClickListener(null)
                    pokemon2ImageView.setOnClickListener(null)
                    pokemon3ImageView.setOnClickListener(null)
                    Utils.greyImage(opponentPokemonImageView)
                }
            } else
                setOpponentPokemon(newOpponent)
        }
    }

    private fun dealDamage(move : Move, attacker : PokemonInfo, defender : PokemonInfo, health : ProgressBar) : Boolean {
        val moveName = Utils.firstLetterUpperCase(move.name)
        if (!DamageHelper.moveHit(move, attacker, defender)) {
            doOrAddAction { showMessage("$moveName missed!"); }
            return true
        }
        val efficiency = DamageHelper.getEfficiency(move.type, defender.pokemon!!.types)
        when {
            efficiency == 0.0 -> doOrAddAction { showMessage("$moveName has no effect..."); }
            efficiency < 1 -> doOrAddAction { showMessage("$moveName is not very effective..."); }
            efficiency > 1 -> doOrAddAction { showMessage("$moveName is super effective!"); }
        }
        if (efficiency == 0.0)
            return true

        if (move.power == null)
            move.power = 0

        val attackerSpeed = attacker.pokemon!!.stats.find { t -> t.stat.name == "speed" }!!
        val critical = if (Globals.GENERATION.generation == 1) DamageHelper.criticalGen1(attackerSpeed) else DamageHelper.critical()
        if (critical)
            showMessage("A critical hit!")
        val damage = DamageHelper.computeDamage(attacker, defender, move, critical, efficiency)

        defender.hp -= max(damage.toInt(), 1)
        defender.hp = max(defender.hp, 0)
        health.progress = defender.hp
        if (defender == opponentBattlingPokemon) {
            opponentPokemonCurrentPV.text = defender.hp.toString()
        } else {
            battlingPokemonCurrentPV.text = defender.hp.toString()
        }
        return efficiency != 1.0
    }

    private fun attack(moveId : Int, attacker : PokemonInfo, defender : PokemonInfo, health : ProgressBar, next : () -> Unit = { nextAction(); }) {
        playerTurn = false
        val move = attacker.moves[moveId - 1]!!
        val attackerName = Utils.firstLetterUpperCase(attacker.pokemon!!.name)
        val moveName = Utils.firstLetterUpperCase(move.name)
        doOrAddAction { showMessage("$attackerName used $moveName") }
        actions.add {
            val message = dealDamage(move, attacker, defender, health)
            if (defender.hp > 0) {
                if (!message)
                    next()
                else
                    actions.add { next(); }
            } else {
                if (!message)
                    handlePokemonKO(defender)
                else
                    actions.add { handlePokemonKO(defender); }
            }
        }
    }

    private fun setBattlingMove(id : Int, move : Move) {
        val (txt, img) = when (id) {
            1 -> Pair(move1, move1Type)
            2 -> Pair(move2, move2Type)
            3 -> Pair(move3, move3Type)
            4 -> Pair(move4, move4Type)
            else -> Pair(null, null)
        }
        txt?.let {
            it.text = Utils.firstLetterUpperCase(move.name)
        }
        when (id) {
            1 -> move1Description.text = move.flavor_text_entries[2].flavor_text
            2 -> move2Description.text = move.flavor_text_entries[2].flavor_text
            3 -> move3Description.text = move.flavor_text_entries[2].flavor_text
            4 -> move4Description.text = move.flavor_text_entries[2].flavor_text
        }
        img?.let {
            Glide
                .with(this@BattleFragment)
                .load(Utils.typeToRDrawable(move.type.name))
                .into(it)
        }
    }

    private fun loadPokemon(pokemonInfo : PokemonInfo, pokemonImageView : ImageView, pokemonNameTextView : TextView) {
        Glide
            .with(this@BattleFragment)
            .load(pokemonInfo.pokemon!!.sprites.front_default)
            .into(pokemonImageView)
        pokemonNameTextView.text = Utils.firstLetterUpperCase(pokemonInfo.pokemon!!.name)
        pokemonImageView.setOnClickListener {
            if (battlingPokemon != pokemonInfo && playerTurn) {
                setBattlingPokemon(pokemonInfo)
                opponentTurn { playerTurn = true }
            }
        }
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBattlingPokemon(pokemon1)
        battlingPokemonLevel.text = Globals.POKEMON_LEVEL.toString()

        setOpponentPokemon(opponentPokemon1)
        opponentPokemonLevel.text = Globals.POKEMON_LEVEL.toString()
        showMessage("A wild " + opponentPokemonName.text + " has appeared!")

        loadPokemon(pokemon1, pokemon1ImageView, pokemon1Name)
        loadPokemon(pokemon2, pokemon2ImageView, pokemon2Name)
        loadPokemon(pokemon3, pokemon3ImageView, pokemon3Name)

        MessageTextView.setOnClickListener {
            setBattlingMove(1, battlingPokemon.moves[0]!!)
            setBattlingMove(2, battlingPokemon.moves[1]!!)
            setBattlingMove(3, battlingPokemon.moves[2]!!)
            setBattlingMove(4, battlingPokemon.moves[3]!!)
            nextAction()
            playerTurn = true
            MessageTextView.setOnClickListener { nextAction() }
        }
        move1.setOnClickListener { playerTurn(1); }
        move1Type.setOnClickListener { playerTurn(1); }
        move1Description.setOnClickListener { playerTurn(1); }
        move2.setOnClickListener { playerTurn(2); }
        move2Type.setOnClickListener { playerTurn(2); }
        move2Description.setOnClickListener { playerTurn(2); }
        move3.setOnClickListener { playerTurn(3); }
        move3Type.setOnClickListener { playerTurn(3); }
        move3Description.setOnClickListener { playerTurn(3); }
        move4.setOnClickListener { playerTurn(4); }
        move4Type.setOnClickListener { playerTurn(4); }
        move4Description.setOnClickListener { playerTurn(4); }
    }

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_battle, container, false)
    }
}
