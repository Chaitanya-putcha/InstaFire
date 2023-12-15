package com.example.instafire

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instafire.models.Post
import com.google.android.gms.common.util.DataUtils
import java.math.BigInteger
import java.security.MessageDigest

class PostsAdapter(val context:Context, val posts:List<Post>):
    RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_post,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.Bind(posts[position])
    }

    inner class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        fun Bind(post: Post) {
               itemView.findViewById<TextView>(R.id.tvUsername).text = post.user?.name
            itemView.findViewById<TextView>(R.id.tvDescription).text = post.description
             Glide.with(context).load(post.image_url).into(itemView.findViewById(R.id.ivImage))

            Glide.with(context).load(post.user?.name?.let { getProfileImageUrl(it) }).into(itemView.findViewById(R.id.ivProfileImage))
            itemView.findViewById<TextView>(R.id.tvRelativeTime).text = DateUtils.getRelativeTimeSpanString(post.creation_time)
        }
        private fun getProfileImageUrl(username:String):String{
            val digest = MessageDigest.getInstance("MD5")
            val hash = digest.digest(username.toByteArray())
            val bigInt = BigInteger(hash)
            val hex = bigInt.abs().toString(16)
            return return "https://www.gravatar.com/avatar/$hex?d=identicon";
        }
    }
}