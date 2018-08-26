package com.example.busget.budgetapp;


import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.SortedMap;


import SwipeMenu.*;

/**
 * THIS IS THE VIEW OF THE MVC
 */
class MainActivity extends AppCompatActivity {

    /**
     * Variables to store the NETINCOME since it is always displayed.
     */
    private int netIncome = 0;

    ArrayList<Budget> bud = new ArrayList<Budget>();
    /**
     * Variables to Display the data and menu.
     */
    TextView totalIncome;
    FloatingActionButton add;
    SwipeMenuItem next;
    SwipeMenuItem deleteItem;


    /**
     * Variable for data manager
     */
    DataBaseManager db;

    /**
     * Variable for uploading display
     */
    ProgressBar bar;

    /**
     * Variable to store the data being displaied, adapter and Display view
     */
    ArrayList<String> list;
    ArrayAdapter adapter;
    SwipeMenuListView menu;
    SwipeMenuCreator creator;
    static int VIEW_POSITION_CLICK_;

    /**
     * On create menu. Initializes all components and it's only call once.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setup the initial view
        InitialSetUp();

        //Setup event Listeners for all UI components
        SetUpEventListener();



    }

    /**
     * Override onBackPress methot to simulate different activities.
     */
    @Override
    public void onBackPressed() {

        //while we are not in main
        if(SourceConstants.BackVirtualContext()){
                Budget b = new Budget();
                b.isIncome = SourceConstants.isIncome();
                b.category = SourceConstants.GetCategory();
                new ThreadFactory(new RetrieveData()).execute(b);
        }
        else{
            finish();
        }

    }

