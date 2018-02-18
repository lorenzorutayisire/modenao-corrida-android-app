package org.upperlevel.corrida.phase.game.playing;

import android.app.Activity;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.upperlevel.corrida.R;
import org.upperlevel.corrida.command.Command;
import org.upperlevel.corrida.phase.Phase;
import org.upperlevel.corrida.phase.game.GamePhase;

import java.io.IOException;

import lombok.Getter;

public class PlayingPhase implements Phase {
    public static final String TAG = "PlayingPhase";

    @Getter
    private Activity activity;

    @Getter
    private GamePhase game;

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

    /**
     * The container of the suggestion fields.
     */
    @Getter
    private LinearLayout suggestLayout;

    @Getter
    private TextView suggestDescriptionField;

    @Getter
    private TextView suggestQuestionField;

    @Getter
    private TextView suggestAnswerField;

    /**
     * A thread that manages dummy fields content.
     */
    @Getter
    private Populator populator;

    public PlayingPhase(GamePhase game) {
        this.activity = game.getActivity();
        this.game = game;
    }

    private void initFields() {
        Activity a = activity;
        themeValueField = a.findViewById(R.id.theme_value);
        questionedPlayerField = a.findViewById(R.id.player_value);

        suggestLayout = a.findViewById(R.id.suggest);
        suggestDescriptionField = a.findViewById(R.id.suggest_description);
        suggestQuestionField = a.findViewById(R.id.suggest_question);
        suggestAnswerField = a.findViewById(R.id.suggest_answer);
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
                    String raw = game.receive();
                    Log.i(TAG, "Really raw thing I received: " + raw);
                    received = Command.split(raw);
                } catch (IOException e) {
                    Toast.makeText(activity, "Errore di ricezione", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return;
                }
                switch (received[0]) {
                    case "exercise_start":
                        ExerciseStartCommand exerciseStartCmd = new ExerciseStartCommand().decode(received);
                        activity.runOnUiThread(() -> {
                            themeValueField.setText(exerciseStartCmd.getTheme());
                            questionedPlayerField.setText(exerciseStartCmd.getQuestionedPlayerName());
                            Log.i(TAG, "Received 'exercise_start' packet. Fields updated.");
                        });
                        break;
                    case "suggest":
                        SuggestCommand suggestCmd = new SuggestCommand().decode(received);
                        activity.runOnUiThread(() -> {
                            suggestLayout.setVisibility(View.VISIBLE);

                            suggestDescriptionField.setText(suggestCmd.getDescription());
                            Log.i(TAG, "Creating field description.");

                            if (suggestCmd.hasQuestion()) {
                                suggestQuestionField.setVisibility(View.INVISIBLE);
                                suggestQuestionField.setText(suggestCmd.getQuestion());
                                Log.i(TAG, "Question is present. Creating its field.");
                            }
                            if (suggestCmd.hasAnswer()) {
                                suggestAnswerField.setVisibility(View.INVISIBLE);
                                suggestAnswerField.setText(suggestCmd.getAnswer());
                                Log.i(TAG, "Answer is present. Creating its field.");
                            }

                            Log.i(TAG, "Received 'suggest' packet. Fields updated.");
                        });
                        break;
                    default:
                        Log.e(TAG, "Received an unhandled packet: " + received[0]);
                        break;
                }
            }
        }
    }
}
