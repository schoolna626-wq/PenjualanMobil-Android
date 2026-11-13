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

public class DataPaketActivity extends AppCompatActivity {

    private ArrayList<String> itemList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_paket);

        Button btnTambah = findViewById(R.id.tomboltambah);
        ListView listView = findViewById(R.id.listpaket);

        itemList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemList);
        listView.setAdapter(adapter);

        btnTambah.setOnClickListener(v ->
                startActivity(new Intent(this, TambahPaketActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        ambilDataPaket();
    }

    private void ambilDataPaket() {
        StringRequest request = new StringRequest(
                Request.Method.GET,
                Config.TAMPIL_PAKET,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        itemList.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            String kode = obj.optString("kode_paket");
                            String dp = obj.optString("uang_muka");
                            String tenor = obj.optString("tenor");
                            String bunga = obj.optString("bunga_cicilan");
                            itemList.add(
                                    "Kode Paket: " + kode
                                            + "\nDP: " + dp + "%"
                                            + "\nTenor: " + tenor + " bulan"
                                            + "\nBunga: " + bunga + "%"
                            );
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Toast.makeText(this, "Gagal membaca data paket: " + response, Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Gagal memuat data paket. Cek Laragon dan IP 192.168.1.8", Toast.LENGTH_LONG).show()
        );
        Volley.newRequestQueue(this).add(request);
    }
}
