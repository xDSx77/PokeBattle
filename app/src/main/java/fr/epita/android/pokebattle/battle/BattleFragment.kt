package fr.epita.android.pokebattle.battle

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import fr.epita.android.pokebattle.Globals
import fr.epita.android.pokebattle.R
import fr.epita.android.pokebattle.Utils
import fr.epita.android.pokebattle.main.MainActivity
import fr.epita.android.pokebattle.webservices.pokeapi.PokeAPIServiceFragment
import fr.epita.android.pokebattle.webservices.pokeapi.moves.Move
import fr.epita.android.pokebattle.webservices.pokeapi.moves.MoveCategory
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.Pokemon
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.PokemonType
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.type.Type
import fr.epita.android.pokebattle.webservices.pokeapi.pokemon.type.TypeRelations
import fr.epita.android.pokebattle.webservices.pokeapi.resourcelist.NamedAPIResourceList
import fr.epita.android.pokebattle.webservices.pokeapi.utils.NamedAPIResource
import kotlinx.android.synthetic.main.fragment_battle.*
import retrofit2.Callback
import java.util.*
import kotlin.math.max


class BattleFragment : PokeAPIServiceFragment() {

    private var allTypes : MutableList<String> = mutableListOf()
    private var allMovesTypesDamageRelations = mutableMapOf<String, TypeRelations>()
    private var allDamageMovesList : MutableList<NamedAPIResource> = mutableListOf()

    private var playerTurn = true

    private lateinit var opponentPokemon1 : PokemonInfo
    private lateinit var opponentPokemon2 : PokemonInfo
    private lateinit var opponentPokemon3 : PokemonInfo
    private lateinit var pokemon1 : PokemonInfo
    private lateinit var pokemon2 : PokemonInfo
    private lateinit var pokemon3 : PokemonInfo

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

    private fun getPokemonMoves(pok : Pokemon) : List<Move?> {
        val moves = mutableListOf<Move?>()
        val allPokemonMoves = pok.moves.shuffled()
        var movesCount = 0
        val moveCallback : Callback<Move> = Utils.pokeAPICallback { response ->
            moves.add(response.body()!!)
        }
        for (pokemonMove in allPokemonMoves){
            if (movesCount == 4)
                break
            if (allDamageMovesList.any { it.name == pokemonMove.move.name }) {
                pokeAPIService.getMove(pokemonMove.move.name).enqueue(moveCallback)
                movesCount++
            }
        }
        return moves
    }

