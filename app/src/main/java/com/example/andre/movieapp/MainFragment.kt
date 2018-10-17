package com.example.andre.movieapp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class MainFragment:Fragment(){
    lateinit var toptxt1: TextView
    lateinit var toptxt2: TextView
    lateinit var img: ImageView
    lateinit var sinopse: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.main_fragment,container,false)

        toptxt1 = v.findViewById(R.id.mfTopTxt)
        toptxt2 = v.findViewById(R.id.mfTopTxt2)
        img = v.findViewById(R.id.mfImgtxt)
        sinopse = v.findViewById(R.id.mfBotTxt)

        val bundle = this.arguments
        if(bundle != null){
            toptxt1.text = bundle.getCharSequence("Name")
            toptxt2.text = bundle.getCharSequence("Age")
            img.setImageBitmap(bundle.getParcelable("Poster"))
            sinopse.text = bundle.getCharSequence("plot")
        }
        return v


    }


}