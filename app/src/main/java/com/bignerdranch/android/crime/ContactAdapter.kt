package com.bignerdranch.android.crime

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(private val contacts: Map<Char, List<String>>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val sortedKeys = contacts.keys.sorted()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            // Заголовок
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            HeaderViewHolder(view)
        } else {
            // Контакт
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            ContactViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val index = getItemIndex(position)
        if (holder is HeaderViewHolder) {
            holder.bind(sortedKeys[index])
        } else if (holder is ContactViewHolder) {
            holder.bind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 || isHeader(position)) 0 else 1
    }

    override fun getItemCount(): Int {
        var count = 0
        for (key in sortedKeys) {
            count += 1 + (contacts[key]?.size ?: 0)
        }
        return count
    }

    private fun isHeader(position: Int): Boolean {
        var offset = 0
        for (key in sortedKeys) {
            if (position == offset) return true
            offset += 1 + (contacts[key]?.size ?: 0)
        }
        return false
    }

    private fun getItem(position: Int): String {
        var offset = 0
        for (key in sortedKeys) {
            val size = 1 + (contacts[key]?.size ?: 0)
            if (position < offset + size) {
                return contacts[key]!![position - offset - 1]
            }
            offset += size
        }
        throw IndexOutOfBoundsException("Invalid item position")
    }

    private fun getItemIndex(position: Int): Int {
        var offset = 0
        for (index in sortedKeys.indices) {
            val size = 1 + (contacts[sortedKeys[index]]?.size ?: 0)
            if (position < offset + size) {
                return index
            }
            offset += size
        }
        throw IndexOutOfBoundsException("Invalid item position")
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView: TextView = view.findViewById(android.R.id.text1)
        fun bind(char: Char) {
            textView.text = char.toString()
        }
    }

    class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView: TextView = view.findViewById(android.R.id.text1)
        fun bind(contact: String) {
            textView.text = contact
        }
    }
}