    private fun opponentTurn(next : () -> Unit = { nextAction(); }) {
        playerTurn = false
        var moveId = 1
        var maxDamage = 0
        for (i in 0 until opponentBattlingPokemon.moves.count()) {
            val move = opponentBattlingPokemon.moves[i]!!
            val efficiency = getEfficiency(move.type, battlingPokemon.pokemon.types)
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
        opponentPokemonName.text = Utils.firstLetterUpperCase(pok.pokemon.name)
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
        battlingPokemonName.text = Utils.firstLetterUpperCase(pok.pokemon.name)
        setBattlingMove(1, if (pok.moves.isNotEmpty()) pok.moves[0] else null)
        setBattlingMove(2, if (pok.moves.size > 1) pok.moves[1] else null)
        setBattlingMove(3, if (pok.moves.size > 2) pok.moves[2] else null)
        setBattlingMove(4, if (pok.moves.size > 3) pok.moves[3] else null)
    }

    private fun playerTurn(moveId : Int) {
        if (opponentBattlingPokemon.pokemon.stats.find { it.stat.name == "speed" }!!.base_stat > battlingPokemon.pokemon.stats.find { it.stat.name == "speed" }!!.base_stat)
            opponentTurn {
                playerTurn = true
                attack(moveId, battlingPokemon, opponentBattlingPokemon, opponentPokemonHealth) { playerTurn = true }
            }
        else
            attack(moveId, battlingPokemon, opponentBattlingPokemon, opponentPokemonHealth) { opponentTurn { playerTurn = true } }
    }

    private fun greyImage(img : ImageView) {
        val matrix = ColorMatrix()
        matrix.setSaturation(0f) // 0 means grayscale
        val cf = ColorMatrixColorFilter(matrix)
        img.colorFilter = cf
        img.imageAlpha = 128 // 128 = 0.5
    }

    private fun handlePokemonKO(pok : PokemonInfo) {
        val pokemonName = Utils.firstLetterUpperCase(pok.pokemon.name)
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
                    MessageTextView.setOnClickListener { (activity as MainActivity).Home(); }
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
                    MessageTextView.setOnClickListener { (activity as MainActivity).Home(); }
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

    private fun getEfficiency(moveType : NamedAPIResource, pokemonTypes : List<PokemonType>) : Double {
        var efficiency = 1.0
        val moveTypesDamageRelations = allMovesTypesDamageRelations[moveType.name]!!
        for (pokemonType in pokemonTypes) {
            if (moveTypesDamageRelations.no_damage_to.any { it.name == pokemonType.type.name })
                efficiency *= 0.0
            if (moveTypesDamageRelations.double_damage_to.any { it.name == pokemonType.type.name })
                efficiency *= 2.0
            if (moveTypesDamageRelations.half_damage_to.any { it.name == pokemonType.type.name })
                efficiency *= 0.5
        }
        return efficiency
    }

    private fun dealDamage(move : Move, attacker : PokemonInfo, defender: PokemonInfo, health: ProgressBar) : Boolean {
        val moveName = Utils.firstLetterUpperCase(move.name)
        if (!DamageHelper.moveHit(move, attacker, defender)) {
            doOrAddAction { showMessage("$moveName missed!"); }
            return true
        }
        val efficiency = getEfficiency(move.type, defender.pokemon.types)
        when {
            efficiency == 0.0 -> doOrAddAction { showMessage("$moveName has no effect..."); }
            efficiency < 1 -> doOrAddAction { showMessage("$moveName is not very effective..."); }
            efficiency > 1 -> doOrAddAction { showMessage("$moveName is super effective!"); }
        }
        if (efficiency == 0.0)
            return true

        if (move.power == null)
            move.power = 0

        val critical = if (Globals.GENERATION.generation == 1) DamageHelper.criticalGen1(attacker.pokemon.stats.find { t -> t.stat.name == "speed" }!!) else DamageHelper.critical()
        if (critical)
            showMessage("A critical hit!")
        val damage = DamageHelper.computeDamage(attacker, defender, move, critical, efficiency)

        defender.hp -= max(damage.toInt(), 1)
        defender.hp = max(defender.hp, 0)
        health.progress = defender.hp
        return efficiency != 1.0
    }

    private fun attack(moveId : Int, attacker : PokemonInfo, defender : PokemonInfo, health : ProgressBar, next : () -> Unit = { nextAction(); }) {
        playerTurn = false
        val move = attacker.moves[moveId - 1]!!
        val attackerName = Utils.firstLetterUpperCase(attacker.pokemon.name)
        val moveName = Utils.firstLetterUpperCase(move.name)
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


    private fun setBattlingMove(id : Int, move : Move?) {
        val (txt, img) = when (id) {
            1 -> Pair(move1, move1Type)
            2 -> Pair(move2, move2Type)
            3 -> Pair(move3, move3Type)
            4 -> Pair(move4, move4Type)
            else -> Pair(null, null)
        }
        if (move != null) {
            txt!!.text = Utils.firstLetterUpperCase(move.name)
            when (id) {
                1 -> move1Description.text = move.flavor_text_entries[2].flavor_text
                2 -> move2Description.text = move.flavor_text_entries[2].flavor_text
                3 -> move3Description.text = move.flavor_text_entries[2].flavor_text
                4 -> move4Description.text = move.flavor_text_entries[2].flavor_text
            }
            Glide
                .with(this@BattleFragment)
                .load(Utils.typeToRDrawable(move.type.name))
                .into(img!!)
        }
    }

    private fun getTypesDamageRelations() {
        for (typeName in allTypes) {
            val typeCallback: Callback<Type> = Utils.pokeAPICallback { response ->
                val type: Type = response.body()!!
                allMovesTypesDamageRelations[typeName] = type.damage_relations
            }
            pokeAPIService.getTypeByName(typeName).enqueue(typeCallback)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val opponent1Callback : Callback<Pokemon> = Utils.pokeAPICallback { response ->
                val pok = response.body()!!
                val moves : List<Move?> = getPokemonMoves(pok)
                opponentPokemon1 = PokemonInfo(pok, moves, pok.stats.find { it.stat.name == "hp" }!!.base_stat, 0, 0, null)
                setOpponentPokemon(opponentPokemon1)
                opponentPokemonLevel.text = Globals.POKEMON_LEVEL.toString()
                showMessage("A wild " + opponentPokemonName.text + " has appeared!")
            }
            val opponent2Callback : Callback<Pokemon> = Utils.pokeAPICallback { response ->
                val pok = response.body()!!
                val moves : List<Move?> = getPokemonMoves(pok)
                opponentPokemon2 = PokemonInfo(pok, moves, pok.stats.find { it.stat.name == "hp" }!!.base_stat, 0, 0, null)
            }
            val opponent3Callback : Callback<Pokemon> = Utils.pokeAPICallback { response ->
                val pok = response.body()!!
                val moves : List<Move?> = getPokemonMoves(pok)
                opponentPokemon3 = PokemonInfo(pok, moves, pok.stats.find { it.stat.name == "hp" }!!.base_stat, 0, 0, null)
            }
            val pokemon1Callback : Callback<Pokemon> = Utils.pokeAPICallback { response ->
                val pok = response.body()!!
                val moves : List<Move?> = getPokemonMoves(pok)
                pokemon1 = PokemonInfo(pok, moves, pok.stats.find { it.stat.name == "hp" }!!.base_stat, 0, 0, null)
                setBattlingPokemon(pokemon1)
                battlingPokemonLevel.text = Globals.POKEMON_LEVEL.toString()
                Glide
                    .with(this@BattleFragment)
                    .load(pokemon1.pokemon.sprites.front_default)
                    .into(pokemon1ImageView)
                pokemon1Name.text = battlingPokemonName.text
                pokemon1ImageView.setOnClickListener {
                    if (battlingPokemon != pokemon1 && playerTurn) {
                        setBattlingPokemon(pokemon1)
                        opponentTurn { playerTurn = true }
                    }
                }
            }
            val pokemon2Callback : Callback<Pokemon> = Utils.pokeAPICallback { response ->
                val pok = response.body()!!
                val moves : List<Move?> = getPokemonMoves(pok)
                pokemon2 = PokemonInfo(pok, moves, pok.stats.find { it.stat.name == "hp" }!!.base_stat, 0, 0, null)
                Glide
                    .with(this@BattleFragment)
                    .load(pokemon2.pokemon.sprites.front_default)
                    .into(pokemon2ImageView)
                pokemon2Name.text = Utils.firstLetterUpperCase(pok.name)
                pokemon2ImageView.setOnClickListener {
                    if (battlingPokemon != pokemon2 && playerTurn) {
                        setBattlingPokemon(pokemon2)
                        opponentTurn { playerTurn = true }
                    }
                }
            }
            val pokemon3Callback : Callback<Pokemon> = Utils.pokeAPICallback { response ->
                val pok = response.body()!!
                val moves : List<Move?> = getPokemonMoves(pok)
                pokemon3 = PokemonInfo(pok, moves, pok.stats.find { it.stat.name == "hp" }!!.base_stat, 0, 0, null)
                Glide
                    .with(this@BattleFragment)
                    .load(pokemon3.pokemon.sprites.front_default)
                    .into(pokemon3ImageView)
                pokemon3Name.text = Utils.firstLetterUpperCase(pok.name)
                pokemon3ImageView.setOnClickListener {
                    if (battlingPokemon != pokemon3 && playerTurn) {
                        setBattlingPokemon(pokemon3)
                        opponentTurn { playerTurn = true }
                    }
                }
            }
            val movesCallback : Callback<MoveCategory> = Utils.pokeAPICallback { response ->
                val allDamageMovesGenerationsList : MoveCategory = response.body()!!
                allDamageMovesList.addAll(allDamageMovesGenerationsList.moves.filterIndexed { index, _ ->
                    index <= Globals.GENERATION.maxIdMove })
                pokeAPIService.getPokemon(it.getInt("opponentPokemon0")).enqueue(opponent1Callback)
                pokeAPIService.getPokemon(it.getInt("opponentPokemon1")).enqueue(opponent2Callback)
                pokeAPIService.getPokemon(it.getInt("opponentPokemon2")).enqueue(opponent3Callback)
                pokeAPIService.getPokemon(it.getInt("pokemon0")).enqueue(pokemon1Callback)
                pokeAPIService.getPokemon(it.getInt("pokemon1")).enqueue(pokemon2Callback)
                pokeAPIService.getPokemon(it.getInt("pokemon2")).enqueue(pokemon3Callback)
            }
            val typesCallback : Callback<NamedAPIResourceList> = Utils.pokeAPICallback { response ->
                val types = response.body()!!
                for (type in types.results){
                    allTypes.add(type.name)
                }
            }
            pokeAPIService.getAllDamageMoves().enqueue(movesCallback)
            pokeAPIService.getTypes().enqueue(typesCallback)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MessageTextView.setOnClickListener {
            setBattlingMove(1, if (this::battlingPokemon.isInitialized && battlingPokemon.moves.isNotEmpty()) battlingPokemon.moves[0] else null)
            setBattlingMove(2, if (this::battlingPokemon.isInitialized && battlingPokemon.moves.size > 1) battlingPokemon.moves[1] else null)
            setBattlingMove(3, if (this::battlingPokemon.isInitialized && battlingPokemon.moves.size > 2) battlingPokemon.moves[2] else null)
            setBattlingMove(4, if (this::battlingPokemon.isInitialized && battlingPokemon.moves.size > 3) battlingPokemon.moves[3] else null)
            nextAction()
            if (allMovesTypesDamageRelations.isEmpty())
                getTypesDamageRelations()
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_battle, container, false)
    }
}
