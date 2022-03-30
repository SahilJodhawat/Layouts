package com.example.layouts.adater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.layouts.R
import com.example.layouts.data.NewsData
import com.squareup.picasso.Picasso


/**
 * Created by mohammad sajjad on 25-03-2022.
 * EMAIL mohammadsajjad679@gmail.com
 */

class NewsAdapter :
    RecyclerView.Adapter<NewsAdapter.viewholder>() {

private var newsData = ArrayList<NewsData>()

    fun setData(newsdata : ArrayList<NewsData>){
        this.newsData= newsdata
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val view:View=LayoutInflater.from(parent.context).inflate(R.layout.news,parent,false)
        return viewholder(view)
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {
     holder.newstitle.text= newsData[holder.adapterPosition].title
        Picasso.get().load(newsData[holder.adapterPosition].imageUrl).resize(240,
        240).into(holder.newsImage)
        holder.newsauthor.text="Author: ${newsData[holder.adapterPosition].author}"
    }

    override fun getItemCount(): Int {
      return newsData.size
    }
    class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newstitle: TextView = itemView.findViewById(R.id.news_title)
        val newsauthor: TextView = itemView.findViewById(R.id.news_author)
        val newsImage: ImageView = itemView.findViewById(R.id.news_image)

    }
}