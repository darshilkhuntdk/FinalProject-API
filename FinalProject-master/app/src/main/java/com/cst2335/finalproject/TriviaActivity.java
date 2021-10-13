package com.cst2335.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;

/**
 * This class presents different kind of choices for the questions
 */
public class TriviaActivity extends AppCompatActivity {
    /**
     * Variables initialized by default
     * amount Quantity of questions
     * type Type of questions
     * level Level of questions
     */
    int amount = 5;
    String type = "boolean";
    String level = "easy";

    /**
     * This method retrieves the data from the buttons and execute the URL based on them
     *
     * @param savedInstanceState This is the reference to the bundle object passed into this method
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia);
        //This gets the toolbar from the layout:
        Toolbar tBar = (Toolbar) findViewById(R.id.toolbar);

        //This loads the toolbar, which calls onCreateOptionsMenu below:
        setSupportActionBar(tBar);


        /**
         * bar Sets the visibility to invisible on the OnCreate Method
         */
        ProgressBar bar = findViewById(R.id.progressBar);
        bar.setVisibility(View.INVISIBLE);

        /**
         * startButton The startButton variable is created to take an action on the button
         * questionNumbersChoice The questionNumbersChoice variable is created to take an action on the button
         * multipleButton The multipleButton variable is created to take an action on the button
         * booleanButton The booleanButton variable is created to take an action on the button
         * easyButton The easyButton variable is created to take an action on the button
         * mediumButton The mediumButton variable is created to take an action on the button
         * hardButton The hardButton variable is created to take an action on the button
         */
        Button startButton = findViewById(R.id.button12);
        EditText questionNumbersChoice = findViewById(R.id.editTextTextPersonName);
        Button multipleButton = findViewById(R.id.button6);
        Button booleanButton = findViewById(R.id.button7);
        Button easyButton = findViewById(R.id.button9);
        Button mediumButton = findViewById(R.id.button10);
        Button hardButton = findViewById(R.id.button11);


        easyButton.setBackgroundColor(getResources().getColor(android.R.color.holo_purple, getTheme()));
        mediumButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray, getTheme()));
        hardButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray, getTheme()));

        multipleButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray, getTheme()));
        booleanButton.setBackgroundColor(getResources().getColor(android.R.color.holo_purple, getTheme()));

        questionNumbersChoice.setText(String.valueOf(amount));
        /**5
         * This will link the listener to the start button.
         * When the startButton is clicked it will execute the URL if none of the field are empty
         */

        startButton.setOnClickListener(click -> {
            try {
                amount = Integer.parseInt(questionNumbersChoice.getText().toString());
                if (amount > 50) {
                    throw new Exception("");
                }
                String myUrl = "https://opentdb.com/api.php?amount=" + amount + "&type=" + type + "&difficulty=" + level;
                TriviaQuery req = new TriviaQuery();
                req.execute(myUrl);
            } catch (Exception e) {
                Toast.makeText(this, getResources().getString(R.string.toast_message), Toast.LENGTH_LONG).show();
            }

        });

        /**
         * This will link the listener to the Multiple button.
         * This button changes state from purple to gray to purple if it is clicked
         * This button retrieves its data be attributed to the URL if it is clicked
         */
        multipleButton.setOnClickListener(click -> {
            multipleButton.setBackgroundColor(getResources().getColor(android.R.color.holo_purple, getTheme()));
            booleanButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray, getTheme()));

            type = "multiple";
            Log.i("button multiple", type);

            String switchState = getResources().getString(R.string.switch_message);
            Snackbar.make(multipleButton, switchState, Snackbar.LENGTH_LONG).setAction("Undo", clk -> {
                booleanButton.setBackgroundColor(getResources().getColor(android.R.color.holo_purple, getTheme()));
                multipleButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray, getTheme()));
                type = "boolean";

            }).show();

        });

        /**
         * This will link the listener to the Boolean button.
         * This button changes state from gray to purple if it is clicked
         * This button retrieves its data to be attributed to the URL if it is clicked
         */
        booleanButton.setOnClickListener(click -> {
            multipleButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray, getTheme()));
            booleanButton.setBackgroundColor(getResources().getColor(android.R.color.holo_purple, getTheme()));
            type = "boolean";
            Log.i("boolean", type);

            String switchState = "You changed the type to True or False";
            Snackbar.make(booleanButton, switchState, Snackbar.LENGTH_LONG).setAction("Undo", clk -> {
                multipleButton.setBackgroundColor(getResources().getColor(android.R.color.holo_purple, getTheme()));
                booleanButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray, getTheme()));
                type = "multiple";

            }).show();
        });

        /**
         * The value retrieved from the easy button is to be sent to the URL
         */
        easyButton.setOnClickListener(click -> {
            level = "easy";
            easyButton.setBackgroundColor(getResources().getColor(android.R.color.holo_purple, getTheme()));
            mediumButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray, getTheme()));
            hardButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray, getTheme()));
            Log.i("level easy", level);
        });

        /**
         * The value retrieved from the medium button is to be sent to the URL
         */
        mediumButton.setOnClickListener(click -> {
            level = "medium";
            easyButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray, getTheme()));
            mediumButton.setBackgroundColor(getResources().getColor(android.R.color.holo_purple, getTheme()));
            hardButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray, getTheme()));
            Log.i("level medium", level);
        });

        /**
         * The value retrieved from the hard button is to be sent to the URL
         */
        hardButton.setOnClickListener(click -> {
            level = "hard";
            easyButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray, getTheme()));
            mediumButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray, getTheme()));
            hardButton.setBackgroundColor(getResources().getColor(android.R.color.holo_purple, getTheme()));
            Log.i("level hard", level);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;

        switch (item.getItemId()) {

            case R.id.help_item:
                message = getResources().getString(R.string.about);
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle(getResources().getString(R.string.instructionsTitle));
                alertBuilder.setMessage(getResources().getString(R.string.instructions));
                alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertBuilder.show();
                break;

        }

        return true;
    }

    /**
     * This class allows  background operations on the UI thread
     */
    private class TriviaQuery extends AsyncTask<String, Integer, String> {

        /**
         * This method access the network and returns the result of the trivia API
         */
        String urlResult;

        public String doInBackground(String... args) {

            try {

                URL url = new URL(args[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                publishProgress(25);
                InputStream response = urlConnection.getInputStream();
                publishProgress(50);


                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();


                String line = null;
                publishProgress(75);
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                publishProgress(100);
                urlResult = sb.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return "Done";

        }

        /**
         * This method is to indicate the progress of the data access
         *
         * @param args represent the URL
         */
        public void onProgressUpdate(Integer... args) {
            ProgressBar bar = findViewById(R.id.progressBar);
            bar.setVisibility(View.VISIBLE);
            bar.setProgress(args[0]);
        }

        /**
         * This method goes to another activity with the new URL generated by the previos buttons
         *
         * @param fromDoInBackground the object that was returned by doInBackground
         */
        public void onPostExecute(String fromDoInBackground) {
            Intent goToQuestionsActivity = new Intent(TriviaActivity.this, QuestionsActivity.class);
            goToQuestionsActivity.putExtra("urlResult", urlResult);
            startActivity(goToQuestionsActivity);

            ProgressBar bar = findViewById(R.id.progressBar);
            bar.setVisibility(View.INVISIBLE);

            Log.i("HTTP", fromDoInBackground);
        }
    }
}