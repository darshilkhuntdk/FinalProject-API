package com.cst2335.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CarDatabaseDetailsFragment extends Fragment {
/*
* this class creates the fragment and loads the data in the fragments
**/
/*
* @param dataFromActivity to store bundle data
* @param resut will display all the widgets for user interface
* @param db is a database variable and will get us access to database
* @param model, make and modelid will be use to store the car information
**/
    private Bundle dataFromActivity;
    private long id;
    private AppCompatActivity parentActivity;
    private  String make, model, modelId;
    View result;
    SQLiteDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

   /*
   *gets the bundle data
   */
        dataFromActivity = getArguments();
        make=dataFromActivity.getString(MainActivityCarDatabase.COMPANY_SELECTED);
        model=dataFromActivity.getString(MainActivityCarDatabase.MODEL_SELECTED);
        modelId=dataFromActivity.getString(MainActivityCarDatabase.MODEL_ID);
    /*
     *Inflate the layout for this fragment
     */     //
      result =  inflater.inflate(R.layout.cardb_fragment_details, container, false);

        //show the message
        TextView tvCompanyView = (TextView)result.findViewById(R.id.tvCompanyName);
        tvCompanyView.setText(getString(R.string.company_name) + dataFromActivity.getString(MainActivityCarDatabase.COMPANY_SELECTED));
        //       message.setText("dataFromActivity.getString(ChatRoomActivity.ITEM_POSITION)");

        TextView tvCompanyModel = (TextView)result.findViewById(R.id.tvCompanyModel);
        tvCompanyModel.setText(getString(R.string.company_model)+dataFromActivity.getString(MainActivityCarDatabase.MODEL_SELECTED));

        //show the id:
        TextView idView = (TextView)result.findViewById(R.id.tvmodelId);
        idView.setText(getString(R.string.company_model_id)+ dataFromActivity.getString(MainActivityCarDatabase.MODEL_ID));

// ************************************************************
// Shoping for selected model in the autotrader website
/*•	To shop for the car, launch an Autotrader.com search using the URL pattern:
    https://www.autotrader.ca/cars/?mdl=ZZZ&make=YYY&loc=K2G1V8, where ZZZ is the model,
   and YYY is the make, and K2G1V8 is Algonquin College’s postal code. For this project,
   we only want cars for sale near the college.
* */
        Button shopButton = (Button)result.findViewById(R.id.btShop);
        shopButton.setOnClickListener(click -> {
           String shopUrl="https://www.autotrader.ca/cars/?mdl="+model+"&"+make+"=&loc=K2G1V8";
           
           Intent shopPage = new Intent( Intent.ACTION_VIEW);
            shopPage.setData(Uri.parse(shopUrl));
            startActivity(shopPage);

        });

//************************************************************

/*
* on the click of this button it creates a new intent which launches a page
* Shoping for selected model in googles
* */

        Button searchButton = (Button)result.findViewById(R.id.btViewDetail);
       searchButton.setOnClickListener(clickMe -> {
            String searchUrl="http://www.google.com/search?q="+make+"+"+model;
            Intent searchPage = new Intent( Intent.ACTION_VIEW);
            searchPage.setData(Uri.parse(searchUrl));
            startActivity(searchPage);
        });

        Button viewSaveButton = (Button)result.findViewById(R.id.btViewSaveSearch);
        viewSaveButton.setOnClickListener(clickMe -> {

            Intent goToSavedActivity = new Intent(getActivity(),savedCarList.class);
        //    goToSavedActivity.putExtras(); //send data to next activity
                startActivity(goToSavedActivity); //make the transition

        });
//**********************************************************
    /* Saving seacrh to Database entry
    *
     */
        Button saveToDatavaeBt = (Button)result.findViewById(R.id.btSave);
        saveToDatavaeBt.setOnClickListener(clickMe -> {
            //get a database connection:
            CarDatabaseMyOpener dbOpener = new CarDatabaseMyOpener(getActivity());
            db = dbOpener.getWritableDatabase(); //T
            ContentValues newRowValues = new ContentValues();
            newRowValues.put(CarDatabaseMyOpener.CAR_MAKE, make);
            newRowValues.put(CarDatabaseMyOpener.CAR_MODEL, model);
            newRowValues.put(CarDatabaseMyOpener.MODEL_ID, modelId);
            long newId = db.insert(CarDatabaseMyOpener.TABLE_NAME, null, newRowValues);

        Toast.makeText(parentActivity.getApplicationContext(),getString(R.string.company_name)+ make + "\n"+getString(R.string.company_model) + model
                    +"\n"+getString(R.string.company_model_id) +  modelId + "\n Seacrh Saved!" ,Toast.LENGTH_LONG).show();

        });


        return result;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity)context;
    }

}