package fr.epita.android.pokebattle.webservices.surleweb.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SurLeWebServiceHelper {

    private val surLeWebRetrofit : Retrofit = Retrofit.Builder()
        .baseUrl(SurLeWebAPIInterface.Constants.url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val surLeWebService : SurLeWebAPIInterface = surLeWebRetrofit.create(SurLeWebAPIInterface::class.java)

}