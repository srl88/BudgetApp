package com.example.busget.budgetapp;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.*;

/**
 * THIS IS PART OF THE MODEL OF THE MVC
 */
public class DataBaseManager extends DataBaseCreator {

    //flag for categories
    private static final int FLAG_ = MIN_VALUE + 2;

    //stored data latest retrieved data
    static final ArrayList<String> names = new ArrayList<>();
    static final ArrayList<Integer> money = new ArrayList<>();

    //constructor
    public DataBaseManager(Context cnt){
        super(cnt);
    }

    //gets the name by index since the view and the model are hash.
    public String getName(int index){return names.get(index);}

    public int flag(){return FLAG_;}

    /**
     *BELOW ALL THE DATA RETRIVAL METHODS.
     */

    /**
     * Returns all the net amunt.
     * @return
     */
    public int getNet(){

        int in = budgetDao.getNetByIncome(true);
        int out = budgetDao.getNetByIncome(false);
        return in-out;

    }

    /**
     * Returns all the income amount.
     * @return
     */
    public ArrayList<String> getIncomeAndExpenses(){
        clearContainers();
        ArrayList<String> temp = new ArrayList<>();
        temp.add("Income: "+budgetDao.getNetByIncome(true)+" $");
        temp.add("Expenses: "+budgetDao.getNetByIncome(false)+" $");
        return temp;
    }



    /**
     * Returns all items by type and category in a string form ready to display
     * @param isIncome
     * @param category
     * @return
     */
    public ArrayList<String> getByCategory(Boolean isIncome, String category) {

        clearContainers();

        List<Budget> bud = budgetDao.findByCategory(isIncome, category);

        ArrayList<String> temp = new ArrayList<String>();

        for(Budget b : bud){
            if(!b.name.equals(FLAG_+"")){
                temp.add(updateAndReturn(b));
            }
        }

        return temp;
    }

    /**
     * Returns all items by type in a string form ready to display.
     * @param isIncome
     * @return
     */
    public ArrayList<String> getByIncome(Boolean isIncome) {
        clearContainers();
        ArrayList<String> temp = new ArrayList<String>();
        List<String> name = budgetDao.findByType(isIncome);


        for(String n: name){

            int m = budgetDao.GetAmountByCategory(isIncome, n);
            money.add(m);
            names.add(n);
            temp.add(n+": "+m+" $");
        }
        return temp;
    }

    /**
     * BELOW ALL THE METHODS TO INSERT NEW DATA
     */

    /**
     * Checks if the the addition is possible to avoid replicates.
     * @param n
     * @return true if it not present, false if already exixts
     */
    public Boolean isNew(String n){
        return !names.contains(n);
    }

    /**
     *
     * @param isIncome
     * @param n
     * @return
     */
    public ArrayList<String> insertCategory(Boolean isIncome, String n) {

        if(isNew(n)){

            Budget b = new Budget();
            b.setIsIncome(isIncome);
            b.setCategory(n);
            b.setAmount(0);
            b.setName(FLAG_+"");

            budgetDao.insertAll(b);

            return getByIncome(isIncome);

        }
        else{
            return null;
        }
    }

    /**
     *
     * @param isIncome
     * @param c
     * @param n
     * @param a
     * @return
     */
    public ArrayList<String> insertItems(Boolean isIncome, String c, String n, int a) {

        if(isNew(n)){

            Budget b = new Budget();
            b.setIsIncome(isIncome);
            b.setCategory(c);
            b.setAmount(a);
            b.setName(n);

            budgetDao.insertAll(b);

            return getByCategory(isIncome, c);
        }
        else{
            return null;
        }
    }

    /**
     * BELOW ALL THE METHODS TO DELETE DATA
     */

    /**
     * Deletes all categories by type
     */
    public ArrayList<String> deteleByType(Boolean isIncome){
        budgetDao.DeleteAllIncome(isIncome);
        return getIncomeAndExpenses();
    }


    /**
     * Deletes all items that belong to the income type and category
     * @param isIncome
     * @param pos
     * @return
     */
    public  ArrayList<String> deleteCategory(Boolean isIncome,int pos){
        String val = names.get(pos);
        budgetDao.DeleteByCategory(isIncome, val);
        return getByIncome(isIncome);
    }

    /**
     * Deletes the selected item.
     * @param isIncome
     * @param c
     * @param pos
     * @return
     */
    public ArrayList<String> deleteItem(Boolean isIncome, String c, int pos){
        String val = names.get(pos);
        budgetDao.DeleteItem(isIncome, c, val);
        return getByCategory(isIncome, c);
    }

    /**
     * BELOW THE METHOD TO UPDATE DATA. ONLY INDIVIDUALS ITEMS CAN BE UPDATED.
     */
    public ArrayList<String> updateItem(Boolean isIncome, String c, String n, int amount){
        budgetDao.UpdateItem(isIncome, c, n, amount);
        return getByCategory(isIncome, c);
    }


    /**
     * BELOW INTERNAL METHODS TO AVOID CODE DUPLICATION
     */
    /**
     * Updates internal lists from budget money.
     * @param b
     * @return
     */
    private String updateAndReturn(Budget b){
        names.add(b.getName());
        money.add(b.getAmount());
        String data = names.get(names.size()-1)+": "+money.get(money.size()-1)+" $";
        return data;
    }

    /**
     * Clear the containers whenever a new query is produced
     */
    private void clearContainers(){
        names.clear();
        money.clear();
    }


}
