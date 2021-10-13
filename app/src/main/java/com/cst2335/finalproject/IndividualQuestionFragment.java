package com.cst2335.finalproject;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
/**
 * @author Marthe Julie Dubuisson
 * @version 1.0
 * @since March 25th, 2021
 * This fragment is to show each question and the answers
 */
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IndividualQuestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IndividualQuestionFragment extends Fragment {

    private Bundle dataFromActivity;
    private long id;
    private AppCompatActivity parentActivity;
    private ArrayList<String> incorrectAnswers;
    private String correctAnswer;
    private String typeAnswer;
    private Boolean isTablet;
    private boolean isCorrectAnswer = false;
    int code = 200;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public IndividualQuestionFragment() {
        // Required empty public constructor
    }

    private OnItemSelectedListener listener;

    // Define the events that the fragment will use to communicate
    public interface OnItemSelectedListener {
        // This can be any number of events to be sent to the activity
        public void resultFromFragment(int requestCode, int resultCode);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IndividualQuestionFragment newInstance(String param1, String param2) {
        IndividualQuestionFragment fragment = new IndividualQuestionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity) context;

        if (context instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataFromActivity = getArguments();
        id = dataFromActivity.getLong(QuestionsActivity.ITEM_ID);

        incorrectAnswers = dataFromActivity.getStringArrayList(QuestionsActivity.ITEM_INCORRECT_ANSWERS);
        correctAnswer = dataFromActivity.getString(QuestionsActivity.ITEM_CORRECT_ANSWER);
        typeAnswer = dataFromActivity.getString(QuestionsActivity.ITEM_TYPE);
        isTablet = dataFromActivity.getBoolean(QuestionsActivity.IS_TABLET);

        ArrayList<String> answerOptions = new ArrayList<>();
        answerOptions.addAll(incorrectAnswers);
        answerOptions.add(correctAnswer);

        Collections.shuffle(answerOptions);


        // Inflate the layout for this fragment
        View fragmentLayout = inflater.inflate(R.layout.fragment_individual_question, container, false);

        //show the question
        TextView questionToShow = (TextView) fragmentLayout.findViewById(R.id.multipleQuestionTextView);
        questionToShow.setText(Html.fromHtml(dataFromActivity.getString(QuestionsActivity.ITEM_QUESTION)));

        //show the answers:
        ArrayList<Button> answerButtons = new ArrayList<>();

        Button firstButton = (Button) fragmentLayout.findViewById(R.id.button2);
        Button secondButton = (Button) fragmentLayout.findViewById(R.id.button3);
        Button thirdButton = (Button) fragmentLayout.findViewById(R.id.button4);
        Button fourthButton = (Button) fragmentLayout.findViewById(R.id.button5);
        ImageButton continueButton = (ImageButton) fragmentLayout.findViewById(R.id.buttonContinue);

        //To go back when the user chooses an answer
        continueButton.setOnClickListener(click -> {
            if (isTablet) {
                parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
                listener.resultFromFragment((int) id, code);
            } else {
                parentActivity.setResult(code);
                parentActivity.finish();
            }
        });

        //To show the answers button based on the type of questions
        if (typeAnswer.equals("multiple")) {

            firstButton.setVisibility(View.VISIBLE);
            secondButton.setVisibility(View.VISIBLE);
            thirdButton.setVisibility(View.VISIBLE);
            fourthButton.setVisibility(View.VISIBLE);

            answerButtons.add(firstButton);
            answerButtons.add(secondButton);
            answerButtons.add(thirdButton);
            answerButtons.add(fourthButton);
        } else {

            firstButton.setVisibility(View.VISIBLE);
            secondButton.setVisibility(View.VISIBLE);

            answerButtons.add(firstButton);
            answerButtons.add(secondButton);
        }

        //If the player chooses the correct answer
        for (int i = 0; i < answerButtons.size(); i++) {
            answerButtons.get(i).setText(Html.fromHtml(answerOptions.get(i)));

            answerButtons.get(i).setOnClickListener(click -> {
                Button currentButton = (Button) click;
                if (currentButton.getText().equals(Html.fromHtml(correctAnswer))) {
                    isCorrectAnswer = true;
                    code = 500;
                    currentButton.setBackgroundColor(Color.GREEN);

                } else {
                    currentButton.setBackgroundColor(Color.RED);
                    code = 501;
                    int correctAnswerPosition = answerOptions.indexOf(correctAnswer);


                    answerButtons.get(correctAnswerPosition).setBackgroundColor(Color.GREEN);

                }
                for (int j = 0; j < answerButtons.size(); j++) {
                    answerButtons.get(j).setEnabled(false);
                }


            });

        }


        return fragmentLayout;
    }
}