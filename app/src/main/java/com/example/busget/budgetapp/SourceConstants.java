package com.example.busget.budgetapp;

import android.content.Context;

import java.util.ArrayList;

/**
 * THIS IS THE CONTROLLER IN THE MDC
 */

public  class SourceConstants {


    /**
     * All variables below are used to track the current state of the app
     * Expenses vs Income
     * Error vs Success
     * Uploading Data vs Done Uploading
     * EditingData vs Inserts/Deletes
     * Breakdown of the data -> EXPENSES/INCOME -> CATEGORIES FOR EACH -> ITEMS FOR EACH CATEGORY
     * What Category is the user currently looking
     * What Item is the user current looking
     */
    private static Boolean NOTIFY_USER_ = false;

    private static final Boolean ERROR_ = true;
    private static final Boolean SUCCESS_ = false;

    private static Boolean HAS_ERROR_OCCURRED_ = SUCCESS_;


    private static final Boolean UPLOADING_ = true;
    private static final Boolean DONE_UPLOADING_=false;

    private static Boolean CURRENTLY_UPLOADING = DONE_UPLOADING_;


    private static final Boolean EDIT_=true;
    private static final Boolean DONE_EDIT_=false;

    private static Boolean EDITING_=DONE_EDIT_;


    private static final String MAIN_ = "MAIN";
    private static final String CATEGORY_ = "CATEGORY";
    private static final String ITEMS_ = "ITEMS";

    private static String CURRENT_ = MAIN_;

    private static String CATEGORY_NAME_=null;
    private static String ITEM_NAME_=null;

    private static final Boolean INCOME_ = true;
    private static final Boolean EXPENSE_ = false;

    private static Boolean ISINCOME_;

    /**
     * methods to  update and retrieve if customer is editing data
     */
    public static void NewEdit(){EDITING_ = EDIT_; }
    public static void DoneEdit(){EDITING_=DONE_EDIT_;}
    public static Boolean IsEditing(){return EDITING_;}

    /**
     * methods to retrieve and set the category
     */
    public static void SetCategory(String n) {CATEGORY_NAME_ = n;}
    public static String GetCategory() {return CATEGORY_NAME_;}

    /**
     *Methods to retrieve and set the items
     */
    public static void SetItem(String n) {ITEM_NAME_ =n;}
    public static String GetItem() {return ITEM_NAME_;}

    /**
     *method to track whether or not data is being uploaded
     */
    public static void startUploading(){CURRENTLY_UPLOADING = UPLOADING_;}
    public static void doneUploading(){CURRENTLY_UPLOADING = DONE_UPLOADING_;}
    public static Boolean isUploading(){return CURRENTLY_UPLOADING;}

    /**
     *Methods to notify the user when data is done uploading.
     *
     */
    public static Boolean getNotification(){return NOTIFY_USER_;}
    public static void setNotification(){
        if(NOTIFY_USER_) {NOTIFY_USER_ = false;}
        else{NOTIFY_USER_=true;}
    }
    /**
     *methods to track down if error has been encounterred.
     *
     */
    public static Boolean isError(){return HAS_ERROR_OCCURRED_;}
    public static void ShitHappenes(){HAS_ERROR_OCCURRED_ = ERROR_;}
    public static void GoodBoy(){HAS_ERROR_OCCURRED_ = SUCCESS_;}

    /**
     * Income vs expenses
     *
     */
    public static Boolean isIncome(){return ISINCOME_; }
    public static void setExpenses(){ ISINCOME_=EXPENSE_; }
    public static void setIncome(){ ISINCOME_=INCOME_; }

    /**
     *Methods to return the current context of the app.
     *
     */
    public static Boolean IsMain(){  return CURRENT_.equals(MAIN_ ); }
    public static Boolean IsCategory(){ return CURRENT_.equals(CATEGORY_); }
    public static Boolean IsItem(){ return CURRENT_.equals(ITEMS_); }



    /**
     *Updates the virtual activity to the next one if possible. Returns true if updated
     */
    public static Boolean NextVirtualContext(){

        if(IsMain()){
            CURRENT_=CATEGORY_;
            return true;
        }
        else if(IsCategory()){
            CURRENT_= ITEMS_;
            return true;
        }
        return false;
    }


    /**
     *Updates the virtual activity to the previous one if possible. Returns true if updated
     */
    public static Boolean BackVirtualContext(){
        if(IsItem()){
            CURRENT_=CATEGORY_;
            return true;
        }
        else if(IsCategory()){
            CURRENT_= MAIN_ ;
            return true;
        }
        return false;
    }

    /**
     *
     * @param displayList
     * @param newValues
     */
    public static void copyArrayList(ArrayList<String> displayList, ArrayList<String> newValues){
        displayList.clear();
        for(String s: newValues){
            displayList.add(s);
        }
    }

    /**
     * Method to transform from pixel to densitypixel
     * @param px
     * @param ctx
     * @return
     */
    public static int pixelToDenpix(int px, Context ctx)
    {
        return (int)(px / ctx.getResources().getDisplayMetrics().density);
    }

    /**
     * Method to transform from density pixel to pixel.
     * @param dp
     * @param ctx
     * @return
     */
    public static int denpixTopizel(int dp, Context ctx)
    {
        return (int)(dp * ctx.getResources().getDisplayMetrics().density);
    }
}
