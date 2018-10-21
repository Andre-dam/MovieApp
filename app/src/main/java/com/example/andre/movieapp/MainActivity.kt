package com.example.andre.movieapp

import android.content.Context
import android.content.Intent
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

data class Movie(var imdbid: String, var Name: String){
    var Age = ""
    var plot = ""
    lateinit var Poster: Bitmap
    var Rating = ""
}

class MainActivity : AppCompatActivity() {

    lateinit var listViews: RecyclerView
    lateinit var editText: EditText
    lateinit var adapter : MyAdapter
    lateinit var spiner : ProgressBar
    //var vetor_ = ArrayList<String>()
    //var BMvetor_ = ArrayList<Bitmap>()
    var movieQuerry = ArrayList<Movie>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listViews = findViewById<RecyclerView>(R.id.recyclerviewid)
        editText = findViewById<EditText>(R.id.editText)


        adapter = MyAdapter(this,movieQuerry)
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
                    //val myint = Intent(this,MyDisplay::class.java)
                    //startActivity(myint)
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

    inner class downloadContent():AsyncTask<String,Boolean,Boolean >(){

        private fun getBitmapFromURL(src: String, name: String): Bitmap {
            try {
                val url = java.net.URL(src)
                val connection = url
                        .openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                val fos = openFileOutput(name, Context.MODE_PRIVATE)
                fos.write(input.readBytes())
                fos.close()

                val fol = openFileInput(name)
                val bmp = BitmapFactory.decodeStream(fol)
                fol.close()
                //return BitmapFactory.decodeStream(input)
                return bmp
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
                val returns = JSONObject(lines).getJSONArray("Search")

                urlConnection.disconnect()
                movieQuerry.clear()
                //vetor_.clear()
                //BMvetor_.clear()
                for (i in 0..(returns.length()-1)){
                    val query_temp = Movie("","")
                    //val imgurl = returns.getJSONObject(i).getString("Poster")
                    //BMvetor_.add(getBitmapFromURL(imgurl))
                    query_temp.imdbid = returns.getJSONObject(i).getString("imdbID")

                    url = URL("http://www.omdbapi.com/?apikey=24f3e826&i="+query_temp.imdbid)
                    urlConnection = url.openConnection() as HttpURLConnection
                    resp = BufferedInputStream(urlConnection.inputStream)
                    lines = BufferedReader(InputStreamReader(resp,StandardCharsets.UTF_8)).readText()
                    Log.i(TAG,lines)
                    val resumeJSON =JSONObject(lines)
                    query_temp.plot = resumeJSON.getString("Plot")
                    query_temp.Name = resumeJSON.getString("Title")
                    query_temp.Age = resumeJSON.getString("Released")
                    query_temp.Poster = getBitmapFromURL(returns.getJSONObject(i).getString("Poster"), query_temp.Name)

                    val rating_array = resumeJSON.getJSONArray("Ratings")
                    for (j in 0..(rating_array.length()-1)){
                        val sorc = rating_array.getJSONObject(j)
                        if(sorc.getString("Source").equals("Rotten Tomatoes")){
                            query_temp.Rating = sorc.getString("Value") + " from Rotten Tomatoes"
                            //vetor_.add(resumeJSON.getString("Title")+ '\n' +"Rating:"+ sorc.getString("Value"))
                        }
                    }
                    if(query_temp.Rating.equals("")) query_temp.Rating = resumeJSON.getString("imdbRating")+" from IMDb"
                    movieQuerry.add(query_temp)
                    urlConnection.disconnect()
                }
            }catch (e: JSONException){
                Log.i(TAG,"ERRO DE JSON")
                return false
            }catch (e: IOException){
                Log.i(TAG,"ERRO DE IO")
                return false
            }
            Log.i(TAG,movieQuerry.toString())
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
