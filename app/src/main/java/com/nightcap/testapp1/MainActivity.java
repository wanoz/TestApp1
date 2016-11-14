package com.nightcap.testapp1;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    // Declare realm object
    Realm realm_obj;

    // Declare other objects
    private EditText input_text;
    private TextView refresh_db;
    private String input_str;
    private Date date;
    private Long date_long;
    private Long time_diff;
    private Long second;
    private Long minute;
    private Long hour;
    private Long day;
    private String time_diff_format;
    private Button save_data;
    private Button clear_data;
    private Button refresh_data;

    private String toast_add;
    private String toast_clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Create realm instance.
        realm_obj = Realm.getDefaultInstance();

        // Create other variables and objects.
        input_text = (EditText)findViewById(R.id.trial_input1);
        refresh_db = (TextView)findViewById(R.id.query_result);
        toast_add = "Adding event: ";
        toast_clear = "Cleared database!";

        // Set buttons.
        save_data = (Button)findViewById(R.id.save_button);
        clear_data = (Button)findViewById(R.id.clear_button);
        refresh_data = (Button)findViewById(R.id.refresh_button);

        refresh_database();

        // Save/add data to the database.
        save_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do some action to add data to database.
                input_str = input_text.getText().toString();
                // Toast message to indicate data is being added to the database
                Toast.makeText(MainActivity.this, toast_add + input_str, Toast.LENGTH_SHORT).show();
                // TODO validation may be needed
                save_to_database(input_str);
            }
        });

        // Refresh the display of the database contents.
        refresh_data.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Do some action to refresh data.
                refresh_database();
            }
        });

        // Clear all database contents.
        clear_data.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Do some action to clear data.
                // Toast.makeText(MainActivity.this, toast_clear, Toast.LENGTH_SHORT).show();
                clear_database();
                refresh_database();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

    @Override
    // Closes realm object on destroy.
    public void onDestroy() {
        super.onDestroy();
        realm_obj.close();
    }

    // Clears all existing content in realm database.
    private void clear_database() {
        realm_obj.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<DataBank> result = realm.where(DataBank.class).findAll();
                result.deleteAllFromRealm();
            }
        });
    }

    // Saves data to realm database.
    private void save_to_database(final String event_str){
        date = new Date();
        date_long = date.getTime();
        realm_obj.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                DataBank input_data = bgRealm.createObject(DataBank.class);
                input_data.setEvent_name(event_str);
                input_data.setTime_ms(date_long);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Transaction was a success.
                Log.v("database", "data saved successfully");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Log.e("database", error.getMessage());
            }
        });

    }

    // Query the realm database and print the contents.
    private void refresh_database() {
        date = new Date();
        date_long = date.getTime();
        RealmResults<DataBank> query_result = realm_obj.where(DataBank.class).findAll();
        String output = "";
        for(DataBank input_data: query_result){
            time_diff = date_long - input_data.getTime_ms();
            second = (time_diff/1000) % 60;
            minute = (time_diff/(1000 * 60)) % 60;
            hour = (time_diff/(1000 * 60 * 60)) % 24;
            day = (time_diff/(1000 * 60 * 60 * 24)) % 365;
            time_diff_format = String.format("%d day, %d hr, %d min", day, hour, minute);
            output += "Activity: " + input_data.getEvent_name().toString()
                    + "  >>  "
//                    + "Time since: " + input_data.getTime_ms().toString()
                    + "Time since: " + time_diff_format
                    + "\n";
        }
        refresh_db.setText(output);

    }

}