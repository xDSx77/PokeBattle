package fr.epita.android.pokebattle

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import fr.epita.android.pokebattle.Utils.firstLetterUpperCase
import fr.epita.android.pokebattle.Utils.typeToRDrawable
import fr.epita.android.pokebattle.webservices.pokeapi.PokeAPIInterface
import fr.epita.android.pokebattle.webservices.pokeapi.moves.Move
import fr.epita.android.pokebattle.webservices.pokeapi.moves.MoveCategory
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.Pokemon
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.PokemonType
import fr.epita.android.pokebattle.webservices.pokeapi.resourcelist.NamedAPIResourceList
import fr.epita.android.pokebattle.webservices.pokeapi.utils.NamedAPIResource
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.type.Type
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.type.TypeRelations
import kotlinx.android.synthetic.main.fragment_battle.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


class BattleFragment : Fragment() {

    data class PokemonInfo(
        var pokemon : Pokemon,
        var moves : List<Move?>,
        var hp : Int
    )

    private val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())
    private val retrofit = Retrofit.Builder()
        .baseUrl(PokeAPIInterface.Constants.url)
        .addConverterFactory(jsonConverter)
        .build()
    private val service =  retrofit.create(PokeAPIInterface::class.java)

    private var allTypes : MutableList<String> = mutableListOf()
    private var allMovesTypesDamageRelations = mutableMapOf<String, TypeRelations>()

    private lateinit var allDamageMovesList : MoveCategory
    private lateinit var opponentPokemon1 : PokemonInfo
    private lateinit var opponentPokemon2 : PokemonInfo
    private lateinit var opponentPokemon3 : PokemonInfo
    private lateinit var pokemon1 : PokemonInfo
    private lateinit var pokemon2 : PokemonInfo
    private lateinit var pokemon3 : PokemonInfo

    private lateinit var battlingPokemon : PokemonInfo
    private lateinit var opponentBattlingPokemon : PokemonInfo

    private val actions : Queue<() -> Unit> = LinkedList()

    private fun <T> pokeAPICallback(onResponse : (Response<T>) -> Unit) : Callback<T> {
        return object : Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) {
                Log.w("WebServices", "Poke API call failed" + t.message)
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                Log.w("WebServices", "Poke API call success")
                if (response.code() == 200)
                    onResponse(response)
            }
        }
    }

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

    private fun getPokemonMoves(pok : Pokemon) : List<Move?> {
        val moves = mutableListOf<Move?>()
        val allPokemonMoves = pok.moves.shuffled()
        var movesCount = 0
        val moveCallback : Callback<Move> = pokeAPICallback { response ->
            moves.add(response.body()!!)
        }
        for (pokemonMove in allPokemonMoves){
            if (movesCount == 4)
                break
            if (allDamageMovesList.moves.any { it.name == pokemonMove.move.name }) {
                service.getMove(pokemonMove.move.name).enqueue(moveCallback)
                movesCount++
            }
        }
        return moves
    }

    private fun opponentTurn(next : () -> Unit = { nextAction(); }) {
        attack(1, opponentBattlingPokemon, battlingPokemon, battlingPokemonHealth, next)
    }

    private fun showMessage(m : String) {
        MessageTextView.text = m
        MessageTextView.visibility = View.VISIBLE
        move1.visibility = View.INVISIBLE
        move1Type.visibility = View.INVISIBLE
        move1Description.visibility = View.INVISIBLE
        move2.visibility = View.INVISIBLE
        move2Type.visibility = View.INVISIBLE
        move2Description.visibility = View.INVISIBLE
        move3.visibility = View.INVISIBLE
        move3Type.visibility = View.INVISIBLE
        move3Description.visibility = View.INVISIBLE
        move4.visibility = View.INVISIBLE
        move4Type.visibility = View.INVISIBLE
        move4Description.visibility = View.INVISIBLE
    }

    private fun showMoves() {
        MessageTextView.visibility = View.INVISIBLE
        move1.visibility = View.VISIBLE
        move1Type.visibility = View.VISIBLE
        move1Description.visibility = View.VISIBLE
        move2.visibility = View.VISIBLE
        move2Type.visibility = View.VISIBLE
        move2Description.visibility = View.VISIBLE
        move3.visibility = View.VISIBLE
        move3Type.visibility = View.VISIBLE
        move3Description.visibility = View.VISIBLE
        move4.visibility = View.VISIBLE
        move4Type.visibility = View.VISIBLE
        move4Description.visibility = View.VISIBLE
    }

    private fun setOpponentPokemon(pok : PokemonInfo) {
        opponentBattlingPokemon = pok
        Glide
            .with(this@BattleFragment)
            .load(pok.pokemon.sprites.front_default)
            .into(opponentPokemonImageView)
        opponentPokemonHealth.max = pok.pokemon.stats.find { it.stat.name == "hp" }!!.base_stat
        opponentPokemonHealth.progress = pok.hp
        opponentPokemonName.text = firstLetterUpperCase(pok.pokemon.name)
    }

    private fun setBattlingPokemon(pok : PokemonInfo) {
        battlingPokemon = pok
        val battlingSprite =
            if (pok.pokemon.sprites.back_default.isNullOrBlank())
                pok.pokemon.sprites.front_default
            else
                pok.pokemon.sprites.back_default
        Glide
            .with(this@BattleFragment)
            .load(battlingSprite)
            .into(battlingPokemonImageView)
        battlingPokemonHealth.max = pok.pokemon.stats.find { it.stat.name == "hp" }!!.base_stat
        battlingPokemonHealth.progress = pok.hp
        battlingPokemonName.text = firstLetterUpperCase(pok.pokemon.name)
        setBattlingMove(1, pok.moves[0]!!)
        setBattlingMove(2, pok.moves[1]!!)
        setBattlingMove(3, pok.moves[2]!!)
        setBattlingMove(4, pok.moves[3]!!)
    }

    private fun playerTurn(moveId : Int) {
        if (opponentBattlingPokemon.pokemon.stats.find { it.stat.name == "speed" }!!.base_stat > battlingPokemon.pokemon.stats.find { it.stat.name == "speed" }!!.base_stat)
            opponentTurn { attack(moveId, battlingPokemon, opponentBattlingPokemon, opponentPokemonHealth) }
        else
            attack(moveId, battlingPokemon, opponentBattlingPokemon, opponentPokemonHealth) { opponentTurn() }
    }

    private fun greyImage(img : ImageView) {
        val matrix = ColorMatrix()
        matrix.setSaturation(0f) // 0 means grayscale
        val cf = ColorMatrixColorFilter(matrix)
        img.colorFilter = cf
        img.imageAlpha = 128 // 128 = 0.5
    }

    private fun handlePokemonKO(pok : PokemonInfo) {
        val pokemonName = firstLetterUpperCase(pok.pokemon.name)
        doOrAddAction { showMessage("$pokemonName fainted!"); }
        if (pok == battlingPokemon) {
            when (pok) {
                pokemon1 -> {
                    greyImage(pokemon1ImageView)
                    pokemon1ImageView.setOnClickListener(null)
                }
                pokemon2 -> {
                    greyImage(pokemon2ImageView)
                    pokemon2ImageView.setOnClickListener(null)
                }
                pokemon3 -> {
                    greyImage(pokemon3ImageView)
                    pokemon3ImageView.setOnClickListener(null)
                }
            }

            val pokemon = listOf(pokemon1, pokemon2, pokemon3)
            val newPokemon = pokemon.find { it.hp > 0 }
            if (newPokemon == null) {
                actions.add {
                    showMessage("You lost !")
                    MessageTextView.setOnClickListener(null)
                    greyImage(battlingPokemonImageView)
                }
            }
            else
                setBattlingPokemon(newPokemon)
        }
        else {
            val opponents = listOf(opponentPokemon1, opponentPokemon2, opponentPokemon3)
            val newOpponent = opponents.find { it.hp > 0 }
            if (newOpponent == null) {
                actions.add {
                    showMessage("You won!")
                    MessageTextView.setOnClickListener(null)
                    pokemon1ImageView.setOnClickListener(null)
                    pokemon2ImageView.setOnClickListener(null)
                    pokemon3ImageView.setOnClickListener(null)
                    greyImage(opponentPokemonImageView)
                }
            }
            else
                setOpponentPokemon(newOpponent)
        }
    }

    private fun getEfficiency(moveType : NamedAPIResource, pokemonType : PokemonType) : Double {
        val moveTypesDamageRelations = allMovesTypesDamageRelations[moveType.name]!!
        if (moveTypesDamageRelations.no_damage_to.any {  it.name == pokemonType.type.name })
            return 0.0
        if (moveTypesDamageRelations.double_damage_to.any {  it.name == pokemonType.type.name })
            return 2.0
        if (moveTypesDamageRelations.half_damage_to.any {  it.name == pokemonType.type.name })
            return 0.5
        return 1.0
    }

    private fun dealDamage(move : Move, attacker : PokemonInfo, defender: PokemonInfo, health: ProgressBar) : Boolean {
        val moveName = firstLetterUpperCase(move.name)
        val random = (1..100).random()
        if (random > move.accuracy) {
            doOrAddAction { showMessage("$moveName missed!"); }
            return true
        }
        var efficiency = 1.0
        for (pokemonType in defender.pokemon.types)
            efficiency *= getEfficiency(move.type, pokemonType)
        when {
            efficiency == 0.0 -> doOrAddAction { showMessage("$moveName has no effect..."); }
            efficiency < 1 -> doOrAddAction { showMessage("$moveName is not very effective..."); }
            efficiency > 1 -> doOrAddAction { showMessage("$moveName is super effective!"); }
        }
        if (efficiency == 0.0)
            return true
        val (att, def) = when (move.damage_class.name) {
            "physical" -> Pair(
                attacker.pokemon.stats.find { it.stat.name == "attack" }!!.base_stat,
                defender.pokemon.stats.find { it.stat.name == "defense" }!!.base_stat
            )
            "special" -> Pair(
                attacker.pokemon.stats.find { it.stat.name == "special-attack" }!!.base_stat,
                defender.pokemon.stats.find { it.stat.name == "special-defense" }!!.base_stat
            )
            else -> Pair(0, 0)
        }
        if (move.power == null)
            move.power = 0
        defender.hp -= maxOf(((att / 10 + move.power!! - def) * efficiency).toInt(), 1)
        defender.hp = maxOf(defender.hp, 0)
        health.progress = defender.hp
        return efficiency != 1.0
    }

    private fun attack(moveId : Int, attacker : PokemonInfo, defender : PokemonInfo, health : ProgressBar, next : () -> Unit = { nextAction(); }) {
        val move = attacker.moves[moveId - 1]!!
        val attackerName = firstLetterUpperCase(attacker.pokemon.name)
        val moveName = firstLetterUpperCase(move.name)
        doOrAddAction { showMessage("$attackerName used $moveName") }
        actions.add {
            val message = dealDamage(move, attacker, defender, health)
            if (defender.hp > 0) {
                if (!message)
                    next()
                else
                    actions.add { next(); }
            }
            else {
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
        txt!!.text = firstLetterUpperCase(move.name)
        when (id) {
            1 -> move1Description.text = move.flavor_text_entries[2].flavor_text
            2 -> move2Description.text = move.flavor_text_entries[2].flavor_text
            3 -> move3Description.text = move.flavor_text_entries[2].flavor_text
            4 -> move4Description.text = move.flavor_text_entries[2].flavor_text
        }
        Glide
            .with(this@BattleFragment)
            .load(typeToRDrawable(move.type.name))
            .into(img!!)
    }

    private fun getTypesDamageRelations()
    {
        for (typeName in allTypes) {
            val typeCallback: Callback<Type> = pokeAPICallback { response ->
                val type: Type = response.body()!!
                allMovesTypesDamageRelations[typeName] = type.damage_relations
            }
            service.getTypeByName(typeName).enqueue(typeCallback)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val opponent1Callback : Callback<Pokemon> = pokeAPICallback { response ->
                val pok = response.body()!!
                val moves : List<Move?> = getPokemonMoves(pok)
                opponentPokemon1 = PokemonInfo(pok, moves, pok.stats.find { it.stat.name == "hp" }!!.base_stat)
                setOpponentPokemon(opponentPokemon1)
                showMessage("A wild " + opponentPokemonName.text + " has appeared!")
            }
            val opponent2Callback : Callback<Pokemon> = pokeAPICallback { response ->
                val pok = response.body()!!
                val moves : List<Move?> = getPokemonMoves(pok)
                opponentPokemon2 = PokemonInfo(pok, moves, pok.stats.find { it.stat.name == "hp" }!!.base_stat)
            }
            val opponent3Callback : Callback<Pokemon> = pokeAPICallback { response ->
                val pok = response.body()!!
                val moves : List<Move?> = getPokemonMoves(pok)
                opponentPokemon3 = PokemonInfo(pok, moves, pok.stats.find { it.stat.name == "hp" }!!.base_stat)
            }
            val pokemon1Callback : Callback<Pokemon> = pokeAPICallback { response ->
                val pok = response.body()!!
                val moves : List<Move?> = getPokemonMoves(pok)
                pokemon1 = PokemonInfo(pok, moves, pok.stats.find { it.stat.name == "hp" }!!.base_stat)
                battlingPokemon = pokemon1
                val battlingSprite =
                    if (pokemon1.pokemon.sprites.back_default.isNullOrBlank())
                        pokemon1.pokemon.sprites.front_default
                    else
                        pokemon1.pokemon.sprites.back_default
                Glide
                    .with(this@BattleFragment)
                    .load(battlingSprite)
                    .into(battlingPokemonImageView)
                Glide
                    .with(this@BattleFragment)
                    .load(pokemon1.pokemon.sprites.front_default)
                    .into(pokemon1ImageView)
                battlingPokemonHealth.max = pokemon1.hp
                battlingPokemonHealth.progress = pokemon1.hp
                battlingPokemonName.text = firstLetterUpperCase(pok.name)
                pokemon1Name.text = battlingPokemonName.text
                pokemon1ImageView.setOnClickListener {
                    if (battlingPokemon != pokemon1)
                        setBattlingPokemon(pokemon1)
                }
            }
            val pokemon2Callback : Callback<Pokemon> = pokeAPICallback { response ->
                val pok = response.body()!!
                val moves : List<Move?> = getPokemonMoves(pok)
                pokemon2 = PokemonInfo(pok, moves, pok.stats.find { it.stat.name == "hp" }!!.base_stat)
                Glide
                    .with(this@BattleFragment)
                    .load(pokemon2.pokemon.sprites.front_default)
                    .into(pokemon2ImageView)
                pokemon2Name.text = firstLetterUpperCase(pok.name)
                pokemon2ImageView.setOnClickListener {
                    if (battlingPokemon != pokemon2)
                        setBattlingPokemon(pokemon2)
                }
            }
            val pokemon3Callback : Callback<Pokemon> = pokeAPICallback { response ->
                val pok = response.body()!!
                val moves : List<Move?> = getPokemonMoves(pok)
                pokemon3 = PokemonInfo(pok, moves, pok.stats.find { it.stat.name == "hp" }!!.base_stat)
                Glide
                    .with(this@BattleFragment)
                    .load(pokemon3.pokemon.sprites.front_default)
                    .into(pokemon3ImageView)
                pokemon3Name.text = firstLetterUpperCase(pok.name)
                pokemon3ImageView.setOnClickListener {
                    if (battlingPokemon != pokemon3)
                        setBattlingPokemon(pokemon3)
                }
            }
            val movesCallback : Callback<MoveCategory> = pokeAPICallback { response ->
                allDamageMovesList = response.body()!!
                service.getPokemon(it.getInt("opponentPokemon0")).enqueue(opponent1Callback)
                service.getPokemon(it.getInt("opponentPokemon1")).enqueue(opponent2Callback)
                service.getPokemon(it.getInt("opponentPokemon2")).enqueue(opponent3Callback)
                service.getPokemon(it.getInt("pokemon0")).enqueue(pokemon1Callback)
                service.getPokemon(it.getInt("pokemon1")).enqueue(pokemon2Callback)
                service.getPokemon(it.getInt("pokemon2")).enqueue(pokemon3Callback)
            }
            val typesCallback : Callback<NamedAPIResourceList> = pokeAPICallback { response ->
                val types = response.body()!!
                for (type in types.results){
                    allTypes.add(type.name)
                }
            }
            service.getAllDamageMoves().enqueue(movesCallback)
            service.getTypes().enqueue(typesCallback)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MessageTextView.setOnClickListener {
            setBattlingMove(1, battlingPokemon.moves[0]!!)
            setBattlingMove(2, battlingPokemon.moves[1]!!)
            setBattlingMove(3, battlingPokemon.moves[2]!!)
            setBattlingMove(4, battlingPokemon.moves[3]!!)
            nextAction()
            if (allMovesTypesDamageRelations.isEmpty())
                getTypesDamageRelations()
            MessageTextView.setOnClickListener { nextAction() }
        }
        move1.setOnClickListener { playerTurn(1); }
        move2.setOnClickListener { playerTurn(2); }
        move3.setOnClickListener { playerTurn(3); }
        move4.setOnClickListener { playerTurn(4); }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_battle, container, false)
    }
}