    /**
     * Shows the add button if state is not in main to simulate different activties.
     */
    private void UpdateUIComponents(){
        if(!SourceConstants.isUploading()){

            bar.setVisibility(View.GONE);

            menu.setVisibility(View.VISIBLE);

            if(SourceConstants.IsMain()){ add.hide();
            }else{add.show();}

            totalIncome.setText(netIncome+" $");
            int color = getResources().getColor(R.color.colorMoney);
            if(netIncome<0){color = getResources().getColor(R.color.colorAccent); }
            totalIncome.setTextColor(color);

        }else{
            menu.setVisibility(View.GONE);
            add.hide();
            bar.setVisibility(View.VISIBLE);
            totalIncome.setText(R.string.uploading);
            totalIncome.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        if(SourceConstants.getNotification()){
            SourceConstants.setNotification();
            Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.success), Toast.LENGTH_SHORT);
            toast.show();
        }


    }

    /**
     * Sets up all the UI and helper elements to display the data.
     */
    private void InitialSetUp(){

        //Initialize the list
        list = new ArrayList<String>();

        //get UI components by id
        totalIncome = (TextView) findViewById(R.id.total);
        add = (FloatingActionButton) findViewById(R.id.add_button);
        menu = (SwipeMenuListView) findViewById(R.id.listView);
        bar = (ProgressBar) findViewById(R.id.pb);


        //Starts the database
        db = new DataBaseManager(getApplicationContext());

        //Initialized and set the swipeMenu and the Adapter
        adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, list);
        menu.setAdapter(adapter);

        //Initialize all buttons in menu
        next = new SwipeMenuItem (getApplicationContext());
        deleteItem = new SwipeMenuItem (getApplicationContext());

        creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {//#3F51B5
                //creates the next button
                next.setBackground(new ColorDrawable(Color.rgb(0x3F, 0x51,0xB5)));
                next.setWidth(SourceConstants.denpixTopizel(90, MainActivity.this));
                next.setIcon(R.drawable.ic_next);
                menu.addMenuItem(next);

                // Detele Item button is created. All states will display it
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xFF,0x40, 0x81)));
                deleteItem.setWidth(SourceConstants.denpixTopizel(90, MainActivity.this));
                deleteItem.setIcon(R.drawable.ic_delete);
                menu.addMenuItem(deleteItem);
            }
        };

        //retrieves the initial data
        new ThreadFactory(new RetrieveData()).execute((Budget)null);

        menu.setMenuCreator(creator);

    }


    /**
     * Sets up all the eventListeners
     */
    private void SetUpEventListener(){

        //Event Listener for SwipeMenu
        menu.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                // gets index
                VIEW_POSITION_CLICK_ = position;
                // uddates controller
                if(SourceConstants.IsMain()&&position==0){SourceConstants.setIncome();}
                else if(SourceConstants.IsMain()&&position==1){SourceConstants.setExpenses();}
                else if(SourceConstants.IsCategory()){SourceConstants.SetCategory(db.getName(VIEW_POSITION_CLICK_));}
                // creates object budget for CRUD operations
                Budget b = new Budget();
                b.isIncome = SourceConstants.isIncome();
                b.category = SourceConstants.GetCategory();

                switch(index){
                    case 0: // next state
                        if(SourceConstants.NextVirtualContext()){
                            new ThreadFactory(new RetrieveData()).execute(b);
                        }
                        else{ // individual items
                            SourceConstants.SetItem(db.getName(VIEW_POSITION_CLICK_));
                            SourceConstants.NewEdit();
                            BuildAddDialog(MainActivity.this);
                        }
                        break;
                    case 1: // delete
                        if(SourceConstants.IsItem()){b.name = db.getName(VIEW_POSITION_CLICK_);}
                        BuildDeleteDialog(MainActivity.this, b); // we delete from a new alertWindow to confirm with user
                        break;
                }
                return true; // closes the menu
            }
        });

        //Event Listener for float button
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BuildAddDialog(MainActivity.this);
            }
        });
    }


    /**
     *Creates a alert window to verify data deletion from database
     * @param cnt
     */
    public void BuildDeleteDialog(Activity cnt, final Budget b){
        AlertDialog.Builder dial = new AlertDialog.Builder(cnt);
        View dialView = getLayoutInflater().inflate(R.layout.activity_input, null);
        String msg;
        // Create the text message depending on the state
        if(SourceConstants.IsMain()){
            msg =  getResources().getString(R.string.delete_all);
            if(SourceConstants.isIncome()){msg = msg+" Income?";}
            else{msg = msg+" Expenses?";}
        }
        else if(SourceConstants.IsCategory()){
            msg = getResources().getString(R.string.delete_cat)+" "+SourceConstants.GetCategory();
        }
        else{
            msg = getResources().getString(R.string.delete_item)+" "+SourceConstants.GetItem();
        }

        // hides all elemnets not visible
        TextView info = (TextView) dialView.findViewById(R.id.instructions);
        EditText name = (EditText) dialView.findViewById(R.id.name);
        name.setVisibility(View.GONE);
        EditText amount = (EditText) dialView.findViewById(R.id.amount);
        amount.setVisibility(View.GONE);
        Button okBtn = (Button) dialView.findViewById(R.id.okBtn);
        Button cancelBtn = (Button) dialView.findViewById(R.id.cancelBtn);

        info.setText(msg);

        dial.setView(dialView);
        final AlertDialog dialTwo = dial.create();
        // event listeners
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ThreadFactory(new DeleteData()).execute(b); // deletes upon confirmation
                dialTwo.cancel();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialTwo.cancel();
            }
        });

        dialTwo.show();
    }
    /**
     *Same as alert but for insertions
     * @param cnt
     */
    public  void  BuildAddDialog(Activity cnt){


        AlertDialog.Builder dial = new AlertDialog.Builder(cnt);
        View dialView = getLayoutInflater().inflate(R.layout.activity_input, null);


        TextView info = (TextView) dialView.findViewById(R.id.instructions);
        final EditText name = (EditText) dialView.findViewById(R.id.name);
        final EditText amount = (EditText) dialView.findViewById(R.id.amount);
        Button okBtn = (Button) dialView.findViewById(R.id.okBtn);
        Button cancelBtn = (Button) dialView.findViewById(R.id.cancelBtn);

        // sets the proper data display
        if(SourceConstants.IsCategory()){
            info.setText(R.string.add_cat);
            amount.setVisibility(View.GONE);
            name.setHint(R.string.cat_hint);
        }
        else if(SourceConstants.IsEditing()){
            info.setText(R.string.update_item);
            String temp = info.getText().toString() +" "+SourceConstants.GetItem();
            info.setText(temp);
            name.setVisibility(View.GONE);
            amount.setHint(R.string.new_amount);
        }
        else{
            info.setText(R.string.add_item);
            name.setHint(R.string.item_hint);
            amount.setHint(R.string.amount_hint);
        }


        dial.setView(dialView);
        final AlertDialog dialTwo = dial.create();

        // event listeners with error checking
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SourceConstants.IsEditing()) {
                    if (!amount.getText().toString().isEmpty()) {
                        int myNum = 0;
                        try {
                            myNum = Integer.parseInt(amount.getText().toString());
                            if (myNum < 0) myNum = myNum * -1;
                            Budget b = new Budget();
                            b.isIncome = SourceConstants.isIncome();
                            b.category = SourceConstants.GetCategory();
                            b.name = SourceConstants.GetItem();
                            b.amount = myNum;
                            new ThreadFactory(new UpdateData()).execute(b);
                        } catch (NumberFormatException nfe) {
                            amount.setError(getResources().getString(R.string.input));
                        }
                    } else {
                        amount.setError(getResources().getString(R.string.amount_missing));
                    }
                } else { // category or item
                    if(!name.getText().toString().isEmpty()){
                        if(SourceConstants.IsCategory()){
                            if(db.isNew(name.getText().toString())){
                                Budget b =  new Budget();
                                b.isIncome = SourceConstants.isIncome();
                                b.category = name.getText().toString();
                                new ThreadFactory(new AddData()).execute(b);
                                dialTwo.cancel();
                            }
                            else{
                                name.setError(getResources().getString(R.string.replicate_cat));
                            }
                        }
                        else{ // item
                            if(db.isNew(name.getText().toString())){
                                if(!amount.getText().toString().isEmpty()){
                                    int myNum = 0;
                                    try {
                                        myNum = Integer.parseInt(amount.getText().toString());
                                        if(myNum<0) myNum = myNum * -1;
                                        Budget b =  new Budget();
                                        b.isIncome = SourceConstants.isIncome();
                                        b.category = SourceConstants.GetCategory();
                                        b.name = name.getText().toString();
                                        b.amount = myNum;
                                        new ThreadFactory(new AddData()).execute(b);
                                        dialTwo.cancel();
                                    } catch(NumberFormatException nfe) {
                                        amount.setError(getResources().getString(R.string.input));
                                    }
                                }
                                else{
                                    amount.setError(getResources().getString(R.string.amount_missing));
                                }
                            }
                            else{
                                name.setError(getResources().getString(R.string.replicate_item));
                            }
                        }
                    }else{
                        name.setError(getResources().getString(R.string.name_missing));
                    }
                }

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialTwo.cancel();
            }
        });

        dialTwo.show();
    }




    /**
     * Class that acts as a wrapper class to multithread the access to the database
     */
    private  class ThreadFactory extends AsyncTask<Budget, Void, Void>{

        //depending on the type of ExecuteQuery different CRUD operarions....
        ExecuteQuery query;
        ArrayList<String> temp;

        ThreadFactory(ExecuteQuery q){
            query = q;
        }

        @Override
        protected Void doInBackground(Budget... b) {
            SourceConstants.startUploading();
            temp = query.Execute(b[0]);
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... voids) {
            UpdateUIComponents();
        }

        @Override
        protected void onPostExecute(Void voids) {
            if(SourceConstants.isError()){
                SourceConstants.GoodBoy();
                Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error), Toast.LENGTH_SHORT);
                toast.show();
            }
            SourceConstants.copyArrayList(list, temp);
            adapter.notifyDataSetChanged();
            SourceConstants.DoneEdit();
            SourceConstants.doneUploading();
            UpdateUIComponents();
        }

    }

    /**
     * Retrives data from data base
     */
    private class RetrieveData implements ExecuteQuery {
        @Override
        public ArrayList<String> Execute(Budget b) {
            ArrayList<String> temp;
            if(SourceConstants.IsMain()){
                temp = db.getIncomeAndExpenses();

            }
            else if(SourceConstants.IsCategory()){
                temp=  db.getByIncome(b.getIsIncome());
            }
            else{
                temp = db.getByCategory(b.isIncome, b.category);
            }
            netIncome = db.getNet();
            return temp;
        }
    }

    /**
     *Adds data to the database
     */
    private class AddData implements ExecuteQuery {
        @Override
        public ArrayList<String> Execute(Budget b) {
            SourceConstants.setNotification();
            ArrayList<String> temp;
            if(SourceConstants.IsCategory()){
                temp = db.insertCategory(b.isIncome, b.category);
            }
            else{
                temp = db.insertItems(b.isIncome, b.category, b.name, b.amount);
            }
            netIncome = db.getNet();
            return temp;
        }
    }

    /**
     *deletes data from the database
     */
    private class DeleteData implements ExecuteQuery {
        @Override
        public ArrayList<String> Execute(Budget b) {
            SourceConstants.setNotification();
            ArrayList<String> temp;
            if(SourceConstants.IsMain()){
                temp= db.deteleByType(b.isIncome);
            }
            else if(SourceConstants.IsCategory()){
                temp = db.deleteCategory(b.isIncome, VIEW_POSITION_CLICK_);
            }
            else{
                temp = db.deleteItem(b.isIncome, b.category, VIEW_POSITION_CLICK_);
            }
            netIncome = db.getNet();
            return temp;
        }
    }

    /**
     * Updates data
     */
    private class UpdateData implements ExecuteQuery {
        @Override
        public ArrayList<String> Execute(Budget b) {
            SourceConstants.setNotification();
            ArrayList<String> temp;
            temp = db.updateItem(b.isIncome, b.category, b.name, b.amount);
            netIncome = db.getNet();
            return temp;
        }
    }

}

