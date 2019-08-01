package com.journaldev.rxjavaretrofit


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

import com.journaldev.rxjavaretrofit.pojo.Crypto

import java.util.ArrayList

class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private val marketList: MutableList<Crypto.Market>

    init {
        marketList = ArrayList<Crypto.Market>()
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): RecyclerViewAdapter.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolder, position: Int) {

        val market = marketList[position]
        holder.txtCoin.text = market.coinName
        holder.txtMarket.text = market.market
        holder.txtPrice.text = "$" + String.format("%.2f", java.lang.Double.parseDouble(market.price))

    }

    override fun getItemCount(): Int {
        return marketList.size
    }

    fun setData(data: List<Crypto.Market>) {

        this.marketList.addAll(data)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var txtCoin: TextView
        var txtMarket: TextView
        var txtPrice: TextView
        var cardView: CardView

        init {

            txtCoin = view.findViewById(R.id.txtCoin)
            txtMarket = view.findViewById(R.id.txtMarket)
            txtPrice = view.findViewById(R.id.txtPrice)
            cardView = view.findViewById(R.id.cardView)
        }
    }
}
