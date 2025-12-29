package com.starnest.data.studio.datasource.migration

import androidx.sqlite.db.SupportSQLiteDatabase
import com.starnest.core.data.model.DatePattern
import com.starnest.core.extension.format
import java.util.Date

object MigrateAddCategoryStudioV1 {

    fun migrate(database: SupportSQLiteDatabase) {
        val today = Date().format(DatePattern.YYYY_MM_DD_HH_MM_SS)

        val values = arrayListOf(
            "('ff55c8f1-65ee-49b7-a269-6d9271f96415', 'Study', 0, '$today', '$today', NULL)",
            "('b0640984-f910-4953-81e5-72725b43d971', 'Work', 0, '$today', '$today', NULL)"
        )

        database.execSQL(
            """
            INSERT INTO CategoryStudio (id, name, isHidden,createdAt, updatedAt, deletedAt)
            VALUES ${values.joinToString(",")}
            """.trimIndent()
        )
    }
}
