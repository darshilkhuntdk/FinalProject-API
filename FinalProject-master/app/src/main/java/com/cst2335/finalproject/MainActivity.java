package com.cst2335.finalproject;

import android.content.Intent;
import android.os.Bundle;
//import android.support.wearable.activity.WearableActivity;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

/**
 * @author Marthe Julie Dubuisson, Harpal Choudhary, Darshilkumar Khunt, Dhruvkumar Patel
 * @version 1.0
 * @since March 25th, 2021
 * This class gives access to each project through the navigation drawer and the toolbar
 * The projects are: Trivia API, Car Database, Songster Search Page, Soccer Games API
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //This gets the toolbar from the layout:
        Toolbar tBar = (Toolbar)findViewById(R.id.toolbar);

        //This loads the toolbar, which calls onCreateOptionsMenu below:
        setSupportActionBar(tBar);

        //For NavigationDrawer:
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        Log.i("inside drawer", drawer.toString());
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.drawer_navigation_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;

        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.triviaId:
                Intent goToTriviaActivity = new Intent(MainActivity.this, TriviaActivity.class);
                startActivity(goToTriviaActivity);
                break;
            case R.id.carId:
                Intent goToCarDatabaseActivity = new Intent(MainActivity.this, MainActivityCarDatabase.class);
                startActivity(goToCarDatabaseActivity );
                break;
            case R.id.songsterId:
                Intent goToSongsterActivity = new Intent(MainActivity.this, SongsterActivity.class);
                startActivity(goToSongsterActivity);
                break;
            case R.id.soccerId:
                Intent goToSoccerGame = new Intent(MainActivity.this, SoccerActivityMain.class);
                startActivity(goToSoccerGame);
                break;


        }

        return true;
    }

    @Override
    public boolean onNavigationItemSelected( MenuItem item) {

        String message = null;

        switch(item.getItemId())
        {
            case R.id.triviaId:
                Intent goToTriviaActivity = new Intent(MainActivity.this, TriviaActivity.class);
                startActivity(goToTriviaActivity);
                break;
            case R.id.carId:
                Intent goToCarDatabaseActivity = new Intent(MainActivity.this, MainActivityCarDatabase.class);
                startActivity(goToCarDatabaseActivity );
                break;
            case R.id.songsterId:
                Intent goToSongsterActivity = new Intent(MainActivity.this, SongsterActivity.class);
                startActivity(goToSongsterActivity);
                break;
            case R.id.soccerId:
                Intent goToSoccerGame = new Intent(MainActivity.this, SoccerActivityMain.class);
                startActivity(goToSoccerGame);
                break;

        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return false;

    }
}