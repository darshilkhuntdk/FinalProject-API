package com.cst2335.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Marthe Julie Dubuisson
 * @version 1.0
 * @since March 25th, 2021
 * This class shows the question on a phone or tablet
 */
public class QuestionsActivity extends AppCompatActivity implements IndividualQuestionFragment.OnItemSelectedListener {
    MyListAdapter myAdapter;
    String fileName = "myPrefs";
    SQLiteDatabase db;

    public static final String ITEM_QUESTION = "ITEM";
    public static final String ITEM_CORRECT_ANSWER = "ITEMTWO";
    public static final String ITEM_INCORRECT_ANSWERS = "ITEMTWlO";
    public static final String ITEM_POSITION = "POSITION";
    public static final String ITEM_ID = "ID";
    public static final String ITEM_TYPE = "TYPE";
    public static final String IS_TABLET = "IS_TABLET";
    int gameScore = 0;


    private ArrayList<TriviaQuestions> questionList = new ArrayList<>();
    private ArrayList<HighScore> highScoreList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_questions);

        loadDataFromDatabase();

        FrameLayout frame = findViewById(R.id.fragmentLocation);
        boolean isTablet = frame != null;

        Button restartScoreButton = findViewById(R.id.restartScoreButton);
        restartScoreButton.setOnClickListener(click -> {
            finish();
        });

        Intent intent = getIntent();
        String urlResult = intent.getStringExtra("urlResult");

        try {


            JSONObject resultsJSON = new JSONObject(urlResult);
            JSONArray resJsonArray = resultsJSON.getJSONArray("results");

            for (int i = 0; i < resJsonArray.length(); i++) {

                JSONObject resJsonObject = resJsonArray.getJSONObject(i);

                String type = resJsonObject.getString("type");
                String difficulty = resJsonObject.getString("difficulty");
                String question = resJsonObject.getString("question");
                String correctAnswer = resJsonObject.getString("correct_answer");
                JSONArray incorrectAnswersArray = resJsonObject.getJSONArray("incorrect_answers");

                ArrayList<String> incorrect_answers = new ArrayList<String>();

                for (int j = 0; j < incorrectAnswersArray.length(); j++) {
                    incorrect_answers.add(incorrectAnswersArray.getString(j));

                }


                TriviaQuestions tempQuestion = new TriviaQuestions(type, difficulty, question, correctAnswer, incorrect_answers, TriviaQuestions.UNANSWERED);
                questionList.add(tempQuestion);

            }
            Log.i("results json",
                    questionList.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }


        myAdapter = new MyListAdapter();
        AbsListView questionsListListView;

        if (isTablet) {
            questionsListListView = (ListView) findViewById(R.id.theListView);
        } else {
            questionsListListView = (GridView) findViewById(R.id.theListView);
        }
        ;
        questionsListListView.setAdapter(myAdapter);

        questionsListListView.setOnItemClickListener((list, view, position, id) -> {
            TriviaQuestions questions = questionList.get(position);
            if (questions.getStatus().equals(TriviaQuestions.UNANSWERED)) {
                Bundle dataToPass = new Bundle();
                dataToPass.putString(ITEM_QUESTION, questions.getQuestion());
                dataToPass.putStringArrayList(ITEM_INCORRECT_ANSWERS, questions.getIncorrect_answers());
                dataToPass.putString(ITEM_CORRECT_ANSWER, questions.getCorrect_answer());
                dataToPass.putString(ITEM_TYPE, questions.getType());
                dataToPass.putInt(ITEM_POSITION, position);
                dataToPass.putLong(ITEM_ID, id);
                dataToPass.putBoolean(IS_TABLET, isTablet);


                if (isTablet) {
                    IndividualQuestionFragment dFragment = new IndividualQuestionFragment(); //add a DetailFragment

                    dFragment.setArguments(dataToPass); //pass it a bundle for information
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentLocation, dFragment) //Add the fragment in FrameLayout
                            .commit(); //load the fragment. Calls onCreate() in DetailFragment
                } else {
                    Intent nextActivity = new Intent(QuestionsActivity.this, IndividualQuestionActivity.class);
                    nextActivity.putExtras(dataToPass); //send data to next activity
                    startActivityForResult(nextActivity, (int) id); //make the transition
                }

            }
        });

    }

    /**
     *
     * @param requestCode This is the Id of the question
     * @param resultCode The code that represent the right answer (500) or wrong (501)
     * To mark if the player's choice was right or wrong(Tablet)
     */
    @Override
    public void resultFromFragment(int requestCode, int resultCode) {
        TriviaQuestions questions = questionList.get(requestCode);
        if (resultCode == 500) {
            questions.setStatus(TriviaQuestions.RIGHT);
        } else if (resultCode == 501) {
            questions.setStatus(TriviaQuestions.WRONG);
        }
        questionList.set(requestCode, questions);
        myAdapter.notifyDataSetChanged();
        Log.i("code: ", String.valueOf(resultCode));
        Log.i("request code: ", String.valueOf(requestCode));

        int i = 0;
        gameScore = 0;
        while (i < questionList.size() && !questionList.get(i).getStatus().equals(TriviaQuestions.UNANSWERED)) {
            String status = questionList.get(i).getStatus();
            if (status.equals(TriviaQuestions.RIGHT)) {
                gameScore++;
            }
            if (i == questionList.size() - 1) {
                Log.i(" count: ", String.valueOf(gameScore));
                AlertDialog.Builder alertBuilder = createSaveAlertDialogBuilder(this);
                alertBuilder.show();
            }
            i++;
        }

    }

    /**
     *
     * @param requestCode This is the Id of the question
     * @param resultCode The code that represent the right answer (500) or wrong (501)
     * To mark if the player's choice was right or wrong(Phone)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        TriviaQuestions questions = questionList.get(requestCode);
        if (resultCode == 500) {
            questions.setStatus(TriviaQuestions.RIGHT);
        } else if (resultCode == 501) {
            questions.setStatus(TriviaQuestions.WRONG);
        }
        questionList.set(requestCode, questions);
        myAdapter.notifyDataSetChanged();
        Log.i("code: ", String.valueOf(resultCode));
        Log.i("request code: ", String.valueOf(requestCode));

        int i = 0;
        gameScore = 0;
        while (i < questionList.size() && !questionList.get(i).getStatus().equals(TriviaQuestions.UNANSWERED)) {
            String status = questionList.get(i).getStatus();
            if (status.equals(TriviaQuestions.RIGHT)) {
                gameScore++;
            }
            if (i == questionList.size() - 1) {
                Log.i(" count: ", String.valueOf(gameScore));
                AlertDialog.Builder alertBuilder = createSaveAlertDialogBuilder(this);
                alertBuilder.show();

            }
            i++;
        }


    }

    /**
     *
     * @param context The current context
     * @return  It is retrun an alert dialog that shows the player score
     */

    private AlertDialog.Builder createSaveAlertDialogBuilder(android.content.Context context) {
        String gameDifficulty = questionList.get(0).getDifficulty();
        AlertDialog.Builder scoreAlertBuilder = new AlertDialog.Builder(this);
        scoreAlertBuilder.setTitle(getResources().getString(R.string.scoreTitle));

        LayoutInflater inflater = getLayoutInflater();
        View alertView = inflater.inflate(R.layout.score_alert_layout, null, false);
        TextView scoreTextView = alertView.findViewById(R.id.scoreTextView);
        String yourScoreIs = getResources().getString(R.string.yourScoreIs);
        scoreTextView.setText(yourScoreIs + " " + String.valueOf(gameScore));



        EditText playerNameEditText = alertView.findViewById(R.id.playerNameEditText);

        SharedPreferences prefs = getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String userName = prefs.getString("Name: ", "");
        playerNameEditText.setText(userName);
        scoreAlertBuilder.setView(alertView);


        scoreAlertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String playerName = playerNameEditText.getText().toString();

                ContentValues newRowValues = new ContentValues();
                newRowValues.put(Trivia_Opener.COL_NAME, playerName);
                newRowValues.put(Trivia_Opener.COL_DIFFICULTY, gameDifficulty);
                newRowValues.put(Trivia_Opener.COL_SCORE, gameScore);

                long newId = db.insert(Trivia_Opener.TABLE_NAME, null, newRowValues);
                loadDataFromDatabase();


                SharedPreferences prefs = getSharedPreferences(fileName, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Name: ", playerNameEditText.getText().toString());
                edit.commit();

                AlertDialog.Builder highScoreAlertBuilder = new AlertDialog.Builder(context);
                highScoreAlertBuilder.setTitle(getResources().getString(R.string.highScore));

                HighScoreListAdapter highScoreListAdapter = new HighScoreListAdapter();
                LayoutInflater inflater = getLayoutInflater();
                View highScoreAlertView = inflater.inflate(R.layout.high_score_alert_layout, null, false);


                ListView highScoreListView = highScoreAlertView.findViewById(R.id.highScoreListView);
                highScoreListView.setAdapter(highScoreListAdapter);
                highScoreAlertBuilder.setView(highScoreAlertView);

                highScoreListView.setOnItemLongClickListener(

                        (parent, view, pos, id) -> {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                            alertDialogBuilder.setTitle(getResources().getString(R.string.deleteHighScore));
                            alertDialogBuilder.setMessage(getResources().getString(R.string.deleteHighScoreMsg));

                            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.delete),
                                    (click, arg) -> {

                                        deleteScore(highScoreList.get(pos));
                                        highScoreList.remove(pos);
                                        highScoreListAdapter.notifyDataSetChanged();
                                    });

                            alertDialogBuilder.setNegativeButton(getResources().getString(R.string.cancel),
                                    (click, arg) -> {
                                    });

                            alertDialogBuilder.create().show();

                            return true;
                        }
                );
                highScoreAlertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Button restartScoreButton = findViewById(R.id.restartScoreButton);
                        restartScoreButton.setVisibility(View.VISIBLE);
                    }
                });
                highScoreAlertBuilder.show();
            }

        });
        scoreAlertBuilder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return scoreAlertBuilder;
    }

    private void printCursor(Cursor c, int version) {
        Log.i("version", String.valueOf(version));
        Log.i("Column Count", String.valueOf(c.getColumnCount()));
        Log.i("Column names", Arrays.toString(c.getColumnNames()));
        Log.i("Rows Count", String.valueOf(c.getCount()));

    }

    /**
     * To load the high score from the database and create the high score list
     */
    private void loadDataFromDatabase() {
        Trivia_Opener dbOpener = new Trivia_Opener(this);
        db = dbOpener.getWritableDatabase();

        String[] columns = {Trivia_Opener.COL_ID, Trivia_Opener.COL_NAME, Trivia_Opener.COL_SCORE, Trivia_Opener.COL_DIFFICULTY};
        Cursor results = db.query(false, Trivia_Opener.TABLE_NAME, columns, null, null, null, null, Trivia_Opener.COL_SCORE + " DESC", null);
        printCursor(results, db.getVersion());


        int nameColIndex = results.getColumnIndex(Trivia_Opener.COL_NAME);
        int scoreColIndex = results.getColumnIndex(Trivia_Opener.COL_SCORE);
        int difficultyColIndex = results.getColumnIndex(Trivia_Opener.COL_DIFFICULTY);
        int idColIndex = results.getColumnIndex(Trivia_Opener.COL_ID);
        highScoreList.clear();
        while (results.moveToNext()) {
            String name = results.getString(nameColIndex);
            int score = results.getInt(scoreColIndex);
            String difficulty = results.getString(difficultyColIndex);
            long id = results.getLong(idColIndex);

            highScoreList.add(new HighScore(id, name, score, difficulty));

        }

    }

    protected void deleteScore(HighScore highScore) {
        db.delete(Trivia_Opener.TABLE_NAME, Trivia_Opener.COL_ID + "= ?", new String[]{Long.toString(highScore.getId())});
    }

    class MyListAdapter extends BaseAdapter {
        /**
         * @return returns the number of questions to display
         */
        @Override
        public int getCount() {
            return questionList.size();
        }

        /**
         * @param position the rows position
         * @return the question that corresponds to the number passed in
         */
        @Override
        public Object getItem(int position) {
            return questionList.get(position);
        }

        /**
         * @param position
         * @return returns the Database ID of the question
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        /**
         * This method specifies how each question row looks like
         */
        public View getView(int position, View old, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View currentRowView;
            TriviaQuestions triviaQuestions = (TriviaQuestions) getItem(position);
            currentRowView = inflater.inflate(R.layout.question_row_layout, parent, false);
            LinearLayout linearLayout = currentRowView.findViewById(R.id.gridRowLinearLayout);
            TextView tView = currentRowView.findViewById(R.id.questionNumberTextView);
            tView.setText(String.valueOf(position + 1));
            if (triviaQuestions.getStatus().equals(TriviaQuestions.UNANSWERED)) {
                linearLayout.setBackgroundColor(getResources().getColor(R.color.grey));
            } else if (triviaQuestions.getStatus().equals(TriviaQuestions.RIGHT)) {
                linearLayout.setBackgroundColor(getResources().getColor(R.color.green));
            } else {
                linearLayout.setBackgroundColor(getResources().getColor(R.color.red));
            }

            return currentRowView;
        }


    }

    class HighScoreListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return highScoreList.size();
        }

        @Override
        public HighScore getItem(int position) {
            return highScoreList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
        }

        @Override
        public View getView(int position, View old, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View currentRowView;
            HighScore highScore = (HighScore) getItem(position);


            currentRowView = inflater.inflate(R.layout.high_score_row_layout, parent, false);
            TextView playerTextView = currentRowView.findViewById(R.id.playerTextView);
            TextView scoreTextView = currentRowView.findViewById(R.id.gameScoreTextView);
            Log.i("test", scoreTextView.toString());
            TextView levelTextView = currentRowView.findViewById(R.id.levelTextView);
            playerTextView.setText(highScore.getName());
            scoreTextView.setText(String.valueOf(highScore.getScore()));
            String difficulty;


            if (highScore.getDifficulty().equals(TriviaQuestions.DIFFICULTY_EASY)) {
                difficulty = getResources().getString(R.string.easyQuestions);
            } else if (highScore.getDifficulty().equals(TriviaQuestions.DIFFICULTY_MEDIUM)) {
                difficulty = getResources().getString(R.string.mediumQuestions);
            } else {
                difficulty = getResources().getString(R.string.hardQuestions);
            }
            levelTextView.setText(difficulty);

            return currentRowView;
        }


    }

}

