package fr.epita.android.pokebattle.webservices.pokeapi

import androidx.fragment.app.Fragment
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

open class PokeAPIServiceFragment : Fragment() {

    private val pokeAPIRetrofit : Retrofit = Retrofit.Builder()
        .baseUrl(PokeAPIInterface.Constants.url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    protected val pokeAPIService : PokeAPIInterface = pokeAPIRetrofit.create(PokeAPIInterface::class.java)
}