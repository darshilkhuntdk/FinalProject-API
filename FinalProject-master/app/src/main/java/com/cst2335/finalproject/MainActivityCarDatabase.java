package com.cst2335.finalproject;
/*
*@author Harpal Choudhary
* @Version 1
* Mile Stone 1
* @Name Final Project
* the program ask the user to enter the company name get a list of models made by that company.
* and then the user can select the model and save the list seacrh the web for selected model
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
/**
 * MainActivityCarDatabase is my my main activity class, after the user clicks the button an MainActivity
 * this class will come in action
 * */
public class MainActivityCarDatabase extends AppCompatActivity {
    private MyCarAdapter myCarAdapter;
    ProgressBar progressBar;
    ListView myCarView;  // list to display the selected company model
    SharedPreferences prefs = null;
    AlertDialog.Builder alertDialogBuilder;
    CarObject carObj;
    public static final String COMPANY_SELECTED = "COMPANYNAME";
    public static final String MODEL_SELECTED = "MODELNAME";
    public static final String MODEL_ID = "MODELID";
    // will store the car make, model and model id.
    String searchCompany, selectedModelId, selectedModelName, selectedCompany;
    /*
    *list ArrayList carList will store the carObject in the list
    * */

    private ArrayList<CarObject> carList = new ArrayList<>();
    Toolbar cartBar;
/**
 * function onCreate( ) is the first function that is called in an application.
 * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * This line loads the layout on your screen. The R.layout.in this case cardatabase_activity
         * */
        //This gets the toolbar from the layout:

        setContentView(R.layout.cardatabase_activity);
        //This gets the toolbar from the layout:
        cartBar= (Toolbar)findViewById(R.id.cartoolbar);
        setSupportActionBar(cartBar);
        progressBar =  findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        myCarAdapter = new MyCarAdapter() ;
        myCarView = findViewById(R.id.theCarListView);
        boolean isTablet = findViewById(R.id.fragmentLocation) != null; //check if the FrameLayout is loaded
        Log.i("Tablet Test: ", String.valueOf(isTablet));
/**
 * the search field where use enters the company name and clicks the seacrh button to seacrh for the list of models the company name
 * */
        EditText searchEditText = findViewById(R.id.txtSearch);
        Button searchButton = findViewById(R.id.btSearch);
        searchEditText.setFocusable(true);
/**
 * Shared preferences
 * */
        prefs = getSharedPreferences("FileName", Context.MODE_PRIVATE);
        String savedString = prefs.getString("ReserveName", "Seacrh Make");
        EditText typeField = findViewById(R.id.txtSearch);
        typeField.setText(savedString);


//*******************
            searchButton.setOnClickListener(click -> {
            carList.clear();
            saveSharedPrefs(typeField.getText().toString());

/**
 * Creating an AsyncTask object which takes care of the thread synchronization issues.
 * */
            ModelSearch    req = new ModelSearch();
/**
 * stores the of search text in searchCompany and will pass it to make a url string
 * */
            searchCompany = searchEditText.getText().toString();
            selectedCompany=searchCompany;
/**
 * if the search field empty show the create an Alertdialog box and prompt the user to enter
 * car company name
 * if the search field is not empty create a url string to search
 * */
            if(searchCompany.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Car DataBase Model Search");
                builder.setMessage("Search Field Empty, please enter Car Company!")
                .setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) { } });
                AlertDialog alert = builder.create();
                alert.show();
                myCarAdapter.notifyDataSetChanged();
            }else {
                Log.i("Model", searchCompany);
                searchEditText.setText(""); // clears the searchText
/**
 * to start a thread execute function called which will get data from the url specified.
 * */
                req.execute("https://vpic.nhtsa.dot.gov/api/vehicles/GetModelsForMake/" + searchCompany + "?format=xml");  //Type 1

            }

        });
