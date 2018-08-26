package com.example.busget.budgetapp;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.ArrayList;
import java.util.List;

/**
 * DataBase manager interface for queries.
 */

@Dao
public interface BudgetDao {

    /**
     * Main queries....
     * @return
     */
    @Query("Select * FROM budget")
    List<Budget> getAll();

    @Query("Select * FROM budget WHERE Category LIKE :catName and Income LIKE :isIncome")
    List<Budget> findByCategory(Boolean isIncome, String catName);

    @Query("Select DISTINCT Category FROM budget WHERE Income like :isIncome")
    List<String> findByType(Boolean isIncome);

    @Query("Select sum(Amount) FROM budget WHERE Income LIKE :isIncome")
    int getNetByIncome(Boolean isIncome);

    @Query("Select sum(Amount) FROM budget WHERE Category LIKE :cat and Income LIKE :isIncome")
    int GetAmountByCategory(Boolean isIncome,String cat);

    @Query("Delete FROM budget WHERE Income LIKE :isIncome")
    void DeleteAllIncome(Boolean isIncome);

    @Query("Delete FROM budget WHERE Category LIKE :cat and Income LIKE :isIncome")
    void DeleteByCategory(Boolean isIncome, String cat);

    @Query("Delete FROM budget WHERE Category LIKE :cat and Income LIKE :isIncome AND Name LIKE :n")
    void DeleteItem(Boolean isIncome, String cat, String n);

    @Query("UPDATE budget SET Amount = :newAmount WHERE  Category LIKE :cat and Income LIKE :isIncome AND Name LIKE :n")
    void UpdateItem(Boolean isIncome, String cat, String n, int newAmount);

    /**
     *The following methods support main CRUD operations in the local database
     * The take as parameter a list of objects Budget.
     * As long as there is an unique identifier as a variable in the class object
     * operations should preserve the database integrity.
     */
    @Update
    void updateAll(Budget... budget);

    @Insert
    void insertAll(Budget... budget);

    @Delete
    void deleteAll(Budget... budget);

}
