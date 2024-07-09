package com.example.pockeitt.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pockeitt.models.IncomeExpense
import com.example.pockeitt.models.RepeatType

@Dao
interface IncomeExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insert(incomeExpense: IncomeExpense)

    @Update
     fun update(incomeExpense: IncomeExpense)
//
//    @Query("DELETE FROM income_expense WHERE id = :id")
//    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM income_expense ORDER BY date DESC")
     fun getAllIncomeExpenses(): List<IncomeExpense>

//    @Query("SELECT * FROM income_expense WHERE id = :id")
//    suspend fun getIncomeExpenseById(id: Int): IncomeExpense?`

    // Example query to get all expenses for a specific category
//    @Query("SELECT * FROM income_expense WHERE category = :category ORDER BY date DESC")
//    suspend fun getExpensesByCategory(category: String): List<IncomeExpense>
//
//    // Example query to get all income entries for a specific domain
//    @Query("SELECT * FROM income_expense WHERE domain = :domain AND amount > 0 ORDER BY date DESC")
//    suspend fun getIncomeByDomain(domain: String): List<IncomeExpense>
//
//    // Example query to get expenses with amount greater than a specified value
//    @Query("SELECT * FROM income_expense WHERE amount > :minAmount ORDER BY date DESC")
//    suspend fun getExpensesAboveAmount(minAmount: Double): List<IncomeExpense>
//
//    // Example query to get all income entries for a specific repeat type
//    @Query("SELECT * FROM income_expense WHERE repeat = :repeatType ORDER BY date DESC")
//    suspend fun getIncomeByRepeatType(repeatType: RepeatType): List<IncomeExpense>
//
//    // Add more specific queries as needed based on your application requirements
}
