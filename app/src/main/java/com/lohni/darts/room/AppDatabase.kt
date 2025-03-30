package com.lohni.darts.room

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lohni.darts.room.dao.CheckoutDao
import com.lohni.darts.room.dao.GameDao
import com.lohni.darts.room.dao.GameModeDao
import com.lohni.darts.room.dao.HistoryDao
import com.lohni.darts.room.dao.PlayerDao
import com.lohni.darts.room.dao.SettingsDao
import com.lohni.darts.room.dao.StatisticsDao
import com.lohni.darts.room.entities.CheckoutTable
import com.lohni.darts.room.entities.Game
import com.lohni.darts.room.entities.GameMode
import com.lohni.darts.room.entities.GameModeConfig
import com.lohni.darts.room.entities.GameModeStep
import com.lohni.darts.room.entities.GamePlayer
import com.lohni.darts.room.entities.Leg
import com.lohni.darts.room.entities.Player
import com.lohni.darts.room.entities.ScoreCalculation
import com.lohni.darts.room.entities.Set
import com.lohni.darts.room.entities.Setting
import com.lohni.darts.room.entities.Throw

@Database(
    version = 1,
    entities = [Game::class, GameMode::class, GameModeConfig::class, GameModeStep::class, GamePlayer::class, Leg::class, Player::class, ScoreCalculation::class, Set::class, Setting::class, Throw::class, CheckoutTable::class]
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
    abstract fun gameModeDao(): GameModeDao
    abstract fun playerDao(): PlayerDao
    abstract fun gameDao(): GameDao
    abstract fun historyDao(): HistoryDao
    abstract fun statisticsDao(): StatisticsDao
    abstract fun checkoutDao(): CheckoutDao

    companion object {
        private const val DATABASE_NAME = "darts.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        DATABASE_NAME
                    ).createFromAsset("database/darts.db").build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}