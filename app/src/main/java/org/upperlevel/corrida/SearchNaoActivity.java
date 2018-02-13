package org.upperlevel.corrida;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SearchNaoActivity extends AppCompatActivity {
    public static final String TAG = "SearchNaoActivity";
    public static final long TEXT_UPDATER_DELAY = 1000;

    private TextView text;
    private Thread textUpdater;

    private SearchNaoTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_nao);

        ((CorridaApplication) getApplication()).reset();

        text = findViewById(R.id.search_nao_text);
        textUpdater = new TextUpdater();

        task = new SearchNaoTask(this);
        task.start();
        textUpdater.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        textUpdater.interrupt();
        task.interrupt();
    }

    public class TextUpdater extends Thread {
        private int countOfPoints = 1;

        @Override
        public void run() {
            while (isAlive()) {
                // needed to edit a view
                runOnUiThread(() -> {
                    StringBuilder content = new StringBuilder("Cercando il NAO");
                    for (int i = 0; i < countOfPoints; i++) {
                        content.append(".");
                    }
                    text.setText(content);
                    if (countOfPoints++ == 3) {
                        countOfPoints = 1;
                    }
                });
                try {
                    Thread.sleep(TEXT_UPDATER_DELAY);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}
