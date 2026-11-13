
package com.example.tugasando;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DataKreditActivity extends AppCompatActivity {

    private final ArrayList<String> listKredit = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ListView listViewKredit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_belikredit);

        listViewKredit = findViewById(R.id.listkredit);
        Button btnTambah = findViewById(R.id.tomboltambah);

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                listKredit
        );

        listViewKredit.setAdapter(adapter);

        btnTambah.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                DataKreditActivity.this,
                                TambahKreditActivity.class
                        )
                )
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        tampilDataKredit();
    }

    private void tampilDataKredit() {
        StringRequest request = new StringRequest(
                Request.Method.GET,
                Config.TAMPIL_KREDIT,
                response -> {
                    try {
                        JSONArray dataArray = new JSONArray(response);

                        listKredit.clear();

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject data = dataArray.getJSONObject(i);

                            String kode = data.optString("kode_kredit");
                            String paket = data.optString("kode_paket");
                            String ktp = data.optString("ktp");
                            String mobil = data.optString("kode_mobil");
                            String tanggal = data.optString("tanggal_kredit");
                            String bayar = data.optString("bayar_kredit");

                            listKredit.add(
                                    "Kode Kredit: " + kode
                                            + "\nKode Paket: " + paket
                                            + "\nKTP Pembeli: " + ktp
                                            + "\nKode Mobil: " + mobil
                                            + "\nTanggal Kredit: " + tanggal
                                            + "\nBayar Kredit: Rp " + bayar
                            );
                        }

                        adapter.notifyDataSetChanged();

                        if (dataArray.length() == 0) {
                            Toast.makeText(
                                    this,
                                    "Belum ada data pembelian kredit",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(
                                this,
                                "Gagal membaca JSON kredit: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                },
                error -> Toast.makeText(
                        this,
                        "Gagal mengambil data kredit. Periksa URL PHP dan koneksi server.",
                        Toast.LENGTH_LONG
                ).show()
        );

        Volley.newRequestQueue(this).add(request);
    }
}
