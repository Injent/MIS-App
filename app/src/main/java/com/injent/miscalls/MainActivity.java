package com.injent.miscalls;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.injent.miscalls.ui.auth.AuthFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.statusBar, getTheme()));
        getWindow().setStatusBarColor(getResources().getColor(R.color.statusBar, getTheme()));

        if (savedInstanceState != null && savedInstanceState.getBoolean(getString(R.string.keyOffUpdates), false)) {
            AuthFragment authFragment = new AuthFragment();
            Bundle args = new Bundle();
            args.putBoolean(getString(R.string.keyOffUpdates), true);
            authFragment.setArguments(args);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container,authFragment)
                    .commit();
        }
    }
}