/**
 * for now i am displaying the data in the toast but in milestone 2 we will pass the data to another activity
 * */
  /*      myCarView.setOnItemClickListener((list, view, position, id) -> {
            carObj=carList.get(position);

            selectedModelName=carObj.getCarModel();
            Toast.makeText(getApplicationContext(),"Company: "+ selectedCompany+ "\nModel: " +selectedModelName
                            +"\nModel Id: " + carObj.getModelId() ,Toast.LENGTH_LONG).show();

        });
*/

        //**********************************************************
        myCarView.setOnItemClickListener((list, view, position, id) -> {
            /*
            * Create a bundle to pass data to the new fragment
            * */
            CarObject selectedCar =carList.get(position);
            Bundle dataToPass = new Bundle();

            dataToPass.putString(COMPANY_SELECTED, selectedCar.getcompanyName());
            dataToPass.putString(MODEL_SELECTED, selectedCar.getCarModel());
            dataToPass.putString(MODEL_ID, selectedCar.getModelId());
/*
* checks if it is a tablet load the fragment
* if it is a phone create an intent and go to the activity
* */
            if(isTablet)
            {
                Log.i("Test", "I am on Tablet");
                CarDatabaseDetailsFragment dFragment = new CarDatabaseDetailsFragment(); //add a DetailFragment
                dFragment.setArguments( dataToPass ); //pass it a bundle for information
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentLocation, dFragment) //Add the fragment in FrameLayout
                        .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
            }
            else //isPhone
            {
                Log.i("Test", "I am on Phone");
                Intent nextActivity = new Intent(MainActivityCarDatabase.this, CarDatabaseEmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }
        });

//************************************************************

    } // end of onCreate


 /*
 *will inflate the help menu on screem
 * */
 @Override
 public boolean onCreateOptionsMenu(Menu menu1) {
     // Inflate the menu items for use in the action bar
     MenuInflater inflater = getMenuInflater();
     inflater.inflate(R.menu.cardb_help_menu, menu1);
     return true;
 }
    /*
     *will respond to the help menu item clicked
     * will display help on how to use the app
     * and there is also the button to take you the saved sarches
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Look at your menu XML file. Put a case for every id in that file:
        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.gosavedsearch:
                Intent goTosavedList = new Intent(MainActivityCarDatabase.this, savedCarList.class);
                startActivity( goTosavedList );
                break;

            case R.id.helpdialog:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Car DataBase Help");
                builder.setMessage(getString(R.string.help_msg))
                        .setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { } });
                AlertDialog alert = builder.create();
                alert.show();
                break;
        }
            return true;
    }

 //**********************************************
/**
 * the method willsave the shared search
***/

private void saveSharedPrefs(String stringToSave) {
    SharedPreferences.Editor editor = prefs.edit();
    editor.putString("ReserveName", stringToSave);
    editor.commit();
}
    /** ListAdapter is an Interface that you must implement by writing these 4 public functions
     * */
    private class MyCarAdapter extends BaseAdapter {
    int index= 0;
/**
 * getCount() returns the number of items to display in the list
 * this function returns the size of the Array or ArrayList
 * */
        @Override
        public int getCount() {
            return carList.size();
        }
/**
 * return the object in the list that you want to display at row position in the list
 * */
        @Override
        public CarObject getItem(int position) {

            Log.i("Position", String.valueOf(position));
            return carList.get(position);
        }
/**
 * not implemented
 * This function is used to return the database ID of the element at the given index of position.
 * */
        @Override
        public long getItemId(int position) {
            return position; /// getItem(position).getId();
        }
/**
 * getView method specifies how each row looks in list.
 * */
        @Override
        public View getView(int i, View view, ViewGroup parent) {
            /** LayoutInflater is a class used to instantiate layout XML file into its corresponding view objects*/
            LayoutInflater inflater = getLayoutInflater();
            CarObject test =getItem(i);
            View theCarListView = inflater.inflate(R.layout.car_list_layout, parent, false);
// displaying the layout
           TextView makeView=theCarListView.findViewById(R.id.tvCarModel);
            makeView.setText(test.carModel);
 //           TextView companyView=theCarListView.findViewById(R.id.tvcarCompany);
 //           makeView.setText(test.getcompanyName());
           return theCarListView; // returns the created view
        }
    } // end of MyCarAdaptor class
