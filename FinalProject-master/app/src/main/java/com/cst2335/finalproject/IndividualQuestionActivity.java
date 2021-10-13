package com.cst2335.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
/**
 * @author Marthe Julie Dubuisson
 * @version 1.0
 * @since March 25th, 2021
 * This activity is to show the fragment on a phone.
 */
public class IndividualQuestionActivity extends AppCompatActivity implements IndividualQuestionFragment.OnItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_question);

        Bundle dataToPass = getIntent().getExtras();
        IndividualQuestionFragment dFragment = new IndividualQuestionFragment();
        dFragment.setArguments( dataToPass );
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentLocation, dFragment)
                .commit();

    }
    @Override
    public void resultFromFragment(int requestCode, int resultCode) {


    }
}