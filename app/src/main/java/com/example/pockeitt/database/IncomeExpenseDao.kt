package com.example.pockeitt.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pockeitt.models.Domain
import com.example.pockeitt.models.IncomeExpense
import java.util.Date

@Dao
interface IncomeExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(incomeExpense: IncomeExpense)

    @Update
    fun update(incomeExpense: IncomeExpense)

    @Query("SELECT * FROM income_expense ORDER BY date DESC")
    fun getAllIncomeExpenses(): List<IncomeExpense>


    @Query("SELECT * FROM income_expense WHERE domain = :domain AND date >= :startOfMonth AND date <= :endOfMonth")
    fun getIncomeExpensesByDomainAndMonth(
        domain: Domain,
        startOfMonth: Date,
        endOfMonth: Date,
    ): List<IncomeExpense>

    @Query("SELECT * FROM income_expense WHERE domain = :domain AND date >= :startOfMonth AND date <= :endOfMonth")
    fun getIncomeExpensesByMonth(
        startOfMonth: Date,
        endOfMonth: Date,
        domain: Domain,
    ): List<IncomeExpense>

}
