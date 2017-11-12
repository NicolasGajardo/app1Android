package com.inacap.nicolas.app1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private static final Calendar AUX_CALENDAR = Calendar.getInstance();
    private static final String URL_MARKS = "https://vpic.nhtsa.dot.gov/api/vehicles/GetMakesForVehicleType/car?format=json";

    private EditText patent;
    private Spinner mark;
    private EditText model;
    private EditText year;
    private EditText valorUF;
    private Button ask;
    private JSONObject jsonObject;

    private ArrayList<String> marks = new ArrayList<>();

    private String markAux;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        patent = (EditText) findViewById(R.id.edt_patent);
        mark = (Spinner) findViewById(R.id.spn_mark);
        model = (EditText) findViewById(R.id.edt_model);
        year = (EditText) findViewById(R.id.edt_year);
        valorUF = (EditText) findViewById(R.id.edt_uf_value);
        ask = (Button) findViewById(R.id.btn_consult);

        ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData();
            }
        });

        new ProcessJSON().execute(URL_MARKS);

    }

    private void persistFields() {
        Bundle bundle = this.getIntent().getExtras();

        if (bundle != null && !bundle.isEmpty()) {
            double insertedUFValue = bundle.getDouble("uf_value");
            int vehicleYear = bundle.getInt("year");

            patent.setText(bundle.getString("patent"));
            model.setText(bundle.getString("model"));
            mark.setSelection(bundle.getInt("mark_item_position"));
            year.setText("" + vehicleYear);
            valorUF.setText("" + insertedUFValue);
        }
    }

    private void sendData() {
        //CHECK DATAS
        if (!this.checkFields(patent, model, year, valorUF)) {//model,
            return;
        } else if (!this.checkFields(mark)) {
            return;
        } else if (!this.checkVehicleYear(Integer.parseInt(year.getText().toString()))) {
            Toast.makeText(this, "Año incorrecto", Toast.LENGTH_SHORT).show();
            return;
        }

        String patentOutput = patent.getText().toString(),
                modelOutput = model.getText().toString(),
                markOutput = mark.getSelectedItem().toString();
        int yearOutput = Integer.parseInt(year.getText().toString()),
                markItemPosition = mark.getSelectedItemPosition();
        double ufValueOutput = Double.parseDouble(valorUF.getText().toString());

        Intent sender = new Intent(MainActivity.this, Output.class);
        sender.putExtra("patent", patentOutput);
        sender.putExtra("mark", markOutput);
        sender.putExtra("model", modelOutput);
        sender.putExtra("mark_item_position", markItemPosition);
        sender.putExtra("year", yearOutput);
        sender.putExtra("uf_value", ufValueOutput);

        startActivity(sender);
    }

    private boolean checkFields(EditText... args) {
        if (args.length == 0) {
            return false;
        }
        for (EditText editText : args) {
            if (editText.getText().toString().isEmpty()) {
                Toast.makeText(this, "Incorrect " +editText.getHint(), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private boolean checkFields(Spinner... args) {
        if (args.length == 0) {
            return false;
        }
        for (Spinner spinner : args) {
            System.out.println("Spinner flag: " + spinner.isSelected());
            if (spinner.isSelected()) {
                Toast.makeText(this, "Incorrect Spinner ", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private boolean checkVehicleYear(int year) {
        int actualYear = AUX_CALENDAR.getInstance().get(Calendar.YEAR);
        return year <= actualYear;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        System.out.println("onNothingSelected");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        System.out.println("onItemSelected");
    }

    private class ProcessJSON extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... strings){
            String urlString = strings[0];
            HTTPDataHandler hh = new HTTPDataHandler();
            String stream = hh.GetHTTPData(urlString);
            return stream;
        }

        protected void onPostExecute(String stream){

            if(stream !=null){
                try{
                    JSONObject objectReader = new JSONObject(stream);
                    JSONArray arrayReader = (JSONArray) objectReader.get("Results");

                    for (int i = 0; i < arrayReader.length(); i++) {
                        jsonObject = arrayReader.getJSONObject(i);
                        markAux = jsonObject.getString("MakeName");
                        marks.add(markAux);
                    }
                    mark.setAdapter(new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_spinner_dropdown_item, marks));

                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
            persistFields();
        }
    }

}
