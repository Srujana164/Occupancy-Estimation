package com.debugandroid.VideoGallery;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class SplashActivity extends Activity {
    private RadioGroup radioSexGroup;
    private RadioButton radioSexButton;
    private Button btnDisplay;
    private EditText editText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        addListenerOnButton();
    }

    public void addListenerOnButton() {

        radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);
        btnDisplay = (Button) findViewById(R.id.btnDisplay);
        editText = (EditText) findViewById(R.id.editText);
        btnDisplay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int selectedId = radioSexGroup.getCheckedRadioButtonId();
                radioSexButton = (RadioButton) findViewById(selectedId);
                String email = editText.getText().toString();
                System.out.print(email);
                Intent myIntent = new Intent(SplashActivity.this, MainActivity.class);
                myIntent.putExtra("key", radioSexButton.getText()); //Optional parameters
                myIntent.putExtra("email",email);
                startActivity(myIntent);

            }

        });

    }
}
