package com.example.roomnoteappofhuy.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.roomnoteappofhuy.R
import com.example.roomnoteappofhuy.RecycleClickListener
import com.example.roomnoteappofhuy.model.Note

class NoteRVAdapter : ListAdapter<Note, NoteRVAdapter.Holder>(DiffCallback()) {
    class Holder(view: View) : RecyclerView.ViewHolder(view)

    private lateinit var listener: RecycleClickListener
    fun setItemListener(listener: RecycleClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): Holder {
        // Create a new view, which defines the UI of the list item
        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_note, viewGroup, false)
        val holder = Holder(view)


        val noteDelete = holder.itemView.findViewById<ImageView>(R.id.note_delete)
        noteDelete.setOnClickListener {
            listener.onItemClickRemove(holder.adapterPosition)
        }

        val note = holder.itemView.findViewById<CardView>(R.id.note)
        note.setOnClickListener {
            listener.onItemClickAdd(holder.adapterPosition)
        }
        return holder
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: Holder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val currentItem = getItem(position)
        val noteText = holder.itemView.findViewById<TextView>(R.id.note_text)
        noteText.text = currentItem.noteText
    }

    class DiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note) =
            oldItem.dateAdded == newItem.dateAdded

        override fun areContentsTheSame(oldItem: Note, newItem: Note) =
            oldItem == newItem
    }

}