package com.example.test

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.gson.Gson


class MainActivity : AppCompatActivity() {

    // Déclaration des data classes

    data class  TypeInfo (
        val name: String,
        val url: String,
    )

    data class  PokemonType (
        val type: TypeInfo
    )

    data class  PokemonSprites (
        val front_default: String,
    )

    data class PokemonInfo(
        val id: Int,
        val name: String,
        val sprites: PokemonSprites,
        val types: List<PokemonType>,
    )

    data class Pokemon(
        val name: String,
        val url: String,
        var info: PokemonInfo,
    )

    data class PokemonList(
        val results: List<Pokemon>
    )

    // FONCTION AU LANCEMENT DE L'APPLICATION
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Récupère le recycler view par l'id
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerView)

        //Limiter à 151 pokemons et le afficher en format grid
        pokeLoadList(151) { pokemonList ->
            val pokeAdapter = PokeAdapter(pokemonList.results as MutableList<Pokemon>)

            //disposition des elements de l'application
            val gridLayoutManager = GridLayoutManager(this, 2)
            recyclerview.adapter = pokeAdapter
            recyclerview.layoutManager = gridLayoutManager
        }
    }

    // Afficher la loupe de recherche
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }

        val menuItem = menu.findItem(R.id.search)
        val searchView = menuItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return true
    }


    // Chargement de la liste
private fun pokeLoadList(
    limit: Int,
    cb: (PokemonList) -> Unit
) {
        val urlPokemons = "https://pokeapi.co/api/v2/pokemon?limit=${limit}&offset=0"
        .httpGet()
        .responseString { _, _, result ->
            when (result) {
                is Result.Failure -> {
                    val ex = result.getException()
                    println(ex)
                }
                is Result.Success -> {
                    val data = result.get()
                    val parsedJson = Gson().fromJson(data, PokemonList::class.java)
                    for (i in 0 until limit) {
                        pokeLoadProfile(parsedJson.results[i].url) { pokemon ->
                            parsedJson.results[i].info = pokemon
                            if (parsedJson.results[i].info.id == limit) {
                                cb(parsedJson)

                            }
                        }
                    }
                }
            }
        }
        urlPokemons.join()
}

    // Chargement des pokemons
private fun pokeLoadProfile (url: String, cb: (PokemonInfo) -> Unit) {

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
                    Gson().fromJson(data, PokemonInfo::class.java).apply {
                        cb(this)
                    }
                }
            }
        }
    httpAsync.join()
    }
}
