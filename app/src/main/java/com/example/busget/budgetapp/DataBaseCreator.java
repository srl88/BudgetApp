package com.example.busget.budgetapp;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

/**
 * DataBaseAdapter to retrieve database. Uses a singleton pattern
 * to avoid multiple instances of the database.
 */

public class  DataBaseCreator extends AppCompatActivity{

    protected static BudgetDataBase db = null;
    protected static BudgetDao budgetDao = null;
    protected Context class_context = null;


    protected DataBaseCreator(Context cnt){
        class_context = cnt;
        getDataBase();
    }

    /**
     * Creates a single db instance if not present.
     * @return
     */
    private BudgetDataBase CreateDataBase(){
        if(class_context == null){
            SourceConstants.ShitHappenes();
            return null;
        }
        if(db==null){
            db = Room.databaseBuilder(class_context.getApplicationContext(),
                    BudgetDataBase.class, BudgetDataBase.DB_NAME).build();

        }
        return db;
    }

    /**
     * Creates a single instance of BudgetDao based on a single instance of
     * the Databse ojbject.
     * @return
     */
    private BudgetDao getDataBase(){
        try{
            if(budgetDao==null){
                BudgetDataBase temp = CreateDataBase();
                budgetDao = temp.budgetDao();
            }
            return budgetDao;
        }catch(Exception e)
        {
            SourceConstants.ShitHappenes();
            return null;
        }
    }

}
