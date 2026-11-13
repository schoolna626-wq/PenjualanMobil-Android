package com.example.tugasando;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class BerandaMActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_berandam);

        Button btnCash = findViewById(R.id.btn_laporan_cash);
        Button btnKredit = findViewById(R.id.btn_laporan_kredit);
        Button btnCicilan = findViewById(R.id.btn_laporan_cicilan);
        Button btnLogout = findViewById(R.id.btn_logout);

        btnCash.setOnClickListener(v ->
                startActivity(new Intent(this, LaporanCashActivity.class)));
        btnKredit.setOnClickListener(v ->
                startActivity(new Intent(this, LaporanKreditActivity.class)));
        btnCicilan.setOnClickListener(v ->
                startActivity(new Intent(this, LaporanBayarActivity.class)));
        btnLogout.setOnClickListener(v -> logout());
    }

    private void logout() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
