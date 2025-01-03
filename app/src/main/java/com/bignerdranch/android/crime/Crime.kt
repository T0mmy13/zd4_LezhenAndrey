package com.bignerdranch.android.crime

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Crime(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var title: String = "",
    var details: String = "",  // Новое поле для деталей
    var date: Date = Date(),
    var isSolved: Boolean = false
)