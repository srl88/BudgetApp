package com.example.busget.budgetapp;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Class to access the database
 */
@Database(entities = {Budget.class}, version = 1)
public abstract class BudgetDataBase extends RoomDatabase{
    public static final String DB_NAME="budget.db";
    public abstract BudgetDao budgetDao();
}