class HighScore {
    private long id;
    private String name;
    private int score;
    private String difficulty;

    HighScore(long id, String name, int score, String difficulty) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.difficulty = difficulty;
    }


    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public String getDifficulty() {
        return difficulty;
    }

    @Override
    public String toString() {
        return "[ name : " + name + ", Difficulty : " + difficulty + ", score : " + score +
                " ]\n";
    }

}


/**
 * This class has all the references needed to create a question
 */
class TriviaQuestions {
    private String type;
    private String difficulty;
    private String question;
    private String correct_answer;
    private ArrayList<String> incorrect_answers;
    private String status;
    public final static String DIFFICULTY_HARD = "hard";
    public final static String DIFFICULTY_EASY = "easy";
    public final static String DIFFICULTY_MEDIUM = "medium";
    public final static String UNANSWERED = "Unanswered";
    public final static String RIGHT = "Right";
    public final static String WRONG = "Wrong";

    /**
     * @param type              The type of questions: If it is multiple choice or true or false choice
     * @param difficulty        The difficulty of questions: If it is easy, medium or hard questions
     * @param question          A random question
     * @param correct_answer    The correct answer of the question
     * @param incorrect_answers The incorrect answers of the question
     * @param status
     */
    public TriviaQuestions(String type, String difficulty, String question, String correct_answer, ArrayList<String> incorrect_answers, String status) {
        this.type = type;
        this.difficulty = difficulty;
        this.question = question;
        this.correct_answer = correct_answer;
        this.incorrect_answers = incorrect_answers;
        this.status = status;
    }

