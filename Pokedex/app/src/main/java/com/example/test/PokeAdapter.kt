package com.example.test


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class PokeAdapter(private var dataSet: MutableList<MainActivity.Pokemon>) :
    RecyclerView.Adapter<PokeAdapter.ViewHolder>() {

/**
 * Provide a reference to the type of views that you are using
 * (custom ViewHolder).
 */

inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val pokeName: TextView = view.findViewById(R.id.pokeName)
    val pokeID: TextView = view.findViewById(R.id.pokeID)
    val pokeImage: ImageView = view.findViewById(R.id.pokeImage)


    init {
        view.setOnClickListener {
            println("Pokemon Selected.")
            val intent = Intent(view.context, ProfileActivity::class.java)
            intent.putExtra("EXTRA_MESSAGE", dataSet[adapterPosition].url)
            view.context.startActivity(intent)
        }
    }
}

// Create new views (invoked by the layout manager)
override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
    // Create a new view, which defines the UI of the list item
    val view = LayoutInflater.from(viewGroup.context)
        .inflate(R.layout.pokelist, viewGroup, false)
    return ViewHolder(view)
}

// Replace the contents of a view (invoked by the layout manager)
override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

    // Get element from your dataset at this position and replace the
    // contents of the view with that element
    viewHolder.pokeName.text = dataSet[position].info.name
    viewHolder.pokeID.text = dataSet[position].info.id.toString()

    Glide.with(viewHolder.pokeImage.context)
        .load(dataSet[position].info.sprites.front_default)
        .into(viewHolder.pokeImage)

}

// Return the size of your dataset (invoked by the layout manager)
override fun getItemCount() = dataSet.size

}

