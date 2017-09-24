package com.inacap.nicolas.app1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final Calendar AUX_CALENDAR = Calendar.getInstance();

    private EditText patente;
    private EditText marca;
    private EditText modelo;
    private EditText anio;
    private EditText valorUF;
    private Button consultar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        patente = (EditText) findViewById(R.id.edt_patente);
        marca = (EditText) findViewById(R.id.edt_marca);
        modelo = (EditText) findViewById(R.id.edt_modelo);
        anio = (EditText) findViewById(R.id.edt_anio);
        valorUF = (EditText) findViewById(R.id.edt_valor_uf);
        consultar = (Button) findViewById(R.id.btn_consultar);

        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarDatos();
            }
        });

        this.persistFields();
    }

    private void persistFields() {
        Bundle bundle = this.getIntent().getExtras();

        if (bundle != null && !bundle.isEmpty()) {
            double valorUFIngresado = bundle.getDouble("valorUF");
            int anioAuto = bundle.getInt("anio");

            patente.setText(bundle.getString("patente"));
            marca.setText(bundle.getString("marca"));
            modelo.setText(bundle.getString("modelo"));
            anio.setText("" + anioAuto);
            valorUF.setText("" + valorUFIngresado);
        }
    }

    private void enviarDatos() {
        //CHECK DATOS
        if (!this.checkFields(patente, marca, modelo, anio, valorUF)) {
            return;
        } else if (!this.checkAnioVehiculo(Integer.parseInt(anio.getText().toString()))) {
            Toast.makeText(this, "Año incorrecto", Toast.LENGTH_SHORT).show();
            return;
        }

        String patenteOutput = patente.getText().toString(),
                marcaOutput = marca.getText().toString(),
                modeloOutput = modelo.getText().toString();
        int anioOutput = Integer.parseInt(anio.getText().toString());
        double valorUFOutput = Double.parseDouble(valorUF.getText().toString());

        Intent envio = new Intent(MainActivity.this, Output.class);
        envio.putExtra("patente", patenteOutput);
        envio.putExtra("marca", marcaOutput);
        envio.putExtra("modelo", modeloOutput);
        envio.putExtra("anio", anioOutput);
        envio.putExtra("valorUF", valorUFOutput);

        startActivity(envio);
    }

    private boolean checkFields(EditText... args) {
        if (args.length == 0) {
            return false;
        }
        for (EditText editText : args) {
            if (editText.getText().toString().isEmpty()) {
                Toast.makeText(this, editText.getHint() + " incorrecto", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private boolean checkAnioVehiculo(int anio) {
        int anioActual = AUX_CALENDAR.getInstance().get(Calendar.YEAR);
        return anio <= anioActual;
    }
}
