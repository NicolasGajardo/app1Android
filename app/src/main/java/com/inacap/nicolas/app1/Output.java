package com.inacap.nicolas.app1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class Output extends AppCompatActivity{

    private static final Calendar AUX_CALENDAR = Calendar.getInstance();
    private static final int MAX_ANTIQUENESS = 10;

    private TextView patent;
    private TextView mark;
    private TextView model;
    private TextView year;
    private TextView ufValue;

    //news
    private TextView antiqueness;
    private TextView valid;
    private TextView secureValue;
    private Button back;
    private ImageView imgResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.output);

        this.initFields();

        final Bundle bundle = this.getIntent().getExtras();

        double insertedUFValue = bundle.getDouble("uf_value");
        int vehicleYear = bundle.getInt("year");

        patent.setText("Patent: " + bundle.getString("patent"));
        mark.setText("Mark: " + bundle.getString("mark"));
        model.setText("Model: " + bundle.getString("model"));
        year.setText("Year: " + vehicleYear);
        ufValue.setText("UF: " + insertedUFValue + "$");

        //Logic
        int auxAntiqueness = this.calculateAntiqueness(vehicleYear);
        boolean isValid = this.isValid(auxAntiqueness);
        antiqueness.setText(auxAntiqueness + " year(s)");
        valid.setText(isValid ? "Yes" : "No");
        secureValue.setText(this.secureCost(auxAntiqueness, insertedUFValue) + "$");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back(bundle);
            }
        });

        this.setImgView(isValid);
    }

    private void initFields() {

        patent = (TextView) findViewById(R.id.txv_patent);
        mark = (TextView) findViewById(R.id.txv_mark);
        model = (TextView) findViewById(R.id.txv_model);
        year = (TextView) findViewById(R.id.txv_year);
        ufValue = (TextView) findViewById(R.id.txv_uf_value);
        back = (Button) findViewById(R.id.btn_back);
        imgResult = (ImageView) findViewById(R.id.imv_result);
        antiqueness = (TextView) findViewById(R.id.txv_antiqueness);
        valid = (TextView) findViewById(R.id.txv_validity);
        secureValue = (TextView) findViewById(R.id.txv_security_cost);
    }

    private void back(Bundle bundle) {

        Intent sender = new Intent(Output.this, MainActivity.class);
        sender.putExtra("patent", bundle.getString("patent"));
        sender.putExtra("mark", bundle.getString("mark"));
        sender.putExtra("model", bundle.getString("model"));
        sender.putExtra("mark_item_position", bundle.getInt("mark_item_position"));
        sender.putExtra("year", bundle.getInt("year"));
        sender.putExtra("uf_value", bundle.getDouble("uf_value"));
        startActivity(sender);
    }

    private int calculateAntiqueness(int yearArg) {
        int actualYear = AUX_CALENDAR.getInstance().get(Calendar.YEAR);

        return actualYear - yearArg;
    }

    private boolean isValid(int years) {
        return years > MAX_ANTIQUENESS ? false : true;
    }

    private double secureCost(int oldness, double ufValue) {
        return this.secureCost(oldness, ufValue, this.isValid(oldness));
    }

    private double secureCost(int oldness, double ufValue, boolean isValid) {
        if (isValid) {
                return oldness == 0 || oldness == 1 ?
                        0.1 * ufValue : 0.1 * ufValue * oldness;
        }
        return 0;
    }

    private void setImgView(boolean isValid) {
        if (isValid) {
            imgResult.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.secured));
            Toast.makeText(getApplicationContext(), "SECURED", Toast.LENGTH_SHORT).show();
        } else {
            imgResult.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.not_secured));
            Toast.makeText(getApplicationContext(), "NOT SECURED", Toast.LENGTH_SHORT).show();
        }

    }
}
