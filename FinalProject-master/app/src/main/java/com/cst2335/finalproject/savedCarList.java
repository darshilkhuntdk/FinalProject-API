package com.cst2335.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
/*
 * this class list all the searches save them in the database when user clicks the saved search
 * button this class put the data in the database and list them on screen for view and delete
 * */
public class savedCarList extends AppCompatActivity {
    private ArrayList<SavedSeacrh> savedCarList = new ArrayList<>();
    ListView mySavedCarView;
    private MySavedCarAdapter mySavedCarAdapter;
    /*
    * connection to database dbRead
    * */
    SQLiteDatabase dbRead;
    /*
     * variable to for the selected row
     * */
    private boolean isSelected =false;
    private long delId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_car_list);
    /*
     * loads the table row of saved searches from the database to list
     * */
        loadDataFromDatabase();
        mySavedCarAdapter = new MySavedCarAdapter() ;
        mySavedCarView = findViewById(R.id.theSaveCarListView);
        mySavedCarView.setAdapter((mySavedCarAdapter));
        mySavedCarAdapter.notifyDataSetChanged();
    /*
     *when the user selects the list it sets the flag for delete and highligts the row
     * */
        mySavedCarView.setOnItemClickListener((list , view, position, id) -> {
            SavedSeacrh saveObj= savedCarList.get(position);
            isSelected=true;
            delId=saveObj.getId();
            mySavedCarAdapter.notifyDataSetChanged();
        });
    /*
     * when the users clicks the delete button, it sets checks for the row selected and deletes the row
     * */
        Button delButton = findViewById(R.id.btDelete);
        delButton.setOnClickListener(toDel-> {
            deleteContact(delId,isSelected);
            mySavedCarAdapter.notifyDataSetChanged();
        });
        mySavedCarAdapter.notifyDataSetChanged();
    }
    /*
     * loads the database in the list
     * */
    private void loadDataFromDatabase()
    {
        //get a database connection:
        CarDatabaseMyOpener dbOpener = new CarDatabaseMyOpener(this);
        dbRead = dbOpener.getWritableDatabase(); //This calls onCreate()
        // We want to get all of the columns. Look at MyOpener.java for the definitions:
        String [] columns = {CarDatabaseMyOpener.COL_ID, CarDatabaseMyOpener.CAR_MAKE,  CarDatabaseMyOpener.CAR_MODEL, CarDatabaseMyOpener.MODEL_ID};
        //query all the results from the database:
        Cursor results = dbRead.query(false, CarDatabaseMyOpener.TABLE_NAME, columns, null, null, null, null, null, null);

        /*
         * Now the results object has rows of results that match the query.
        *  find the column indices:
         * */

        int makeColumnIndex = results.getColumnIndex(CarDatabaseMyOpener.CAR_MAKE);
        int modelColumnIndex = results.getColumnIndex(CarDatabaseMyOpener.CAR_MODEL);
        int modelidColIndex = results.getColumnIndex(CarDatabaseMyOpener.MODEL_ID);
        int idColIndex = results.getColumnIndex(CarDatabaseMyOpener.COL_ID);

        /*
         * iterate over the results, return true if there is a next item:
         * */
        while(results.moveToNext())
        {
            String cmake = results.getString(makeColumnIndex);
            String cmodel = results.getString(modelColumnIndex);
            String cmodelid = results.getString(modelidColIndex);
            long id = results.getLong(idColIndex);

            //add the new Contact to the array list:
            savedCarList.add(new SavedSeacrh(cmake,cmodel,cmodelid,id));
        }

        //At this point, the contactsList array has loaded every row from the cursor.


    }
    /*
    * this methods checks if the row is selected and deletes the row and notifys the listview adaptar
    * */
    protected void deleteContact(long drow, boolean deleteTrue)
    {
       if(deleteTrue){
           dbRead.delete(CarDatabaseMyOpener.TABLE_NAME, CarDatabaseMyOpener.COL_ID + "= ?", new String[] {Long.toString(drow)});
           mySavedCarAdapter.notifyDataSetChanged();
          savedCarList.clear();
           loadDataFromDatabase();
           Toast.makeText(getApplicationContext(),"Record Deleted" ,Toast.LENGTH_LONG).show();
           isSelected=false;
           delId=0;
       } else{
           Toast.makeText(getApplicationContext(),"Please Select from List",Toast.LENGTH_LONG).show();
       }
        mySavedCarAdapter.notifyDataSetChanged();
    }


    /** ListAdapter is an Interface that you must implement by writing these 4 public functions
     * */
    private class MySavedCarAdapter extends BaseAdapter {

        /**
         * getCount() returns the number of items to display in the list
         * this function returns the size of the Array or ArrayList
         * */
        @Override
        public int getCount() {
            return savedCarList.size();
        }
        /**
         * return the object in the list that you want to display at row position in the list
         * */
        @Override
        public SavedSeacrh getItem(int position) {

            Log.i("PPPPP", "position");
            return savedCarList.get(position);
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
            SavedSeacrh test =getItem(i);
            View theSavedCarListView = inflater.inflate(R.layout.saved_car_layout, parent, false);
 // displaying the layout
            TextView modelView=theSavedCarListView.findViewById(R.id.tvSavedCarModel);
            modelView.setText(test.getSavedMake() +" "+test.getSavedModel()+ " Model ID: "+test.getSavedModelId() );
   //         TextView modelView1=theSavedCarListView.findViewById(R.id.tvSavedcarCompany);
     //       modelView1.setText(" "+test.savedModel);

            return theSavedCarListView; // returns the created view
        }
    } // end of MySaveCarAdaptor class


  /*
  * this is inner class it creates a an object in which the row data is saved
  * */
    class SavedSeacrh {
    /*
    * instance variable to store the saved search
    * **/
        protected String savedModel,savedMake, savedModelId;
        protected long id;
  /*
   *constructor call and assigns the value
   * **/
             public SavedSeacrh (String savedMake, String savedModel, String savedModelId, long id)
        {
           this.savedMake=savedMake;
           this.savedModel=savedModel;
           this.savedModelId=savedModelId;
           this.id=id;
        }
  /*
   * getters to retrieve the value from instance variable
   * **/
        public String getSavedMake()
        {
            return savedMake;
        }
        public String getSavedModel()
        {
            return savedModel;
        }
        public String getSavedModelId()
        {
            return savedModelId;
        }
        public long getId()
        {
            return this.id;
        }
    }
}