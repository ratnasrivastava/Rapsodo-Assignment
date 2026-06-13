package me.ratnasrivastava.golfperformancetracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import me.ratnasrivastava.golfperformancetracker.data.local.entity.ShotEntity

@Dao
interface ShotDao {

    // Observes all shots for a given player, ordered by ball speed (fastest first).
    @Query("SELECT * FROM shots WHERE playerId = :playerId ORDER BY ballSpeed DESC")
    fun observeShotsForPlayer(playerId: String): Flow<List<ShotEntity>>

    // Inserts or replaces a batch of shots (used after a network fetch).
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(shots: List<ShotEntity>)

    // Clears all shots for a given player.
    @Query("DELETE FROM shots WHERE playerId = :playerId")
    suspend fun clearForPlayer(playerId: String)

    // Clears the entire shots table.
    @Query("DELETE FROM shots")
    suspend fun clear()

    // Returns the number of cached shots for a player (used to decide whether to refresh).
    @Query("SELECT COUNT(*) FROM shots WHERE playerId = :playerId")
    suspend fun countForPlayer(playerId: String): Int
}