package com.example.mygram

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mygram.models.Posts
import kotlinx.android.synthetic.main.item_post.view.*

class PostsAdapter(val context: Context,val post:List<Posts>):RecyclerView.Adapter<PostsAdapter.ViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_post,parent,false))
    }

    override fun getItemCount(): Int {
        return post.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(post[position])
    }
    inner class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        fun bind(posts: Posts) {
            itemView.tvUsername.text=posts.user?.username
            itemView.tvDescreption.text=posts.descreption
            Glide.with(context).load(posts.imageUrl).into(itemView.ivPost)
            itemView.tvRelativeTime.text=DateUtils.getRelativeTimeSpanString(posts.currentTime)
        }
    }
}