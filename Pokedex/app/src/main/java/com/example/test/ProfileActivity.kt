package com.example.test

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.gson.Gson


class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val pokeImage = findViewById<ImageView>(R.id.pokeImage)
        val pokeName = findViewById<TextView>(R.id.pokeName)
        val pokeID = findViewById<TextView>(R.id.pokeID)

        val ss:String = intent.getStringExtra("name").toString()

        var url = intent.getStringExtra("EXTRA_MESSAGE")
        pokeLoadProfile(url as String) { pokemonInfo ->
            runOnUiThread() {
                pokeName.text = pokemonInfo.name
                pokeID.text = pokemonInfo.id.toString()
                if (pokemonInfo.types.size == 2) {

                } else {
                    Log.d("Profile", pokemonInfo.types.size.toString())
                }
                Glide.with(this)
                    .load(pokemonInfo.sprites.front_default)
                    .into(pokeImage)

            }
        }

    }
}

// Chargement des pokemons
private fun pokeLoadProfile (url: String, cb: (MainActivity.PokemonInfo) -> Unit) {

    val httpAsync = url
        .httpGet()
        .responseString { _, _, result ->
            when (result) {
                is Result.Failure -> {
                    val ex = result.getException()
                    println(ex)
                }
                is Result.Success -> {
                    val data = result.get()
                    Gson().fromJson(data, MainActivity.PokemonInfo::class.java).apply {
                        cb(this)
                    }
                }
            }
        }
    httpAsync.join()
}