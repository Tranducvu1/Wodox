package com.starnest.data.studio.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.starnest.core.data.model.database.converter.DateConverter
import com.starnest.core.data.model.database.converter.UUIDConverter
import com.starnest.data.studio.datasource.dao.CategoryStudioDao
import com.starnest.data.studio.datasource.dao.DrawLayerDao
import com.starnest.data.studio.datasource.dao.DrawingDao
import com.starnest.data.studio.datasource.entity.CategoryStudioEntity
import com.starnest.data.studio.datasource.entity.DrawEntity
import com.starnest.data.studio.datasource.entity.DrawLayerEntity
import com.starnest.data.studio.datasource.migration.MigrateAddCategoryStudioV1

@Database(
    entities = [CategoryStudioEntity::class, DrawEntity::class, DrawLayerEntity::class],
    version = StudioDatabase.DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters(
    *[UUIDConverter::class, DateConverter::class]
)
abstract class StudioDatabase : RoomDatabase() {
    abstract fun categoryStudioDao(): CategoryStudioDao

    abstract fun drawingItemDao(): DrawingDao

    abstract fun drawingLayerDao(): DrawLayerDao


    companion object {
        const val DATABASE_NAME = "studio.db"
        const val DATABASE_VERSION = 1

        fun getDatabase(context: Context): StudioDatabase {
            return Room.databaseBuilder(
                context, StudioDatabase::class.java, DATABASE_NAME
            ).setJournalMode(JournalMode.TRUNCATE).addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    MigrateAddCategoryStudioV1.migrate(db)
                }
            }).build()
        }
    }

}