package com.example.busget.budgetapp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Entity Class for Room DataBase configuration.
 */
@Entity(tableName = "budget")
public class Budget {
    /**
     * Primary key for main table
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="Id")
    private int id;

    /**
     * All other columns.
     * ExpenseOrIncome can only be expense=false or income=true
     * Category can be anything inputted by the user. Each Category belongs to a ExpenseOrIncome
     * Name same as category, however each name has a category
     * Amount is the amount which would be + or - based on ExpenseOrIncome
     */
    @ColumnInfo (name = "Income")
    public Boolean isIncome;

    @ColumnInfo (name ="Category")
    public String category;

    @ColumnInfo (name="Name")
    public String name;

    @ColumnInfo (name="Amount")
    public int amount;

    /**
     * Constructor
    **/
    public Budget(){}

    /**
     * Getters
     */
    public Boolean getIsIncome(){
        return isIncome;
    }

    public String getCategory(){
        return category;
    }

    public String getName(){
        return name;
    }

    public int getAmount(){
        return amount;
    }

    public int getId(){ return id; }

    /**
     * Setters
     */
    public void setId(int i){ id=i; }

    public void setIsIncome(Boolean i){
        isIncome = i;
    }

    public void setName(String n){
        name = n;
    }

    public void setCategory(String c){
        category=c;
    }

    public void setAmount(int a){
        amount = a;
    }

}
