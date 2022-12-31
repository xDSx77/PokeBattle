package fr.epita.android.pokebattle.webservices.pokeapi.services

import android.util.Log
import fr.epita.android.pokebattle.Constants.POKEBATTLE_DATABASE
import fr.epita.android.pokebattle.Constants.POKEMON_SPECIES
import fr.epita.android.pokebattle.Constants.WEB_SERVICES
import fr.epita.android.pokebattle.Globals
import fr.epita.android.pokebattle.ObserverHelper
import fr.epita.android.pokebattle.webservices.pokeapi.PokeAPIHelper
import fr.epita.android.pokebattle.webservices.pokeapi.models.pokemon.Pokemon
import fr.epita.android.pokebattle.webservices.pokeapi.models.pokemon.PokemonSpecies
import fr.epita.android.pokebattle.webservices.pokeapi.models.resourcelist.NamedAPIResourceList
import fr.epita.android.pokebattle.webservices.pokeapi.models.utils.NamedAPIResource
import fr.epita.android.pokebattle.webservices.pokeapi.repository.pokemon.PokemonRepository
import fr.epita.android.pokebattle.webservices.pokeapi.repository.pokemon.PokemonSpeciesRepository
import fr.epita.android.pokebattle.webservices.pokeapi.repository.resourcelist.NamedAPIResourceListRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class PokeAPIService @Inject constructor(
    private val namedAPIResourceListRepository : NamedAPIResourceListRepository,
    private val pokemonSpeciesRepository : PokemonSpeciesRepository,
    private val pokemonRepository : PokemonRepository
) {

    fun buildAllPokemons(actionWithPokemons : (List<Pokemon>) -> Unit) {
        pokemonRepository.getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                ObserverHelper.pokeBattleDbSingleObserver(
                    noPokemonFoundInDb(actionWithPokemons),
                    pokemonFoundInDb(actionWithPokemons)
                )
            )
    }

    private fun noPokemonFoundInDb(actionWithPokemons : (List<Pokemon>) -> Unit) =
        fun(throwable : Throwable) {
            Log.i(POKEBATTLE_DATABASE, "$throwable")
            pokemonSpeciesRepository.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    ObserverHelper.pokeBattleDbSingleObserver(
                        fun(throwable : Throwable) {
                            Log.i(POKEBATTLE_DATABASE, "$throwable")
                            getPokemonSpeciesResourceNameAndInsertIntoDb(actionWithPokemons)
                        },
                        getPokemonOrGetPokemonSpeciesIfMissing(actionWithPokemons)
                    )
                )
        }

    private fun pokemonFoundInDb(actionWithPokemons : (List<Pokemon>) -> Unit) =
        fun(pokemons : List<Pokemon>) {
            if (pokemons.size == Globals.GENERATION.maxIdPokedex) {
                val pokemonsSortedById = pokemons.sortedBy { it.id }
                actionWithPokemons(pokemonsSortedById)
            } else {
                // On a des pokémons en BDD mais on n'en a pas le bon nombre, il faut refaire toute la chaîne
                // getAllPokemonSpecies -> on save en BDD -> pour chaque espèce -> getPokemonSpecie -> on save en BDD -> pour chaque espèce -> getPokemon -> on save en BDD
                Log.i(POKEBATTLE_DATABASE, "Not the right number of Pokemon in the DB, recall getPokemonSpecies")
                getPokemonSpeciesResourceNameAndInsertIntoDb(actionWithPokemons)
            }
        }

    private fun getPokemonOrGetPokemonSpeciesIfMissing(actionWithPokemons : (List<Pokemon>) -> Unit) =
        fun(pokemonSpecies : List<PokemonSpecies>) {
            if (pokemonSpecies.size == Globals.GENERATION.maxIdPokedex) {
                val pokemonList : MutableList<Pokemon> = mutableListOf()
                for (pokemonSpecie in pokemonSpecies) {
                    getPokemonAndInsertIntoDb(pokemonSpecie, pokemonList, actionWithPokemons)
                }
            } else {
                getPokemonSpeciesResourceNameAndInsertIntoDb(actionWithPokemons)
            }
        }

    private fun getPokemonSpeciesResourceNameAndInsertIntoDb(actionWithPokemons : (List<Pokemon>) -> Unit) {
        namedAPIResourceListRepository.findByType(POKEMON_SPECIES)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                ObserverHelper.pokeBattleDbSingleObserver(
                    pokemonSpeciesNotFoundInDb(actionWithPokemons),
                    pokemonSpeciesAllInDb(actionWithPokemons)
                )
            )
    }

    private fun getPokemonAndInsertIntoDb(pokemonSpecie : PokemonSpecies, pokemonList : MutableList<Pokemon>, actionWithPokemons : (List<Pokemon>) -> Unit) {
        val pokemonDefaultVarietyName = pokemonSpecie.varieties.find { it.is_default }!!.pokemon.name
        PokeAPIHelper.pokeAPIInterface.getPokemonByName(pokemonDefaultVarietyName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                ObserverHelper.pokeApiObserver { pokemon ->
                    pokemonRepository.insert(pokemon)
                        .doOnError { error ->
                            Log.e(POKEBATTLE_DATABASE, "Error while saving Pokemon into the db: $error")
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            Log.d(POKEBATTLE_DATABASE, "Pokemon correctly inserted into the db")
                            pokemonList.add(pokemon)
                            if (pokemonList.size == Globals.GENERATION.maxIdPokedex) {
                                val pokemonsSortedById = pokemonList.sortedBy { it.id }
                                actionWithPokemons(pokemonsSortedById)
                            }
                        }
                }
            )
    }

    private fun pokemonSpeciesNotFoundInDb(actionWithPokemons : (List<Pokemon>) -> Unit) =
        fun(throwable : Throwable) {
            Log.i(POKEBATTLE_DATABASE, "$throwable")
            PokeAPIHelper.pokeAPIInterface.getAllPokemonSpecies(Globals.GENERATION.maxIdPokedex)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    ObserverHelper.pokeApiObserver(
                        fun() {
                            Log.i(WEB_SERVICES, "Subscribe complete")
                        },
                        onPokemonSpecieInsertIntoDb(actionWithPokemons)
                    )
                )
        }

    private fun onPokemonSpecieInsertIntoDb(actionWithPokemons : (List<Pokemon>) -> Unit) =
        fun(pokemonSpecies : NamedAPIResourceList) {
            pokemonSpecies.type = POKEMON_SPECIES
            namedAPIResourceListRepository.insert(pokemonSpecies)
                .doOnError { error ->
                    Log.e(POKEBATTLE_DATABASE, "Error while saving PokemonSpecies List into the db: $error")
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d(POKEBATTLE_DATABASE, "PokemonSpecies List correctly inserted into the db")
                    val pokemons : MutableList<Pokemon> = mutableListOf()
                    for (pokemonSpecieResource in pokemonSpecies.results) {
                        pokemonSpeciesRepository.findByName(pokemonSpecieResource.name)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                ObserverHelper.pokeBattleDbSingleObserver(
                                    pokemonSpecieNotFoundInDb(pokemonSpecieResource, pokemons, actionWithPokemons),
                                    pokemonSpecieFoundInDb(pokemons, actionWithPokemons)
                                )
                            )
                    }
                }
        }

    private fun pokemonSpecieNotFoundInDb(pokemonSpecieResource : NamedAPIResource, pokemons : MutableList<Pokemon>, actionWithPokemons : (List<Pokemon>) -> Unit) =
        fun(throwable : Throwable) {
            Log.i(POKEBATTLE_DATABASE, "$throwable")
            PokeAPIHelper.pokeAPIInterface.getPokemonSpecieByName(pokemonSpecieResource.name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    ObserverHelper.pokeApiObserver { pokemonSpecie ->
                        pokemonSpeciesRepository.insert(pokemonSpecie)
                            .doOnError { error ->
                                Log.e(POKEBATTLE_DATABASE, "Error while saving PokemonSpecie into the db: $error")
                            }
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                Log.d(POKEBATTLE_DATABASE, "PokemonSpecie correctly inserted into the db")
                                getPokemonInDbOrDoActions(pokemonSpecie, pokemons, actionWithPokemons)
                            }
                    }
                )
        }

    private fun pokemonSpecieFoundInDb(pokemons : MutableList<Pokemon>, actionWithPokemons : (List<Pokemon>) -> Unit) =
        fun(pokemonSpecie : PokemonSpecies) {
            getPokemonInDbOrDoActions(pokemonSpecie, pokemons, actionWithPokemons)
        }

    private fun getPokemonInDbOrDoActions(pokemonSpecie : PokemonSpecies, pokemons : MutableList<Pokemon>, actionWithPokemons : (List<Pokemon>) -> Unit) {
        val pokemonSpecieDefaultVarietyName = pokemonSpecie.varieties.find { it.is_default }!!.pokemon.name
        pokemonRepository.findByName(pokemonSpecieDefaultVarietyName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                ObserverHelper.pokeBattleDbSingleObserver(
                    fun(throwable : Throwable) {
                        Log.i(POKEBATTLE_DATABASE, "$throwable")
                        getPokemonAndInsertIntoDb(pokemonSpecie, pokemons, actionWithPokemons)
                    },
                    fun(pokemonInDb : Pokemon) {
                        pokemons.add(pokemonInDb)
                        if (pokemons.size == Globals.GENERATION.maxIdPokedex) {
                            val pokemonsSortedById = pokemons.sortedBy { it.id }
                            actionWithPokemons(pokemonsSortedById)
                        }
                    }
                )
            )
    }

    private fun getPokemonSpecieInDBOrCallApi(pokemonSpecieName : String) : Observable<PokemonSpecies> {
        lateinit var pokemonSpeciesObservable : Observable<PokemonSpecies>
        pokemonSpeciesRepository.findByName(pokemonSpecieName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                ObserverHelper.pokeBattleDbSingleObserver(
                    fun(throwable : Throwable) {
                        Log.i(POKEBATTLE_DATABASE, "$throwable")
                        pokemonSpeciesObservable = PokeAPIHelper.pokeAPIInterface.getPokemonSpecieByName(pokemonSpecieName)
                    },
                    fun(pokemonSpecieInDb : PokemonSpecies) {
                        pokemonSpeciesObservable = Observable.just(pokemonSpecieInDb)
                    }
                )
            )
        return pokemonSpeciesObservable
    }

    private fun getPokemonInDBOrCallApi(pokemonSpecie : PokemonSpecies) : Observable<Pokemon> {
        lateinit var pokemonObservable : Observable<Pokemon>
        val pokemonSpecieDefaultVarietyName = pokemonSpecie.varieties.find { it.is_default }!!.pokemon.name
        pokemonRepository.findByName(pokemonSpecieDefaultVarietyName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                ObserverHelper.pokeBattleDbSingleObserver(
                    fun(throwable : Throwable) {
                        Log.i(POKEBATTLE_DATABASE, "$throwable")
                        pokemonObservable = PokeAPIHelper.pokeAPIInterface.getPokemonByName(pokemonSpecieDefaultVarietyName)

                    },
                    fun(pokemonInDb : Pokemon) {
                        pokemonObservable = Observable.just(pokemonInDb)
                    }
                )
            )
        return pokemonObservable
    }

    private fun pokemonSpeciesAllInDb(actionWithPokemons : (List<Pokemon>) -> Unit) =
        fun(pokemonsSpecies : NamedAPIResourceList) {
            val pokemonsSpeciesNames : List<String> = pokemonsSpecies.results.map { it.name }
            Observable.fromIterable(pokemonsSpeciesNames)
                .flatMap { pokemonSpecieName -> getPokemonSpecieInDBOrCallApi(pokemonSpecieName) }
                .flatMap { pokemonSpecie -> getPokemonInDBOrCallApi(pokemonSpecie) }
                .doOnError { error ->
                    Log.e(WEB_SERVICES, "Error with PokeAPI call: $error")
                }
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { pokemons ->
                    val pokemonsSortedById = pokemons.sortedBy { it.id }
                    actionWithPokemons(pokemonsSortedById)
                }
        }
}