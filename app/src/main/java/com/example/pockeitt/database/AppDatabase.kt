package com.example.pockeitt.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pockeitt.models.IncomeExpense

@Database(entities = [IncomeExpense::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun incomeExpenseDao(): IncomeExpenseDao
}