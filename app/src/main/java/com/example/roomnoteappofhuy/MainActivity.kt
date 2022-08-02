package com.example.roomnoteappofhuy

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomnoteappofhuy.adapter.NoteRVAdapter
import com.example.roomnoteappofhuy.database.NoteDatabase
import com.example.roomnoteappofhuy.model.Note
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: NoteRVAdapter

    private val noteDatabase by lazy {NoteDatabase.getDatabase(this).noteDao()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setRecycleView()
        observeNotes()
    }

    private fun observeNotes() {
        lifecycleScope.launch {
            noteDatabase.getNotes().collect{ notesList ->
                if (notesList.isNotEmpty()) {
                    adapter.submitList(notesList)
                }
            }
        }
    }

    private fun setRecycleView() {
        val rvNotes = findViewById<RecyclerView>(R.id.rvNotes)
        rvNotes.layoutManager = LinearLayoutManager(this)
        rvNotes.setHasFixedSize(true)
        adapter = NoteRVAdapter()
        adapter.setItemListener(object :RecycleClickListener{
            //Tap X to remove the note
            override fun onItemClickRemove(position: Int){
                val notesList = adapter.currentList.toMutableList()
                val noteText = notesList[position].noteText
                val noteDateAdded = notesList[position].dateAdded
                val removeNote = Note(noteDateAdded, noteText)
                lifecycleScope.launch {
                    noteDatabase.deleteNote(removeNote)
                }
            }

            override fun onItemClickAdd(position: Int) {
                val intent = Intent(this@MainActivity,AddNoteActivity::class.java)
                val notesList = adapter.currentList.toMutableList()
                intent.putExtra("note_date_added",notesList[position].dateAdded)
                intent.putExtra("note_text",notesList[position].noteText)
                editNoteResultLauncher.launch(intent)
            }

        })
        rvNotes.adapter = adapter

    }

    private val newNoteResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Get the new note from the AddNoteActivity
                val notes = adapter.currentList.toMutableList()
                val noteDateAdded = Date()
                val noteText = result.data?.getStringExtra("note_text")
                // Add the new note at the top of the list
                val newNote = Note(noteDateAdded, noteText ?: "")
                notes.add(newNote)
                // Update RecyclerView
                adapter.submitList(notes)
                lifecycleScope.launch {
                    noteDatabase.addNote(newNote)
                }
            }
        }

    val editNoteResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val notes = adapter.currentList.toMutableList()
                // Get the edited note from the AddNoteActivity
                val noteDateAdded = result.data?.getSerializableExtra("note_date_added") as Date
                val noteText = result.data?.getStringExtra("note_text")
                // Update the note in the list
                val editedNote = Note(noteDateAdded, noteText ?: "")
                adapter.submitList(notes)
                adapter.notifyDataSetChanged()
                lifecycleScope.launch {
                    noteDatabase.updateNote(editedNote)
                }
            }
        }

    //Tap the + button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.add_note_button){
            val intent = Intent(this, AddNoteActivity::class.java)
            newNoteResultLauncher.launch(intent)
            return true
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_notes, menu)
        return true
    }
}