package com.example.pockeitt.models
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "income_expense")
data class IncomeExpense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String,
    val emoji: String, // Added emoji property
    val amount: Double,
    val date: Date,
    val domain: String,
    val notes: String,
    val name: String,
    val repeat: RepeatType
)

enum class RepeatType {
    MONTHLY,
    WEEKLY,
    NEVER
}
