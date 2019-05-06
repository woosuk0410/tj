package com.example.android.tj;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.android.tj.model.TJServiceCommand;
import com.example.android.tj.model.TJServiceSearchResult;
import com.google.gson.Gson;

import java.util.LinkedList;

import static com.example.android.tj.Constants.INTENT_PARAM_HASH;
import static com.example.android.tj.Constants.SERVICE_ANSWER;
import static com.example.android.tj.Constants.SERVICE_ANSWER_SEARCH;
import static com.example.android.tj.Constants.SERVICE_CMD;
import static com.example.android.tj.Constants.SERVICE_CMD_PLAY_FROM_HASH;
import static com.example.android.tj.Constants.SERVICE_QUERY_SEARCH;

public class SearchableActivity extends AppCompatActivity {

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String searchResultStr = intent.getStringExtra(SERVICE_ANSWER_SEARCH);
            if (searchResultStr == null) return;

            currentResult = new Gson().fromJson(searchResultStr, TJServiceSearchResult.class);
            adapter.clear();
            adapter.addAll(currentResult.fileNames);
            adapter.notifyDataSetChanged();

            Snackbar snackbar = Snackbar.make(findViewById(R.id.list_searchable_files),
                    currentResult.hashes.size() + " found.", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    };

    ArrayAdapter<String> adapter;
    TJServiceSearchResult currentResult;

    private void playFromHash(String hash) {
        Intent intent = new Intent(this, TJService.class);
        intent.putExtra(SERVICE_CMD, new TJServiceCommand(SERVICE_CMD_PLAY_FROM_HASH, hash)
                .toString());
        startService(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
                new IntentFilter(SERVICE_ANSWER));

        // list view
        ListView lv = findViewById(R.id.list_searchable_files);
        adapter = new ArrayAdapter<>(this, R.layout.activity_listview, new LinkedList<>());
        lv.setAdapter(adapter);
        lv.setOnItemClickListener((parent, view, position, id) -> {
            String hash = currentResult.hashes.get(position);
            playFromHash(hash);
            finish();
        });

        lv.setOnItemLongClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getApplicationContext(), MetadataActivity.class);
            String hash = currentResult.hashes.get(position);
            intent.putExtra(INTENT_PARAM_HASH, hash);
            startActivity(intent);
            return true;
        });

        // search
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            sendSearchQuery(query);
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            sendSearchQuery(query);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // https://developer.android.com/guide/topics/search/search-dialog#java
        // https://developer.android.com/training/search/setup#java

        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        return true;
    }

    private void sendSearchQuery(String query) {
        Intent intent = new Intent(this, TJService.class);
        intent.putExtra(SERVICE_CMD, new TJServiceCommand(SERVICE_QUERY_SEARCH, query).toString());
        startService(intent);
    }
}
