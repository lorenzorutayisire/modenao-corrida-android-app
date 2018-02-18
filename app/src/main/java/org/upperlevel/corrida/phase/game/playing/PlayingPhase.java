package org.upperlevel.corrida.phase.game.playing;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.upperlevel.corrida.R;
import org.upperlevel.corrida.command.Command;
import org.upperlevel.corrida.phase.Phase;
import org.upperlevel.corrida.phase.game.Game;
import org.upperlevel.corrida.phase.game.Player;

import java.io.IOException;
import java.util.Arrays;

import lombok.Getter;

public class PlayingPhase implements Phase {
    public static final String TAG = "PlayingPhase";

    @Getter
    private Activity activity;

    @Getter
    private Game game;

    /**
     * The field that displays the theme.
     */
    @Getter
    private TextView themeValueField;

    /**
     * The field that displays the player questioned.
     */
    @Getter
    private TextView questionedPlayerField;

    // -------------------------- Suggestion
    @Getter
    private LinearLayout suggestLayout;

    @Getter
    private TextView suggestDescriptionField;

    @Getter
    private TextView suggestQuestionField;

    @Getter
    private TextView suggestAnswerField;

    // -------------------------- Rating
    @Getter
    private LinearLayout ratingLayout;

    @Getter
    private Button rate1;

    @Getter
    private Button rate2;

    @Getter
    private Button rate3;

    @Getter
    private Button rate4;

    @Getter
    private Button rate5;

    /**
     * A thread that manages dummy fields content.
     */
    @Getter
    private Populator populator;

    public PlayingPhase(Game game) {
        this.activity = game.getActivity();
        this.game = game;
    }

    /**
     * Called when the player rates.
     * It sends the "rate" command and shows a little popup.
     */
    public void onRate(Button button, int rate) {
        try {
            game.emit(new RateCommand(rate));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Toast.makeText(activity, "Hai votato: \"" + button.getText() + "\"", Toast.LENGTH_SHORT).show();
    }

    private void initFields() {
        Activity a = activity;
        themeValueField = a.findViewById(R.id.theme_value);
        questionedPlayerField = a.findViewById(R.id.player_value);

        // Suggestion
        suggestLayout = a.findViewById(R.id.suggest);
        suggestDescriptionField = a.findViewById(R.id.suggest_description);
        suggestQuestionField = a.findViewById(R.id.suggest_question);
        suggestAnswerField = a.findViewById(R.id.suggest_answer);

        // Rating
        ratingLayout = a.findViewById(R.id.rating);
        rate1 = a.findViewById(R.id.rate1);
        rate1.setOnClickListener(view -> onRate(rate1, 1));
        rate2 = a.findViewById(R.id.rate2);
        rate2.setOnClickListener(view -> onRate(rate2, 2));
        rate3 = a.findViewById(R.id.rate3);
        rate3.setOnClickListener(view -> onRate(rate3, 3));
        rate4 = a.findViewById(R.id.rate4);
        rate4.setOnClickListener(view -> onRate(rate4, 4));
        rate5 = a.findViewById(R.id.rate5);
        rate5.setOnClickListener(view -> onRate(rate5, 5));
    }

    @Override
    public void onStart() {
        populator = new Populator();
        populator.start();

        activity.setContentView(R.layout.activity_exercise);
        initFields();
    }

    @Override
    public void onStop() {
        if (populator != null && populator.isAlive()) {
            populator.interrupt();
        }
    }

    /**
     * Listens for exercise things and populates the activity fields.
     */
    public class Populator extends Thread {
        @Override
        public void run() {
            while (isAlive()) {
                String[] received;
                try {
                    received = Command.split(game.receive());
                } catch (IOException e) {
                    Toast.makeText(activity, "Errore di ricezione", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return;
                }
                switch (received[0]) {
                    // -----
                    case "exercise_start":
                        ExerciseStartCommand exerciseStartCmd = new ExerciseStartCommand().decode(received);
                        Player questioned = game.getPlayer(exerciseStartCmd.getQuestionedPlayerName());
                        activity.runOnUiThread(() -> {
                            // Sets theme and questioned player name
                            themeValueField.setText(exerciseStartCmd.getTheme());
                            questionedPlayerField.setText(exerciseStartCmd.getQuestionedPlayerName());

                            if (questioned.equals(game.getMe())) { // The questioned player is me
                                suggestLayout.setVisibility(View.INVISIBLE); // Hides suggestion
                                ratingLayout.setVisibility(View.INVISIBLE); // Hides rating
                                Log.i(TAG, "The exercise is for me! I can't see certain fields...");
                            } else {
                                suggestLayout.setVisibility(View.VISIBLE); // Shows suggestion
                                ratingLayout.setVisibility(View.VISIBLE); // shows rating
                                Log.i(TAG, "The exercise is not for me... I may vote then.");
                            }
                            Log.i(TAG, "Received 'exercise_start' packet. Fields updated.");
                        });
                        break;
                    // -----
                    case "suggest":
                        SuggestCommand suggestCmd = new SuggestCommand().decode(received);
                        activity.runOnUiThread(() -> {
                            suggestLayout.setVisibility(View.VISIBLE);

                            // Description (always present)
                            suggestDescriptionField.setText(suggestCmd.getDescription());
                            Log.i(TAG, "Creating field description.");

                            // Question
                            if (suggestCmd.hasQuestion()) {
                                suggestQuestionField.setVisibility(View.VISIBLE);
                                suggestQuestionField.setText(suggestCmd.getQuestion());
                                Log.i(TAG, "Question is present. Creating its field.");
                            } else {
                                suggestQuestionField.setVisibility(View.INVISIBLE);
                                Log.i(TAG, "Question is not present so invisible.");
                            }

                            // Answer
                            if (suggestCmd.hasAnswer()) {
                                suggestAnswerField.setVisibility(View.VISIBLE);
                                suggestAnswerField.setText(suggestCmd.getAnswer());
                                Log.i(TAG, "Answer is present. Creating its field.");
                            } else {
                                suggestAnswerField.setVisibility(View.INVISIBLE);
                                Log.i(TAG, "Answer is not present so invisible.");
                            }

                            Log.i(TAG, "Received 'suggest' packet. Fields updated.");
                        });
                        break;
                    // -----
                    case "update_points":
                        UpdatePointsCommand updatePointsCmd = new UpdatePointsCommand().decode(received);
                        activity.runOnUiThread(() -> { // on Ui thread just to be safe from concurrency
                            String playerName = updatePointsCmd.getPlayerName();
                            Player player = game.getPlayer(playerName);
                            if (player != null) {
                                player.updatePoints(updatePointsCmd.getNewPoints());
                                Log.i(TAG, "Points of the player \"" + player.getName() + "\" updated to: " + player.getPoints() + ".");
                            } else {
                                Log.e(TAG, "Cannot find player \"" + playerName + "\" that was searched by points update.");
                            }
                        });
                        break;
                    // -----
                    case "game_end":
                        activity.runOnUiThread(() -> activity.setContentView(R.layout.activity_game_end));
                        populator.interrupt();
                        Log.i(TAG, "GAME END! Interrupt PlayingPhase!");
                        break;
                    default:
                        Log.e(TAG, "Received an unhandled packet: " + received[0]);
                        break;
                }
            }
        }
    }
}
