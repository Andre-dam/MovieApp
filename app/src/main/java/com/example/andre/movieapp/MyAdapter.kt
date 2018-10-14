package com.example.andre.movieapp

import android.content.Context
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.example.andre.movieapp.R.id.parent
import kotlinx.android.synthetic.main.layout_listimtem.view.*
import java.net.URI

class MyAdapter(val mContext: Context, val mText: ArrayList<String>, val mImage: ArrayList<Bitmap>): RecyclerView.Adapter<MyAdapter.MyViewHolder>() {


    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {
        p0.texto_rf.text = mText[p1]
        p0.image_rf.setImageBitmap(mImage[p1])
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val texto_rf = itemView.findViewById<TextView>(R.id.texto)
        val image_rf = itemView.findViewById<ImageView>(R.id.imagem)
    }
    override fun onCreateViewHolder(p0: ViewGroup, viewtype: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_listimtem, p0, false))
    }
    override fun getItemCount(): Int {
        return mText.size
    }
}