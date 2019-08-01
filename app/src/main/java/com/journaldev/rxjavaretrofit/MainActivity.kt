package com.journaldev.rxjavaretrofit


import android.os.Bundle

import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.journaldev.rxjavaretrofit.pojo.Crypto

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

import com.journaldev.rxjavaretrofit.CryptocurrencyService.Companion.BASE_URL
import io.reactivex.functions.Consumer

class MainActivity : AppCompatActivity() {

    internal lateinit var recyclerView: RecyclerView
    internal lateinit var retrofit: Retrofit
    internal lateinit var recyclerViewAdapter: RecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerViewAdapter = RecyclerViewAdapter()
        recyclerView.adapter = recyclerViewAdapter


        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val gson = GsonBuilder()
                .setLenient()
                .create()

        retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()


        callEndpoints()
    }

    private fun callEndpoints() {

        val cryptocurrencyService = retrofit.create<CryptocurrencyService>(CryptocurrencyService::class.java!!)

        //Single call
        /*Observable<Crypto> cryptoObservable = cryptocurrencyService.getCoinData("btc");
        cryptoObservable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).map(result -> result.ticker).subscribe(this::handleResults, this::handleError);*/

        val btcObservable = cryptocurrencyService.getCoinData("btc")
                .map<Observable<Crypto.Market>> { result -> Observable.fromIterable<Crypto.Market>(result.ticker?.markets) }
                .flatMap<Crypto.Market> { x -> x }.filter { y ->
                    y.coinName = "btc"
                    true
                }.toList().toObservable()

        val ethObservable = cryptocurrencyService.getCoinData("eth")
                .map<Observable<Crypto.Market>> { result -> Observable.fromIterable<Crypto.Market>(result.ticker?.markets) }
                .flatMap<Crypto.Market> { x -> x }.filter { y ->
                    y.coinName = "eth"
                    true
                }.toList().toObservable()

        Observable.merge<List<Crypto.Market>>(btcObservable, ethObservable)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer<List<Crypto.Market>> { this.handleResults(it) }, Consumer<Throwable> { this.handleError(it) })

    }


    private fun handleResults(marketList: List<Crypto.Market>?) {
        if (marketList != null && marketList.size != 0) {
            recyclerViewAdapter.setData(marketList)


        } else {
            Toast.makeText(this, "NO RESULTS FOUND", Toast.LENGTH_LONG).show()
        }
    }

    private fun handleError(t: Throwable) {

        Toast.makeText(this, "ERROR IN FETCHING API RESPONSE. Try again", Toast.LENGTH_LONG).show()
    }

}
