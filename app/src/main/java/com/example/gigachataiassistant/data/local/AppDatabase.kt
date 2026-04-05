package com.example.gigachataiassistant.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlin.jvm.Volatile

@Database(
    entities = [ChatEntity::class, MessageEntity::class],
    version = 4,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: build(context).also { built -> instance = built }
            }

        private fun build(context: Context): AppDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "gigachat.db",
            )
                .addMigrations(MIGRATION_3_4)
                .fallbackToDestructiveMigration(true)
                .build()
    }
}

private val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE chats ADD COLUMN lastActivityAt INTEGER NOT NULL DEFAULT 0")
        db.execSQL("UPDATE chats SET lastActivityAt = createdAt")
    }
}