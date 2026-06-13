package me.ratnasrivastava.golfperformancetracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import me.ratnasrivastava.golfperformancetracker.data.local.entity.PlayerEntity

@Dao
interface PlayerDao {

    // Observes all players, ordered by name. Emits again on any change.
    @Query("SELECT * FROM players ORDER BY name ASC")
    fun observePlayers(): Flow<List<PlayerEntity>>

    // Observes a single player by id; emits null if not cached.
    @Query("SELECT * FROM players WHERE id = :playerId LIMIT 1")
    fun observePlayer(playerId: String): Flow<PlayerEntity?>

    // Inserts or replaces a batch of players (used after a network fetch).
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(players: List<PlayerEntity>)

    // Clears the players table.
    @Query("DELETE FROM players")
    suspend fun clear()

    // Returns the number of cached players (used to decide whether to refresh).
    @Query("SELECT COUNT(*) FROM players")
    suspend fun count(): Int
}