package com.sjinadasa.nicdecodersrilanka;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText nicNumberInput;
    private Spinner nicLetterSelector;
    private Button decodeButton;
    private TextView bDayAns;
    private TextView genderAns;
    private TextView voteAns;

    private Activity thisActivity;

    private CharSequence nicLetter;
    private String nicNumber;

    private int[] monthDateNumbers = new int[] {31,29,31,30,31,30,31,31,30,31,30,31};
    private String[] months = new String[]{"January","February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nicNumberInput = (EditText) findViewById(R.id.nicNumberInput);
        decodeButton = (Button) findViewById(R.id.decodeBtn);

        // declare these views and then set these views to invisible
        bDayAns = (TextView) findViewById(R.id.bDayView);
        bDayAns.setVisibility(View.INVISIBLE);
        genderAns = (TextView) findViewById(R.id.genderAns);
        genderAns.setVisibility(View.INVISIBLE);
        voteAns = (TextView) findViewById(R.id.voteAns);
        voteAns.setVisibility(View.INVISIBLE);


        thisActivity = this;

        // Setting up the spinner for NIC letter input
        nicLetterSelector = (Spinner)findViewById(R.id.nic_letter_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.nic_letter_array,R.layout.spinner_layout);
        nicLetterSelector.setAdapter(adapter);

        nicLetterSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               nicLetter = (CharSequence) nicLetterSelector.getItemAtPosition(i);
                Log.i("check",nicLetter.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                nicLetter = "-";
            }
        });

        // setting up button on click listener
        decodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hides the keyboard on pressing the button
                hideKeyboard(thisActivity);

                // show the answers
                bDayAns.setVisibility(View.VISIBLE);
                genderAns.setVisibility(View.VISIBLE);

                if(nicLetter.equals("V")){
                    voteAns.setVisibility(View.VISIBLE);
                }else{
                    voteAns.setVisibility(View.INVISIBLE);
                }

                if(nicNumberInput.length() == 9){
                    // Checking if the date input is correct
                    if(checkDateInput(nicNumberInput.getText().toString().substring(2,5))){
                        String bDay = getBDayOldNIC(nicNumberInput.getText().toString());
                        Log.i("birthday", bDay);

                        // Getting the gender
                        String gender = getGender(nicNumberInput.getText().toString().substring(2,5));

                        // Setting the answers
                        bDayAns.setText(bDay);
                        genderAns.setText(gender);
                    }else{
                        nicNumberInput.setText("");
                        nicNumberInput.setHint(R.string.error_hint);
                    }

                }else if(nicNumberInput.length() == 12){
                    // Checking if the date input is correct
                    if(checkDateInput(nicNumberInput.getText().toString().substring(4,7))){
                        String bDay = getBDayNewNIC(nicNumberInput.getText().toString());
                        Log.i("birthday", bDay);

                        // Get gender
                        String gender = getGender(nicNumberInput.getText().toString().substring(4,7));

                        // Setting the answers
                        bDayAns.setText(bDay);
                        genderAns.setText(gender);
                    }else{
                        nicNumberInput.setText("");
                        nicNumberInput.setHint(R.string.error_hint);
                    }
                }else{
                    nicNumberInput.setText("");
                    nicNumberInput.setHint(R.string.error_hint);
                }
            }
        });

    }

    private String getBDayOldNIC(String nicNumber){
        // Every person with the old NIC was born in the 19xxs
        String year = "19" + nicNumber.substring(0,2);
        String date = getDate(nicNumber.substring(2,5));

        Log.i("yearTest", year);
        return date + " - " + year;
    }

    private String getBDayNewNIC(String nicNumber){
        // The year is represented as 4 integers. So it is easier for us
        String year = nicNumber.substring(0,4);
        String date = getDate(nicNumber.substring(4,7));

        Log.i("yearTest", year);
        return date + " - " + year;
    }

    private String getGender(String nicDate){
        int date = Integer.parseInt(nicDate);

        if(date < 500){
            return "A Man";
        }
        return "A Woman";
    }

    private String getDate(String dateString){
        int date = Integer.parseInt(dateString);
        String day = "";
        String month = "";

        if(date > 500 && date < 867){
            date = date - 500;
        }

        for(int i = 0; i < 12; i++){
            if(monthDateNumbers[i] >= date){
                day = Integer.toString(date);
                month = months[i];
                break;
            }
            date = date - monthDateNumbers[i];
        }

        return day + " - " + month;
    }

    // The dates have to be within a certain range for the NIC number to be valid. SO this checks whether the input is valid
    private boolean checkDateInput(String nicDate){
        int date = Integer.parseInt(nicDate);
        if(date > 366 && date <= 500){
            return false;
        }else if(date > 866){
            return false;
        }
        return true;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
