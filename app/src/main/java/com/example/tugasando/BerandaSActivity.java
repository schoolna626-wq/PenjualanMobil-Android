package com.example.tugasando;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class BerandaSActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_berandas);

        Button btnMobil = findViewById(R.id.btn_data_mobil);
        Button btnPembeli = findViewById(R.id.btn_data_pembeli);
        Button btnPaket = findViewById(R.id.btn_data_paket);
        Button btnCash = findViewById(R.id.btn_data_cash);
        Button btnKredit = findViewById(R.id.btn_data_kredit);
        Button btnCicilan = findViewById(R.id.btn_data_cicilan);
        Button btnLogout = findViewById(R.id.btn_logout);

        btnMobil.setOnClickListener(v ->
                startActivity(new Intent(this, DataMobilActivity.class)));
        btnPembeli.setOnClickListener(v ->
                startActivity(new Intent(this, DataPembeliActivity.class)));
        btnPaket.setOnClickListener(v ->
                startActivity(new Intent(this, DataPaketActivity.class)));
        btnCash.setOnClickListener(v ->
                startActivity(new Intent(this, DataCashActivity.class)));
        btnKredit.setOnClickListener(v ->
                startActivity(new Intent(this, DataKreditActivity.class)));
        btnCicilan.setOnClickListener(v ->
                startActivity(new Intent(this, DataCicilActivity.class)));
        btnLogout.setOnClickListener(v -> logout());
    }

    private void logout() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
