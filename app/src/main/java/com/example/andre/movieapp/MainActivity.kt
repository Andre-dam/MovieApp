package com.example.andre.movieapp

import android.content.Context
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.io.*
import java.nio.charset.StandardCharsets
import org.json.JSONException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.app.FragmentManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.inputmethod.InputMethodManager


private val TAG = "MyActivity"

class MainActivity : AppCompatActivity() {

    lateinit var listViews: RecyclerView
    lateinit var editText: EditText
    lateinit var adapter : MyAdapter
    lateinit var spiner : ProgressBar
    var vetor_ = ArrayList<String>()
    var BMvetor_ = ArrayList<Bitmap>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listViews = findViewById<RecyclerView>(R.id.recyclerviewid)
        editText = findViewById<EditText>(R.id.editText)


        adapter = MyAdapter(this,vetor_,BMvetor_)
        spiner = findViewById<ProgressBar>(R.id.progressBar)
        spiner.visibility= View.INVISIBLE


        listViews.adapter = adapter
        listViews.layoutManager = LinearLayoutManager(this)

        editText.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    downloadContent().execute()
                    val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)


                    /*val transaction = supportFragmentManager.beginTransaction()
                    transaction.add(R.id.frameid,MainFragment())
                    transaction.commit()*/

                    true
                }
                else -> false
            }
        }

    }

    override fun onStart() {
        super.onStart()
    }

    inner class downloadContent:AsyncTask<String,Boolean,Boolean >(){

        fun getBitmapFromURL(src: String): Bitmap {
            try {
                val url = java.net.URL(src)
                val connection = url
                        .openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                return BitmapFactory.decodeStream(input)
            } catch (e: IOException) {
                e.printStackTrace()
                return BitmapFactory.decodeResource(resources,R.mipmap.apollo)
            }

        }

        override fun doInBackground(vararg params: String?): Boolean {

            Log.i(TAG,"Entrou em doInback")
            publishProgress(true)
            var url = URL("http://www.omdbapi.com/?apikey=24f3e826&s="+editText.text.toString()+"&type=movie")
            var urlConnection = url.openConnection() as HttpURLConnection
            try {
                var resp = BufferedInputStream(urlConnection.inputStream)
                var lines = BufferedReader(InputStreamReader(resp,StandardCharsets.UTF_8)).readText()
                Log.i(TAG,lines)
                var returns = JSONObject(lines).getJSONArray("Search")

                urlConnection.disconnect()
                vetor_.clear()
                BMvetor_.clear()
                for (i in 0..(returns.length()-1)){
                    val imgurl = returns.getJSONObject(i).getString("Poster")
                    BMvetor_.add(getBitmapFromURL(imgurl))
                    url = URL("http://www.omdbapi.com/?apikey=24f3e826&i="+returns.getJSONObject(i).getString("imdbID"))
                    var urlConnection = url.openConnection() as HttpURLConnection
                    var resp = BufferedInputStream(urlConnection.inputStream)
                    var lines = BufferedReader(InputStreamReader(resp,StandardCharsets.UTF_8)).readText()
                    var resumeJSON =JSONObject(lines)

                    val rating_array = resumeJSON.getJSONArray("Ratings")
                    for (j in 0..(rating_array.length()-1)){
                        val sorc = rating_array.getJSONObject(j)
                        if(sorc.getString("Source").equals("Rotten Tomatoes")){
                            vetor_.add(resumeJSON.getString("Title")+ '\n' +"Rating:"+ sorc.getString("Value"))
                        }
                    }
                    urlConnection.disconnect()
                }
                for (i in vetor_.iterator()){
                    Log.i(TAG,i)
                }
            }catch (e: JSONException){
                return false
            }catch (e: IOException){
                return false
            }
            return true
        }

        override fun onProgressUpdate(vararg values: Boolean?) {
            super.onProgressUpdate(*values)
            if(values[0]==true) {
                spiner.visibility = View.VISIBLE
            }else{
                spiner.visibility = View.INVISIBLE
            }
        }
        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            publishProgress(false)
            if(result) {
                //Log.i(TAG, "Entrou em onPost")
                adapter.notifyDataSetChanged()
            }else{
                val toast = Toast.makeText(this@MainActivity,"Deu Ruim",Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }
}
