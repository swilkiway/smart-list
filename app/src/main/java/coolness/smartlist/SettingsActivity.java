package coolness.smartlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    private EditText expireNum;
    private int numberWeeks;
    private Switch remindersButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String storedWeeks = sharedPref.getString("storedWeeks", "6");
        boolean storedReminders = sharedPref.getBoolean("storedReminders",true);
        expireNum = findViewById(R.id.expire_num);
        expireNum.setText(storedWeeks);
        expireNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().equals("")) {
                    int tmp = Integer.parseInt(charSequence.toString());
                    if (tmp > 0) {
                        numberWeeks = tmp;
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.clear();
                        editor.putString("storedWeeks", charSequence.toString());
                        editor.apply();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        remindersButton = findViewById(R.id.reminders_button);
        remindersButton.setChecked(storedReminders);
        remindersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                editor.putBoolean("storedReminders", remindersButton.isChecked());
                editor.apply();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }
}
