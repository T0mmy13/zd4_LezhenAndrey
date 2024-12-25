package com.bignerdranch.android.crime

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface CrimeDao {
    @Query("SELECT * FROM crime")
    fun getCrimes(): Flow<List<Crime>>

    @Query("SELECT * FROM crime WHERE id = :id")
    suspend fun getCrime(id: UUID): Crime?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCrime(crime: Crime)
}