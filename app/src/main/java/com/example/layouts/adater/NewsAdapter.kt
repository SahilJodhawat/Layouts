package com.example.layouts.adater

import android.content.Intent
import android.content.Intent.*
import android.net.ConnectivityManager
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.layouts.R
import com.example.layouts.data.NewsData
import com.example.layouts.databinding.NewsBinding
import com.squareup.picasso.Picasso


/**
 * Created by mohammad sajjad on 25-03-2022.
 * EMAIL mohammadsajjad679@gmail.com
 */

class NewsAdapter :
    RecyclerView.Adapter<NewsAdapter.viewholder>() {

    private var newsData = ArrayList<NewsData>()

    fun setData(newsdata: ArrayList<NewsData>) {
        this.newsData = newsdata
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val newsbinding = NewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewholder(newsbinding)
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {
        holder.newsbinding.newsTitle.text = newsData.get(holder.adapterPosition).title
        holder.newsbinding.newsAuthor.text = newsData.get(holder.adapterPosition).author
        Picasso.get().load(newsData.get(holder.adapterPosition).imageUrl).resize(
            240,
            240
        ).into(holder.newsbinding.newsImage)

    }

    override fun getItemCount(): Int {
        return newsData.size
    }

    class viewholder(val newsbinding: NewsBinding) : RecyclerView.ViewHolder(newsbinding.root)

}