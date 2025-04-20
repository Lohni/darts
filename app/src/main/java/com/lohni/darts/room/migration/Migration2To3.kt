package com.lohni.darts.room.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3= object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        val sql = """
            ALTER TABLE PLAYER ADD p_default INTEGER NOT NULL DEFAULT 0
        """.trimIndent()
        db.execSQL(sql)
    }
}