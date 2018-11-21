package com.example.andre.movieapp

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import java.io.FileNotFoundException

class MyDisplay: Activity(){
    lateinit var toptxt1: TextView
    lateinit var toptxt2: TextView
    lateinit var img: ImageView
    lateinit var sinopse: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)

        toptxt1 = findViewById(R.id.mfTopTxt)
        toptxt2 = findViewById(R.id.mfTopTxt2)
        img = findViewById(R.id.mfImgtxt)
        sinopse = findViewById(R.id.mfBotTxt)

        val bundle = intent.getBundleExtra("bund")
        if(bundle != null){
            toptxt1.text = bundle.getCharSequence("Name")
            toptxt2.text = bundle.getCharSequence("Age")
            //img.setImageBitmap(bundle.getParcelable("Poster"))
            try {
                val fol = openFileInput(bundle.getCharSequence("Name").toString())
                val bm = BitmapFactory.decodeStream(fol)
                img.setImageBitmap(bm)
                fol.close()
            }catch(e: FileNotFoundException){
                Log.d("TAG", "FileNotFoundException")
                img.setImageBitmap(BitmapFactory.decodeResource(resources,R.mipmap.image_not_found))

            }
            //val mybmb = fol.read()

            sinopse.text = bundle.getCharSequence("plot")
        }
    }
}