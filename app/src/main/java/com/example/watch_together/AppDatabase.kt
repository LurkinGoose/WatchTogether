package com.example.watch_together

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.watch_together.models.FavoriteMovieDao
import com.example.watch_together.models.FavoriteMovieEntity

@Database(entities = [FavoriteMovieEntity::class], version = 2)  // Увеличиваем версию на 2
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteMovieDao(): FavoriteMovieDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Миграция с версии 1 на 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Добавляем новое поле в таблицу favorite_movies
                // Важно, чтобы это поле было добавлено с правильным значением по умолчанию
                database.execSQL("ALTER TABLE favorite_movies ADD COLUMN addedAt INTEGER DEFAULT 0 NOT NULL")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            // Синхронизация синглтона для обеспечения потокобезопасности
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "favorite_movies_db"
                )
                    .addMigrations(MIGRATION_1_2)  // Добавляем миграцию
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
