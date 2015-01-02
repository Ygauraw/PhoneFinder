package com.mzar.phonefinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity
{
    /* Known Bugs:
        -"Activated" prompt appears when the app is closed, and it no longer works.
        -"Phrase changed" prompt appears every time the button is pressed, instead of every time a new phrase is entered.
        -If the service is activated and the app is closed, the buttons will not actually match the state of the service if the app is launched again.
     */

    private static final String DEFAULT_KEY = "Ring!";
    private static final String PHRASE_KEY_IN_PREFERENCES = "userPhrase";
    private static SharedPreferences keyStorage;
    public static String userPhrase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Handles retrieval and displaying of user key at launch.
        final EditText editText_keywordField = (EditText)findViewById(R.id.editText_keywordField);
        keyStorage = getPreferences(0);
        if(keyStorage.contains(PHRASE_KEY_IN_PREFERENCES))
        {
            userPhrase = keyStorage.getString(PHRASE_KEY_IN_PREFERENCES, "");
        }
        else
        {
            userPhrase = DEFAULT_KEY;
        }
        editText_keywordField.setText(userPhrase);

        final Button button_start = (Button)findViewById(R.id.button_start);
        final Button button_stop = (Button)findViewById(R.id.button_stop);
        button_stop.setEnabled(false);

        //Activate button.
        button_start.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startService(new Intent(getBaseContext(), TextManager.class));
                button_start.setEnabled(false);
                button_stop.setEnabled(true);
            }
        });

        //Deactivate button.
        button_stop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                stopService(new Intent(getBaseContext(), TextManager.class));
                button_start.setEnabled(true);
                button_stop.setEnabled(false);
            }
        });

        //Set phrase button.
        Button button_setPhrase = (Button)findViewById(R.id.button_setPhrase);
        button_setPhrase.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                userPhrase = editText_keywordField.getText().toString();
                Intent phraseChanged = new Intent();
                phraseChanged.setAction(getString(R.string.INTENT_PHRASE_CHANGED));
                phraseChanged.putExtra(getString(R.string.EXTRA_USER_PHRASE), userPhrase);
                sendBroadcast(phraseChanged);
                Toast.makeText(getApplicationContext(), "Phrase changed to " + userPhrase + ".", Toast.LENGTH_SHORT).show(); //Appears with every press of the button. Rework.
            }
        });

        //Stop ringing button.
        Button button_foundIt = (Button)findViewById(R.id.button_foundIt);
        button_foundIt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent stopRinging = new Intent();
                stopRinging.setAction(getString(R.string.INTENT_STOP_RINGING));
                sendBroadcast(stopRinging);
            }
        });
    }

    @Override
    public void onStop()
    {
        super.onStop();
        SharedPreferences.Editor editor = keyStorage.edit();
        editor.putString(PHRASE_KEY_IN_PREFERENCES, userPhrase);
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
