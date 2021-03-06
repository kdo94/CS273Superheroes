package edu.orangecoastcollege.cs273.kdo94.cs273superheroes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static edu.orangecoastcollege.cs273.kdo94.cs273superheroes.QuizActivity.QUIZ_TYPE;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuizActivityFragment extends Fragment {

    private static final String TAG = "SuperheroQuiz Activity";

    private static final  int SUPERHEROES_IN_QUIZ = 10;

    private List<String>  fileNameList; // Superhero file names
    private List<String> quizSuperheroesList; // Superheroes in current quiz
    private String correctAnswer; // Correct Answer for the current superhero
    private String quizType;
    private int totalGuesses; // Number of guesses made
    private int correctAnswers; //Number of correct guesses
    private SecureRandom random; // Used to randomize the quiz
    private Handler handler; // Used to delay loading next flag

    private TextView questionNumberTextView; // Shows current question #
    private ImageView superheroImageView; // Displays a superhero
    private LinearLayout[] guessLinearLayout; // Rows of answer Buttons
    private TextView answerTextView; // Displays correct answer
    private TextView guessSuperheroTextView; // Displays the correct question according to the quiz type
    private ArrayList<Superheroes> allSuperheroes;

    public QuizActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        fileNameList = new ArrayList<>();
        quizSuperheroesList = new ArrayList<>();
        random = new SecureRandom();
        handler = new Handler();

        // Get reference to GUI components
        questionNumberTextView = (TextView) view.findViewById(R.id.questionNumberTextView);
        superheroImageView = (ImageView) view.findViewById(R.id.superheroImageView);
        guessLinearLayout = new LinearLayout[2];
        guessLinearLayout[0] = (LinearLayout) view.findViewById(R.id.row1LinearLayout);
        guessLinearLayout[1] = (LinearLayout) view.findViewById(R.id.row2LinearLayout);
        answerTextView = (TextView) view.findViewById(R.id.answerTextView);
        guessSuperheroTextView = (TextView) view.findViewById(R.id.guessSuperheroTextView);

        // Configure listeners for the guess Buttons
        for (LinearLayout row : guessLinearLayout){
            for(int column = 0; column < row.getChildCount(); column++){
                Button button = (Button) row.getChildAt(column);
                button.setOnClickListener(guessButtonListener);
            }
        }

        // Set questionNumberTextView's Text
        questionNumberTextView.setText(
                getString(R.string.question, 1, SUPERHEROES_IN_QUIZ));

        try{
            allSuperheroes = JSONLoader.loadJSONFromAsset(getContext());
        }
        catch(IOException ex){
            Log.e("Superhero", "Error loading JSON data");
        }

        return view;
    }

    public void updateQuizType(SharedPreferences sharedPreferences){
        quizType = sharedPreferences.getString(QUIZ_TYPE, null);
    }

    public void resetQuiz(){
        // Use AssetManager to get image file name for quiz type
        AssetManager assets = getActivity().getAssets();
        fileNameList.clear();

        // Add Superheroes to the fileNameList
//        try{
//            for (String type : quizType){
//                String[] paths = assets.list(type);
//
//                for (String path : paths)
//                    fileNameList.add(path.replace(".png", ""));
//            }
//        }
//        catch(IOException exception){
//            Log.e(TAG, "Error loading image file names", exception);
//        }
        for (Superheroes hero : allSuperheroes)
            fileNameList.add(hero.getUsername());
        correctAnswers = 0;
        totalGuesses = 0;
        quizSuperheroesList.clear();

        int superheroCounter = 1;
        int numberOfSuperheroes = fileNameList.size();

        // Add SUPERHEROES_IN_QUIZ random file name to the quizSuperheroesList
        while(superheroCounter <= SUPERHEROES_IN_QUIZ){
            int randomIndex = random.nextInt(numberOfSuperheroes);

            // get the random file name
            String filename = fileNameList.get(randomIndex);

            // If the quiz is enabled and it hasn't been chosen already
            if(!quizSuperheroesList.contains(filename)){
                quizSuperheroesList.add(filename); // Add file to the list
                superheroCounter++;
            }
        }

        loadNextSuperhero();
    }

    private void loadNextSuperhero(){
        // Get file name of the next superhero and remove it from list
        String nextImage = quizSuperheroesList.remove(0);
        correctAnswer = nextImage;
        answerTextView.setText("");

        // Display current question number
        questionNumberTextView.setText(getString(
                R.string.question, (correctAnswers + 1), SUPERHEROES_IN_QUIZ));

        // Use assetManager to load next image from assets folder
        AssetManager assets = getActivity().getAssets();

        // Get an InputStream to the asset representing the next superhero
        // and try to use the InputStream
        try (InputStream stream = assets.open(nextImage + ".png")){
            // Load the asset as a Drawable and display on the superheroImageView
            Drawable hero = Drawable.createFromStream(stream, nextImage);
            superheroImageView.setImageDrawable(hero);
        }
        catch(IOException exception){
            Log.e(TAG, "Error loading " + nextImage, exception);
        }

        Collections.shuffle(fileNameList); // shuffle file names

        // put the correct answer at the end of the fileNameList
        int correct = fileNameList.indexOf(correctAnswer);
        fileNameList.add(fileNameList.remove(correct));

        // Add, 2, 4, 6, or 8 guess Buttons based on the value of guessRows
        for(int row = 0; row < 2; row++){
            // Place Buttons in currentTableRow
            for(int column = 0;
                    column < guessLinearLayout[row].getChildCount();
                    column++){
                // Get reference to Button to configure
                Button newGuessButton =
                        (Button) guessLinearLayout[row].getChildAt(column);
                newGuessButton.setEnabled(true);

                // Get country name and set it as the newGuessButton's text
                String filename = fileNameList.get((row * 2) + column);
                newGuessButton.setText(changeButtons(filename));
            }
        }

        // Randomly replace one Button with the correct answer
        int row = random.nextInt(2);
        int column = random.nextInt(2);
        LinearLayout randomRow = guessLinearLayout[row]; // get the row
        String superheroName = changeButtons(correctAnswer);
        ((Button) randomRow.getChildAt(column)).setText(superheroName);
    }

    private View.OnClickListener guessButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button guessButton = ((Button) v);
            String guess = guessButton.getText().toString();
            String answer = changeButtons(correctAnswer);
            totalGuesses++; // Increment number of guesses the user had made

            if(guess.equals(answer)) { // if the guess is correct
                correctAnswers++; // Increment the number of correct answers

                // Display correct answer in green text
                answerTextView.setText(answer + "!");
                answerTextView.setTextColor(
                        getResources().getColor(R.color.correct_answer,
                                getContext().getTheme()));

                disableButtons(); // Disable all guess Buttons

                // If the user has correctly identified SUPERHEROES_IN_QUIZ superhero
                if(correctAnswers == SUPERHEROES_IN_QUIZ){
                    // DialogFragment to display quiz stats and start a new quiz
                    DialogFragment quizResults =
                            new DialogFragment(){
                                // Create an AlertDialog and return it
                                @Override
                                public Dialog onCreateDialog(Bundle bundle){
                                    AlertDialog.Builder builder =
                                            new AlertDialog.Builder(getActivity());
                                    builder.setMessage(
                                            getString(R.string.results,
                                            totalGuesses,
                                            (1000 / (double) totalGuesses)));

                                    // "Reset Quiz" Button
                                    builder.setPositiveButton(R.string.reset_quiz,
                                            new DialogInterface.OnClickListener(){
                                                public void onClick(DialogInterface dialog,
                                                                    int id){
                                                    resetQuiz();
                                                }
                                            }
                                    );

                                    return builder.create();
                                }
                            };
                    // Use FragmentManager to display the DialogFragment
                    quizResults.setCancelable(false);
                    quizResults.show(getFragmentManager(), "quiz results");
                }
                else { // Answer is correct but the quiz is not over
                    // Load the next flag after a 2-second delay
                    handler.postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    loadNextSuperhero();
                                }
                            }, 2000); // 2 second delay
                    }
                }
            else{ // Answer was incorrect
                // Display "Incorrect!" in red
                answerTextView.setText(R.string.incorrect_answer);
                answerTextView.setTextColor(getResources().getColor(
                        R.color.incorrect_answer, getContext().getTheme()));
                guessButton.setEnabled(false); // Disable incorrect answer
            }
        }
    };

    private String getSuperheroName(String name){
        for (Superheroes hero : allSuperheroes)
            if (name.equals(hero.getUsername()))
                return hero.getName();
        return "";
    }

    private void disableButtons(){
        for(int row = 0; row < 2;row++){
            LinearLayout guessRow = guessLinearLayout[row];
            for(int i = 0; i < guessRow.getChildCount(); i++){
                guessRow.getChildAt(i).setEnabled(false);
            }
        }
    }

    private String changeButtons(String name){
        for (Superheroes hero : allSuperheroes)
            if (name.equals(hero.getUsername())){
                switch (quizType){
                    case "Superpower":
                        guessSuperheroTextView.setText(R.string.guess_superpower);
                        return hero.getSuperpower();
                    case "One Thing":
                        guessSuperheroTextView.setText(R.string.guess_one_thing);
                        return hero.getOneThing();
                    default:
                        guessSuperheroTextView.setText(R.string.guess_superhero);
                        return hero.getName();
                }
            }
        return "";
    }

}
