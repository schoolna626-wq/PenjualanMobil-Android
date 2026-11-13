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

public class DataCicilActivity extends AppCompatActivity {

    private ArrayList<String> itemList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_pembayarancicilan);

        Button btnTambah = findViewById(R.id.tomboltambah);
        ListView listView = findViewById(R.id.listbayarcicilan);

        itemList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemList);
        listView.setAdapter(adapter);

        btnTambah.setOnClickListener(v ->
                startActivity(new Intent(this, TambahCicilanActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        tampilData();
    }

    private void tampilData() {
        StringRequest request = new StringRequest(
                Request.Method.GET,
                Config.TAMPIL_CICILAN,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        itemList.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            itemList.add(
                                    "Kode Cicilan: " + obj.optString("kode_cicilan")
                                            + "\nKode Kredit: " + obj.optString("kode_kredit")
                                            + "\nTanggal: " + obj.optString("tanggal_cicilan")
                                            + "\nCicilan ke: " + obj.optString("cicilanke")
                                            + "\nJumlah: " + obj.optString("jumlah_cicilan")
                                            + "\nSisa Cicilan Ke: " + obj.optString("sisacicilke")
                                            + "\nSisa: " + obj.optString("sisa_cicilan")
                            );
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Toast.makeText(this, "Gagal membaca data cicilan", Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Gagal memuat data cicilan. Cek Laragon dan IP 192.168.1.8", Toast.LENGTH_LONG).show()
        );
        Volley.newRequestQueue(this).add(request);
    }
}
