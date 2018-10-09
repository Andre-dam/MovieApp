package com.example.andre.movieapp

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.io.*
import java.nio.charset.StandardCharsets
import org.json.JSONException


private val TAG = "MyActivity"

class MainActivity : AppCompatActivity() {

    lateinit var listViews: ListView
    lateinit var editText: EditText
    var vetor_ = ArrayList<String>()
    lateinit var adapter : ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listViews = findViewById<ListView>(R.id.recyclerviewid)
        editText = findViewById<EditText>(R.id.editText)
        adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, vetor_)

        listViews.adapter = adapter

        editText.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    downloadContent().execute()
                    true
                }
                else -> false
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    inner class downloadContent:AsyncTask<String,String,Boolean >(){
        override fun doInBackground(vararg params: String?): Boolean {

            Log.i(TAG,"Entrou em doInback")

            var url = URL("http://www.omdbapi.com/?apikey=24f3e826&s="+editText.text.toString()+"&type=movie")
            var urlConnection = url.openConnection() as HttpURLConnection
            try {
                var resp = BufferedInputStream(urlConnection.inputStream)
                var lines = BufferedReader(InputStreamReader(resp,StandardCharsets.UTF_8)).readText()
                Log.i(TAG,lines)
                var returns = JSONObject(lines).getJSONArray("Search")

                urlConnection.disconnect()
                vetor_.clear()
                for (i in 0..(returns.length()-1)){
                    //vetor_.add(returns.getJSONObject(i).getString("imdbID"))
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

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
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
