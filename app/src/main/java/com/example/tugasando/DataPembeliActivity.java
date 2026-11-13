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

public class DataPembeliActivity extends AppCompatActivity {

    private ArrayList<String> itemList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_pembeli);

        Button btnTambah = findViewById(R.id.tomboltambah);
        ListView listView = findViewById(R.id.listpembeli);

        itemList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemList);
        listView.setAdapter(adapter);

        btnTambah.setOnClickListener(v ->
                startActivity(new Intent(this, TambahPembeliActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        tampilData();
    }

    private void tampilData() {
        StringRequest request = new StringRequest(
                Request.Method.GET,
                Config.TAMPIL_PEMBELI,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        itemList.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            String ktp = obj.optString("ktp");
                            String nama = obj.optString("nama_pembeli");
                            String alamat = obj.optString("alamat_pembeli");
                            String noHp = obj.optString("telp_pembeli");
                            itemList.add(
                                    "KTP: " + ktp
                                            + "\nNama: " + nama
                                            + "\nAlamat: " + alamat
                                            + "\nNomor HP: " + noHp
                            );
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Toast.makeText(this, "Gagal membaca data pembeli: " + response, Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Gagal memuat data pembeli. Cek Laragon dan IP 192.168.1.8", Toast.LENGTH_LONG).show()
        );
        Volley.newRequestQueue(this).add(request);
    }
}
