package com.example.andre.movieapp

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import android.support.v4.app.FragmentManager
import com.example.andre.movieapp.R.id.parent
import kotlinx.android.synthetic.main.layout_listimtem.view.*
import java.net.URI

class MyAdapter(val mContext: Context, val mMovie: ArrayList<Movie>): RecyclerView.Adapter<MyAdapter.MyViewHolder>() {


    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {
        //p0.texto_rf.text = mText[p1]
        //p0.image_rf.setImageBitmap(mImage[p1])
        val auxstr = mMovie[p1].Name + '\n' + "R:" + mMovie[p1].Rating
        p0.texto_rf.text = auxstr
        p0.image_rf.setImageBitmap(mMovie[p1].Poster)

        p0.itemView.setOnClickListener{
            val myfrag = MainFragment()
            val mybundle = Bundle()

            mybundle.putString("Name",mMovie[p1].Name)
            mybundle.putString("Age",mMovie[p1].Age)
            mybundle.putString("plot",mMovie[p1].plot)
            mybundle.putString("Rating",mMovie[p1].Rating)
            mybundle.putParcelable("Poster",mMovie[p1].Poster)

            myfrag.arguments = mybundle

            val transaction = (mContext as MainActivity).supportFragmentManager.beginTransaction()
            transaction.add(R.id.frameid,myfrag)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val texto_rf = itemView.findViewById<TextView>(R.id.texto)
        val image_rf = itemView.findViewById<ImageView>(R.id.imagem)
    }
    override fun onCreateViewHolder(p0: ViewGroup, viewtype: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_listimtem, p0, false))
    }
    override fun getItemCount(): Int {
        return mMovie.size
    }


}