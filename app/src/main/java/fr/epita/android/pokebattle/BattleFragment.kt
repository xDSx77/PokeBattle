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
import fr.epita.android.pokebattle.webservices.Move
import fr.epita.android.pokebattle.webservices.MoveCategory
import fr.epita.android.pokebattle.webservices.PokeAPIInterface
import fr.epita.android.pokebattle.webservices.Pokemon
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
    );

    private val jsonConverter = GsonConverterFactory.create(GsonBuilder().create());
    private val retrofit = Retrofit.Builder()
        .baseUrl(PokeAPIInterface.Constants.url)
        .addConverterFactory(jsonConverter)
        .build();
    private val service =  retrofit.create(PokeAPIInterface::class.java);

    private lateinit var moveList : MoveCategory;
    private lateinit var opponentPokemon1 : PokemonInfo;
    private lateinit var opponentPokemon2 : PokemonInfo;
    private lateinit var opponentPokemon3 : PokemonInfo;
    private lateinit var pokemon1 : PokemonInfo;
    private lateinit var pokemon2 : PokemonInfo;
    private lateinit var pokemon3 : PokemonInfo;

    private lateinit var battlingPokemon : PokemonInfo;
    private lateinit var opponentBattlingPokemon : PokemonInfo;

    private val actions : Queue<() -> Unit> = LinkedList<() -> Unit>();

    private fun <T> pokeAPICallback(onResponse : (Response<T>) -> Unit) : Callback<T> {
        return object : Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) {
                Log.w("WebServices", "Poke API call failed" + t.message)
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                Log.w("WebServices", "Poke API call success");
                if (response.code() == 200)
                    onResponse(response);
            }
        }
    }

    private fun doOrAddAction(action : () -> Unit) {
        if (actions.isEmpty())
            action();
        else
            actions.add { action() };
    }

    private fun nextAction() {
        if (actions.isEmpty())
            showMoves();
        else
            actions.remove()();
    }

    private fun getPokemonMoves(pok : Pokemon) : List<Move?> {
        val moves = mutableListOf<Move?>();
        val allMoves = pok.moves.shuffled();
        var count = 0;
        val moveCallback : Callback<Move> = pokeAPICallback { response ->
            moves.add(response.body()!!);
        };
        allMoves.forEach loop@{ move ->
            if (moveList.moves.any { it.name == move.move.name }) {
                service.getMove(move.move.name).enqueue(moveCallback);
                count++;
                if (count == 4)
                    return@loop;
            }
        }
        return moves;
    }

    private fun opponentTurn(next : () -> Unit = { nextAction(); }) {
        attack(1, opponentBattlingPokemon, battlingPokemon, pokemonHealth, next);
    }

    private fun showMessage(m : String) {
        messageText.text = m;
        move1.visibility = View.INVISIBLE;
        move1Type.visibility = View.INVISIBLE;
        move2.visibility = View.INVISIBLE;
        move2Type.visibility = View.INVISIBLE;
        move3.visibility = View.INVISIBLE;
        move3Type.visibility = View.INVISIBLE;
        move4.visibility = View.INVISIBLE;
        move4Type.visibility = View.INVISIBLE;
        messageText.visibility = View.VISIBLE;
    }

    private fun showMoves() {
        messageText.visibility = View.INVISIBLE;
        move1.visibility = View.VISIBLE;
        move1Type.visibility = View.VISIBLE;
        move2.visibility = View.VISIBLE;
        move2Type.visibility = View.VISIBLE;
        move3.visibility = View.VISIBLE;
        move3Type.visibility = View.VISIBLE;
        move4.visibility = View.VISIBLE;
        move4Type.visibility = View.VISIBLE;
    }

    private fun setOpponentPokemon(pok : PokemonInfo) {
        opponentBattlingPokemon = pok;
        Glide
            .with(this@BattleFragment)
            .load(pok.pokemon.sprites.front_default)
            .into(opponentPokemonImage);
        opponentHealth.max = pok.pokemon.stats.find { it.stat.name == "hp" }!!.base_stat;
        opponentHealth.progress = pok.hp;
    }

    private fun setBattlingPokemon(pok : PokemonInfo) {
        battlingPokemon = pok;
        Glide
            .with(this@BattleFragment)
            .load(pok.pokemon.sprites.back_default)
            .into(battlingPokemonImage);
        pokemonHealth.max = pok.pokemon.stats.find { it.stat.name == "hp" }!!.base_stat;
        pokemonHealth.progress = pok.hp;
        setBattlingMove(1, pok.moves[0]!!);
        setBattlingMove(2, pok.moves[1]!!);
        setBattlingMove(3, pok.moves[2]!!);
        setBattlingMove(4, pok.moves[3]!!);
    }

    private fun playerTurn(moveId : Int) {
        if (opponentBattlingPokemon.pokemon.stats.find { it.stat.name == "speed" }!!.base_stat > battlingPokemon.pokemon.stats.find { it.stat.name == "speed" }!!.base_stat)
            opponentTurn { attack(moveId, battlingPokemon, opponentBattlingPokemon, opponentHealth) };
        else
            attack(moveId, battlingPokemon, opponentBattlingPokemon, opponentHealth) { opponentTurn() };
    }

    private fun greyImage(img : ImageView) {
        val matrix = ColorMatrix()
        matrix.setSaturation(0f) //0 means grayscale
        val cf = ColorMatrixColorFilter(matrix)
        img.colorFilter = cf
        img.imageAlpha = 128 // 128 = 0.5
    }

    private fun handlePokemonKO(pok : PokemonInfo) {
        showMessage(pok.pokemon.name + " is KO!");
        if (pok == battlingPokemon) {
            when (pok) {
                pokemon1 -> greyImage(pokemon1Image)
                pokemon2 -> greyImage(pokemon2Image)
                pokemon3 -> greyImage(pokemon3Image)
            }

            val pokemon = listOf<PokemonInfo>(pokemon1, pokemon2, pokemon3);
            val newPokemon = pokemon.find { it.hp > 0 };
            if (newPokemon == null) {
                actions.add {
                    showMessage("You lost!");
                    messageText.setOnClickListener(null);
                    greyImage(battlingPokemonImage);
                }
            }
            else
                setBattlingPokemon(newPokemon);
        }
        else {
            val opponents = listOf<PokemonInfo>(opponentPokemon1, opponentPokemon2, opponentPokemon3);
            val newOpponent = opponents.find { it.hp > 0 };
            if (newOpponent == null) {
                actions.add {
                    showMessage("You won!");
                    messageText.setOnClickListener(null);
                    greyImage(opponentPokemonImage);
                }
            }
            else
                setOpponentPokemon(newOpponent);
        }
    }

    private fun attack(moveId : Int, attacker : PokemonInfo, defender : PokemonInfo, health : ProgressBar, next : () -> Unit = { nextAction(); }) {
        val move = attacker.moves[moveId - 1]!!;
        doOrAddAction { showMessage(attacker.pokemon.name + " used " + move.name); }
        actions.add {
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
                move.power = 0;
            defender.hp -= maxOf(att / 10 + move.power!! - def, 1);
            defender.hp = maxOf(defender.hp, 0);
            health.progress = defender.hp;
            if (defender.hp > 0)
                next();
            else
                handlePokemonKO(defender);
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
        txt!!.text = move.name;
        Glide
            .with(this@BattleFragment)
            .load(move.typeToRDrawable())
            .into(img!!);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val opponent1Callback : Callback<Pokemon> = pokeAPICallback { response ->
                val pok = response.body()!!;
                opponentPokemon1 = PokemonInfo(pok, getPokemonMoves(pok), pok.stats.find { it.stat.name == "hp" }!!.base_stat);
                setOpponentPokemon(opponentPokemon1);
                showMessage("A wild " + pok.name + " has appeared!");
            }
            val opponent2Callback : Callback<Pokemon> = pokeAPICallback { response ->
                val pok = response.body()!!;
                opponentPokemon2 = PokemonInfo(pok, getPokemonMoves(pok), pok.stats.find { it.stat.name == "hp" }!!.base_stat);
            }
            val opponent3Callback : Callback<Pokemon> = pokeAPICallback { response ->
                val pok = response.body()!!;
                opponentPokemon3 = PokemonInfo(pok, getPokemonMoves(pok), pok.stats.find { it.stat.name == "hp" }!!.base_stat);
            }
            val pokemon1Callback : Callback<Pokemon> = pokeAPICallback { response ->
                val pok = response.body()!!;
                pokemon1 = PokemonInfo(pok, getPokemonMoves(pok), pok.stats.find { it.stat.name == "hp" }!!.base_stat);
                battlingPokemon = pokemon1;
                Glide
                    .with(this@BattleFragment)
                    .load(pokemon1.pokemon.sprites.back_default)
                    .into(battlingPokemonImage);
                Glide
                    .with(this@BattleFragment)
                    .load(pokemon1.pokemon.sprites.front_default)
                    .into(pokemon1Image);
                pokemonHealth.max = pokemon1.pokemon.stats.find { it.stat.name == "hp" }!!.base_stat;
                pokemonHealth.progress = pokemon1.hp;
                pokemon1Image.setOnClickListener {
                    if (battlingPokemon != pokemon1)
                        setBattlingPokemon(pokemon1);
                }
            }
            val pokemon2Callback : Callback<Pokemon> = pokeAPICallback { response ->
                val pok = response.body()!!;
                pokemon2 = PokemonInfo(pok, getPokemonMoves(pok), pok.stats.find { it.stat.name == "hp" }!!.base_stat);
                Glide
                    .with(this@BattleFragment)
                    .load(pokemon2.pokemon.sprites.front_default)
                    .into(pokemon2Image);
                pokemon2Image.setOnClickListener {
                    if (battlingPokemon != pokemon2)
                        setBattlingPokemon(pokemon2);
                }
            }
            val pokemon3Callback : Callback<Pokemon> = pokeAPICallback { response ->
                val pok = response.body()!!;
                pokemon3 = PokemonInfo(pok, getPokemonMoves(pok), pok.stats.find { it.stat.name == "hp" }!!.base_stat);
                Glide
                    .with(this@BattleFragment)
                    .load(pokemon3.pokemon.sprites.front_default)
                    .into(pokemon3Image);
                pokemon3Image.setOnClickListener {
                    if (battlingPokemon != pokemon3)
                        setBattlingPokemon(pokemon3);
                }
            }
            val movesCallback : Callback<MoveCategory> = pokeAPICallback {response ->
                moveList = response.body()!!;
                service.getPokemon(it.getInt("opponentPokemon0")).enqueue(opponent1Callback);
                service.getPokemon(it.getInt("opponentPokemon1")).enqueue(opponent2Callback);
                service.getPokemon(it.getInt("opponentPokemon2")).enqueue(opponent3Callback);
                service.getPokemon(it.getInt("pokemon0")).enqueue(pokemon1Callback);
                service.getPokemon(it.getInt("pokemon1")).enqueue(pokemon2Callback);
                service.getPokemon(it.getInt("pokemon2")).enqueue(pokemon3Callback);
            }
            service.getMoves().enqueue(movesCallback);
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        messageText.setOnClickListener {
            setBattlingMove(1, battlingPokemon.moves[0]!!);
            setBattlingMove(2, battlingPokemon.moves[1]!!);
            setBattlingMove(3, battlingPokemon.moves[2]!!);
            setBattlingMove(4, battlingPokemon.moves[3]!!);
            nextAction();
        }
        move1.setOnClickListener { playerTurn(1); }
        move2.setOnClickListener { playerTurn(2); }
        move3.setOnClickListener { playerTurn(3); }
        move4.setOnClickListener { playerTurn(4); }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_battle, container, false)
    }
}
