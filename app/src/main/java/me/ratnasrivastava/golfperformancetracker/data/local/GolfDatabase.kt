package me.ratnasrivastava.golfperformancetracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import me.ratnasrivastava.golfperformancetracker.data.local.dao.PlayerDao
import me.ratnasrivastava.golfperformancetracker.data.local.dao.ShotDao
import me.ratnasrivastava.golfperformancetracker.data.local.entity.PlayerEntity
import me.ratnasrivastava.golfperformancetracker.data.local.entity.ShotEntity

@Database(
    entities = [PlayerEntity::class, ShotEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GolfDatabase : RoomDatabase() {

    abstract fun playerDao(): PlayerDao

    abstract fun shotDao(): ShotDao

    companion object {
        const val DATABASE_NAME = "golf_performance.db"
    }
}