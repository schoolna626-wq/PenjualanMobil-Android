package com.example.tugasando;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class DataCashActivity extends AppCompatActivity {

    private TextView tvDataCash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_bayarcash);

        tvDataCash = findViewById(R.id.tv_data_cash);
        Button btnTambah = findViewById(R.id.btn_tambah_cash);

        btnTambah.setOnClickListener(v ->
                startActivity(new Intent(this, TambahCashActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        tampilDataCash();
    }

    private void tampilDataCash() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                Config.TAMPIL_CASH,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        if (jsonArray.length() == 0) {
                            tvDataCash.setText("Belum ada data transaksi cash.");
                            return;
                        }

                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject item = jsonArray.getJSONObject(i);
                            sb.append("Kode Cash : ").append(item.optString("kode_cash")).append("\n");
                            sb.append("KTP Pembeli : ").append(item.optString("ktp")).append("\n");
                            sb.append("Kode Mobil : ").append(item.optString("kode_mobil")).append("\n");
                            sb.append("Tanggal Cash : ").append(item.optString("cash_tanggal")).append("\n");
                            sb.append("Nominal Bayar : ").append(item.optString("cash_bayar")).append("\n\n");
                        }
                        tvDataCash.setText(sb.toString());
                    } catch (Exception e) {
                        tvDataCash.setText("Format data cash tidak sesuai.");
                        Toast.makeText(this, "Gagal membaca data cash", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    tvDataCash.setText("Tidak bisa terhubung ke server.");
                    Toast.makeText(this, "Cek Laragon dan IP 192.168.1.8", Toast.LENGTH_LONG).show();
                }
        );
        Volley.newRequestQueue(this).add(stringRequest);
    }
}
