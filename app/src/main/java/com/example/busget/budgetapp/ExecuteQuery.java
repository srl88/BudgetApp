package com.example.busget.budgetapp;


import java.util.ArrayList;

/**
 * Interface for data retrieval. This will allow multithreading from a single  class.
 */
public interface ExecuteQuery {

    ArrayList<String> Execute(Budget b);


}
