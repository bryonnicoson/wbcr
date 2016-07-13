package com.bryonnicoson.wbcr;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DogSearchActivity extends AppCompatActivity {

    private Spinner breedSpinner;
    private CheckBox checkBoxSmall, checkBoxMedium, checkBoxLarge;
    private RadioButton radioF, radioM, radioFM;
    private View selectedRadio;

    DatabaseHelper db;
    ListView mDogList;
    Cursor mCursor;
    DogCursorAdapter mAdapter;
    AdapterView.OnItemClickListener mDogClickListener;
    Intent mDetailIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = DatabaseHelper.getInstance(this);

        selectedRadio = findViewById(R.id.radio_fm);

        // Sex RadioGroup
        radioF = (RadioButton) findViewById(R.id.radio_f);
        radioM = (RadioButton) findViewById(R.id.radio_m);
        radioFM = (RadioButton) findViewById(R.id.radio_fm);

        // Size Checkboxes
        checkBoxSmall = (CheckBox) findViewById(R.id.checkbox_small);
        checkBoxMedium = (CheckBox) findViewById(R.id.checkbox_medium);
        checkBoxLarge = (CheckBox) findViewById(R.id.checkbox_large);

        // Breed Spinner
        breedSpinner = (Spinner) findViewById(R.id.spinner_breed);
        List<String> spinnerBreed = new ArrayList<>(
                Arrays.asList(getResources().getStringArray(R.array.spinner_breed)));
        ArrayAdapter adapterBreed = new ArrayAdapter(this, android.R.layout.simple_list_item_1, spinnerBreed);
        breedSpinner.setAdapter(adapterBreed);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // BEGIN RUBEGOLDBERGIAN QUERY BUILDER

                StringBuilder query = new StringBuilder("SELECT * FROM dog");  // simple...arf!
                boolean sexSpecified = false;

                // DOG_SEX from RadioGroup
                switch (selectedRadio.getId()) {
                    case R.id.radio_f:
                        query.append(" WHERE sex = 'Female'");
                        sexSpecified = true;
                        break;
                    case R.id.radio_m:
                        query.append(" WHERE sex = 'Male'");
                        sexSpecified = true;
                        break;
                    default:
                        break;
                }

                // DOG_SIZE from Checkbox
                ArrayList<String> checked = new ArrayList<>();

                if (checkBoxSmall.isChecked()) checked.add("Small");
                if (checkBoxMedium.isChecked()) checked.add("Medium");
                if (checkBoxLarge.isChecked()) checked.add("Large");

                if (checked.size() < 3) {  // if we have to specify an argument, prepare our syntax
                    if (sexSpecified) {
                        query.append(" AND "); // if we specified a sex, this is a second condition
                        if (checked.size() > 1){
                            query.append(" (");
                        }
                    } else {
                        query.append(" WHERE ");    // if we didn't, this is the first condition
                    }

                    switch (checked.size()) {
                        case 1:
                            query.append("size = '" + checked.get(0) + "'");
                            break;
                        case 2:
                            query.append("size = '" + checked.get(0) + "' OR size = '" + checked.get(1) + "'");
                            if(sexSpecified) query.append(")");
                            break;
                        default:
                            break;
                    }
                }
                query.append(";");
                Toast.makeText(DogSearchActivity.this, query.toString(), Toast.LENGTH_LONG).show();
                //mDogList = (ListView) findViewById(R.id.dog_search_list_view);
                mCursor = db.showDogs();
                //mAdapter = new DogCursorAdapter(this, mCursor, 0);
                mDogList.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        selectedRadio = view;
    }

}