    ;

    /**
     * @return the type of the question
     */
    public String getType() {
        return type;
    }

    /**
     * @param type Sets the argument type of the question and assigns it to this variable
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the type of the difficulty of the question
     */
    public String getDifficulty() {
        return difficulty;
    }

    /**
     * @param difficulty Sets the argument difficulty of the question and assigns it to this variable
     */
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * @return the question
     */
    public String getQuestion() {
        return question;
    }

    /**
     * @param question Sets the argument question and assigns it to this variable
     */
    public void setQuestion(String question) {
        this.question = question;
    }

    /**
     * @return the correct answer of the question
     */
    public String getCorrect_answer() {
        return correct_answer;
    }

    /**
     * @param correct_answer Sets the argument correct answer and assigns it to this variable
     */
    public void setCorrect_answer(String correct_answer) {
        this.correct_answer = correct_answer;
    }

    /**
     * @return the incorrect answers of the question
     */
    public ArrayList<String> getIncorrect_answers() {
        return incorrect_answers;
    }

    /**
     * @param incorrect_answers Sets the argument incorrect answers and assigns it to this variable
     */
    public void setIncorrect_answers(ArrayList<String> incorrect_answers) {
        this.incorrect_answers = incorrect_answers;
    }


    /**
     * @return the String representation of the Trivia questions class
     */

    @Override
    public String toString() {
        return "[ Type : " + type + ", Difficulty : " + difficulty + ", Questions : " + question +
                ", Correct Answers : " + correct_answer + ", Incorrect Answers : " + incorrect_answers + " ]\n";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}