/**
 * AsyncTask is an abstract class provided by Android which gives us the liberty to perform
 * heavy tasks in the background and keep the UI thread light thus making the application more responsive.
 * ModelSearch is a sub class which creates the object and calls the 3 method to perform the task*/
    private  class  ModelSearch extends AsyncTask<String, Integer, String  >
    {
        private String modelName;

 /**
  * this is where you connect to the internet read the xml data and call the other two method to update
  * the GUI and read the data.
  *
  * */
 @Override
        protected String doInBackground(String... args)
        {
            try {

            URL url = new URL(args[0]);
/**
 * open the connection HTTPUrlConnection class for connecting to a server
 * */
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            //wait for data: inputStream is the text response that was returned by the server.
            InputStream response = urlConnection.getInputStream();
/**
 * A Pull parser processes xml data
 * */
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(response, "UTF-8");

/**
 * while loop starts reading from begning. and moves the event type and then reads
 * and reads it till reaches the end of document.
 * */
            int eventType = xpp.getEventType(); //The parser is currently at START_DOCUMENT

            while(eventType != XmlPullParser.END_DOCUMENT)
            {
                if(eventType == XmlPullParser.START_TAG)
                {   publishProgress(25);
                    //If you get here, then you are pointing at a start tag
                   if(xpp.getName().equals("Model_ID"))
                   {
                        xpp.next(); //move the pointer from the opening tag to the TEXT event
                        selectedModelId=xpp.getText(); // this will get the text between the tags in this case model id
                       Log.i("Test", selectedModelId);
                       xpp.next(); //move the pointer from the opening tag to the TEXT event
                    }

                    if(xpp.getName().equals("Model_Name"))
                    {   publishProgress(50);
                        xpp.next(); //move the pointer from the opening tag to the TEXT event
                        modelName = xpp.getText(); // this will get the text between the tags in this case modelName
                        carObj = new CarObject(searchCompany,modelName, selectedModelId); // create and object
                        carList.add(carObj);  // add the object to the list
                        Log.i("Test", modelName);
                    }

                }
                eventType = xpp.next(); //move to the next xml event and store it in a variable
                publishProgress(75);
            }
        }  catch (Exception e)
            {
                e.printStackTrace();
            }

            publishProgress(100);
            return "done";
        } // end of doinBackground();
/**
 * this is where you update your gui
 * */
        public void onProgressUpdate(Integer ... args)
        {
           // Log.i("Progress", args[0].toString());
         //   progressBar.setVisibility(View.VISIBLE);
            if(args[0]>99) {
               progressBar.setVisibility(View.GONE);
                Log.i("Progress", args[0].toString());
            }else {
                progressBar.setVisibility(args[0]);
            }

        } // end of onProgressUpdate
/**
 * doInBackground has finished. and we are updating our final GUI.
 * */
        public void onPostExecute(String fromDoInBackground)
        {
            myCarView.setAdapter(myCarAdapter);
            myCarAdapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(),"All Models of " +searchCompany, Toast.LENGTH_SHORT).show();
            searchCompany=""; // resets the searchCompany

        } // end of PostExecute

    } // end of Forecast Class

/**
 * the carobject class will create a car object and store the make model and company name
 * */
    class CarObject {
/**
 * the class variables to store car object values
 */
        protected String companyName,carModel, modelId;
/**
 * Constructor call intialize the values
 * */
        public CarObject (String companyName, String carModel, String modelId)
        {
            this.companyName=companyName;
            this.carModel=carModel;
            this.modelId=modelId;
        }
/**
 * getters company Name
 * */
        public String getcompanyName()
        {
            return companyName;
        }
/**
 * getter for car model
 * */
        public String getCarModel()
        {
            return carModel;
        }
/**
 * getters model id
 * */
        public String getModelId()
        {
            return modelId;
        }
    }// end of car object class
} // end of the MainActivityCarDatabase class