package org.upperlevel.corrida.phase.fetch;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import org.upperlevel.corrida.R;
import org.upperlevel.corrida.phase.Phase;

public class SelectFetchMethodPhase implements Phase {
    private FetchPhase parent;
    private Activity activity;

    public SelectFetchMethodPhase(FetchPhase parent) {
        this.parent = parent;
        this.activity = parent.getActivity();
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void sendNoConnection() {
        Toast.makeText(activity, "Non sei connesso ad internet", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart() {
        activity.findViewById(R.id.search_nao_button).setOnClickListener(view -> {
            if (!isOnline()) {
                sendNoConnection();
                return;
            }
            parent.setPhase(new SearchNaoPhase(parent));
        });
        activity.findViewById(R.id.join_nao_button).setOnClickListener(view -> {
            if (!isOnline()) {
                sendNoConnection();
                return;
            }
            parent.setPhase(new JoinNaoPhase(parent));
        });
    }

    @Override
    public void onStop() {
    }
}
