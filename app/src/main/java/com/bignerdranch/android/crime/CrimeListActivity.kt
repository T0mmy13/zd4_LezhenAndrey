package com.bignerdranch.android.crime

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class CrimeListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crime_list)

        // Добавляем CrimeListFragment, если он еще не добавлен
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, CrimeListFragment())
                .commit()
        }
    }
}