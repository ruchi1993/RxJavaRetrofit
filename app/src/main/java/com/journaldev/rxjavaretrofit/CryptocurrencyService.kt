package com.journaldev.rxjavaretrofit

import com.journaldev.rxjavaretrofit.pojo.Crypto

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface CryptocurrencyService {

    @GET("{coin}-usd")
    fun getCoinData(@Path("coin") coin: String): Observable<Crypto>

    companion object {


        val BASE_URL = "https://api.cryptonator.com/api/full/"
    }
}
