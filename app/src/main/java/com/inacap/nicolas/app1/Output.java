package com.inacap.nicolas.app1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class Output extends AppCompatActivity {

    private static final Calendar AUX_CALENDAR = Calendar.getInstance();
    private static final int ANTIGUEDAD_MAX = 10;

    private TextView patente;
    private TextView marca;
    private TextView modelo;
    private TextView anio;
    private TextView valorUF;

    //nuevas
    private TextView antiguedad;
    private TextView valido;
    private TextView valorSeguro;
    private Button volver;
    private ImageView resultadoImagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.output);

        this.initFields();

        final Bundle bundle = this.getIntent().getExtras();

        double valorUFIngresado = bundle.getDouble("valorUF");
        int anioAuto = bundle.getInt("anio");

        patente.setText("Patente: " + bundle.getString("patente"));
        marca.setText("Marca: " + bundle.getString("marca"));
        modelo.setText("Modelo: " + bundle.getString("modelo"));
        anio.setText("Año: " + anioAuto);
        valorUF.setText("UF: " + valorUFIngresado + "$");

        //Lógica
        int auxAntiguedad = this.calcularAntiguedad(anioAuto);
        boolean esValido = this.esValido(auxAntiguedad);
        antiguedad.setText(auxAntiguedad + " año(s)");
        valido.setText(esValido ? "Sí" : "No");
        valorSeguro.setText("Valor seguro: " + this.valorSeguro(auxAntiguedad, valorUFIngresado) + "$");

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                volver(bundle);
            }
        });

        this.setImgView(esValido);
    }

    private void initFields() {

        patente = (TextView) findViewById(R.id.txv_patente);
        marca = (TextView) findViewById(R.id.txv_marca);
        modelo = (TextView) findViewById(R.id.txv_modelo);
        anio = (TextView) findViewById(R.id.txv_anio);
        valorUF = (TextView) findViewById(R.id.txv_valor_uf);
        volver = (Button) findViewById(R.id.btn_volver);
        resultadoImagen = (ImageView) findViewById(R.id.imv_resultado);
        antiguedad = (TextView) findViewById(R.id.txv_antiguedad);
        valido = (TextView) findViewById(R.id.txv_validez);
        valorSeguro = (TextView) findViewById(R.id.txv_valor_seguro);
    }

    private void volver(Bundle bundle) {

        Intent envio = new Intent(Output.this, MainActivity.class);
        envio.putExtra("patente", bundle.getString("patente"));
        envio.putExtra("marca", bundle.getString("marca"));
        envio.putExtra("modelo", bundle.getString("modelo"));
        envio.putExtra("anio", bundle.getInt("anio"));
        envio.putExtra("valorUF", bundle.getDouble("valorUF"));
        startActivity(envio);
    }

    private int calcularAntiguedad(int anio) {
        int anioActual = AUX_CALENDAR.getInstance().get(Calendar.YEAR);

        return anioActual - anio;
    }

    private boolean esValido(int anios) {
        return anios > ANTIGUEDAD_MAX ? false : true;
    }

    private double valorSeguro(int antiguedad, double valorUf) {
        return this.valorSeguro(antiguedad, valorUf, this.esValido(antiguedad));
    }

    private double valorSeguro(int antiguedad, double valorUf, boolean esValido) {
        if (esValido) {
            return 0.1 * valorUf * (ANTIGUEDAD_MAX - antiguedad);
        }
        return 0;
    }

    private void setImgView(boolean esValido) {
        if (esValido) {
            resultadoImagen.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.si_asegurado));
            Toast.makeText(getApplicationContext(), "ASEGURADO", Toast.LENGTH_SHORT).show();
        } else {
            resultadoImagen.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.no_asegurado));
            Toast.makeText(getApplicationContext(), "NO ASEGURADO", Toast.LENGTH_SHORT).show();
        }

    }
}
