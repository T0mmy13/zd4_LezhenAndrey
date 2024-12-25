package com.bignerdranch.android.crime

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CrimeAdapter(
    private var crimes: List<Crime> = emptyList(),
    private val onCrimeClick: (Crime) -> Unit // Lambda for item click
) : RecyclerView.Adapter<CrimeAdapter.CrimeViewHolder>() {

    class CrimeViewHolder(itemView: View, val onCrimeClick: (Crime) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)

        fun bind(crime: Crime) {
            titleTextView.text = crime.title
            dateTextView.text = crime.date.toString() // Можно отформатировать дату перед отображением
            itemView.setOnClickListener {
                onCrimeClick(crime) // Call the click handler with the crime
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_crime, parent, false)
        return CrimeViewHolder(view, onCrimeClick)
    }

    override fun onBindViewHolder(holder: CrimeViewHolder, position: Int) {
        holder.bind(crimes[position])
    }

    override fun getItemCount(): Int = crimes.size

    fun submitList(newCrimes: List<Crime>) {
        crimes = newCrimes
        notifyDataSetChanged()
    }
}