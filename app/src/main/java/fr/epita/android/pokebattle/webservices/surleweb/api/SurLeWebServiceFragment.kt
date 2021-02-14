package fr.epita.android.pokebattle.webservices.surleweb.api

import androidx.fragment.app.Fragment
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

open class SurLeWebServiceFragment : Fragment() {

    private val surLeWebRetrofit : Retrofit = Retrofit.Builder()
        .baseUrl(SurLeWebAPIInterface.Constants.url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    protected val surLeWebService : SurLeWebAPIInterface = surLeWebRetrofit.create(SurLeWebAPIInterface::class.java)
}