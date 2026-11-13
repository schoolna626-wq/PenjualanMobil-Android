package com.example.tugasando;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
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
import java.util.HashMap;
import java.util.Map;

public class DataMobilActivity extends AppCompatActivity {

    private ListView listView;
    private final ArrayList<String> daftarMobil = new ArrayList<>();
    private final ArrayList<String> kodeMobilList = new ArrayList<>();
    private final ArrayList<String> merkMobilList = new ArrayList<>();
    private final ArrayList<String> typeMobilList = new ArrayList<>();
    private final ArrayList<String> warnaMobilList = new ArrayList<>();
    private final ArrayList<String> hargaMobilList = new ArrayList<>();
    private final ArrayList<String> fotoMobilList = new ArrayList<>();
    private MobilAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_mobil);

        listView = findViewById(R.id.listmobil);
        Button tombolTambah = findViewById(R.id.tomboltambah);

        adapter = new MobilAdapter(this, daftarMobil, fotoMobilList);
        listView.setAdapter(adapter);

        tombolTambah.setOnClickListener(v ->
                startActivity(new Intent(this, TambahMobilActivity.class)));

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String[] opsiAksi = {"Detail", "Edit", "Hapus"};
            new AlertDialog.Builder(this)
                    .setTitle("Pilihan Aksi")
                    .setItems(opsiAksi, (dialog, which) -> {
                        if (which == 0) {
                            String detailMobil = "Kode Mobil : " + kodeMobilList.get(position)
                                    + "\nMerk Mobil : " + merkMobilList.get(position)
                                    + "\nTipe Mobil : " + typeMobilList.get(position)
                                    + "\nWarna Mobil : " + warnaMobilList.get(position)
                                    + "\nHarga Mobil : " + hargaMobilList.get(position);
                            new AlertDialog.Builder(this)
                                    .setTitle("Detail Mobil")
                                    .setMessage(detailMobil)
                                    .setPositiveButton("OK", null)
                                    .show();
                        } else if (which == 1) {
                            Intent intent = new Intent(this, EditMobilActivity.class);
                            intent.putExtra("kode_mobil", kodeMobilList.get(position));
                            intent.putExtra("merk_mobil", merkMobilList.get(position));
                            intent.putExtra("type_mobil", typeMobilList.get(position));
                            intent.putExtra("warna_mobil", warnaMobilList.get(position));
                            intent.putExtra("harga_mobil", hargaMobilList.get(position));
                            startActivity(intent);
                        } else {
                            String kodeMobil = kodeMobilList.get(position);
                            new AlertDialog.Builder(this)
                                    .setTitle("Hapus Data")
                                    .setMessage("Yakin ingin menghapus mobil [" + kodeMobil + "]?")
                                    .setPositiveButton("Hapus", (d, w) -> hapusMobil(kodeMobil))
                                    .setNegativeButton("Batal", null)
                                    .show();
                        }
                    })
                    .show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        tampilDataMobil();
    }

    private void tampilDataMobil() {
        StringRequest request = new StringRequest(
                Request.Method.GET,
                Config.TAMPIL_MOBIL,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        daftarMobil.clear();
                        kodeMobilList.clear();
                        merkMobilList.clear();
                        typeMobilList.clear();
                        warnaMobilList.clear();
                        hargaMobilList.clear();
                        fotoMobilList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            String kode = object.optString("kode_mobil");
                            String merk = object.optString("merk");
                            String type = object.optString("type");
                            String warna = object.optString("warna");
                            String harga = object.optString("harga");
                            String foto = object.optString("foto");

                            kodeMobilList.add(kode);
                            merkMobilList.add(merk);
                            typeMobilList.add(type);
                            warnaMobilList.add(warna);
                            hargaMobilList.add(harga);
                            fotoMobilList.add(foto);

                            daftarMobil.add(
                                    "Kode Mobil : " + kode
                                            + "\nMerk Mobil : " + merk
                                            + "\nTipe Mobil : " + type
                                            + "\nWarna Mobil : " + warna
                                            + "\nHarga Mobil : " + harga
                            );
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Toast.makeText(this, "Gagal membaca data mobil: " + response, Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Gagal memuat data mobil. Cek Laragon dan IP 192.168.1.8", Toast.LENGTH_LONG).show()
        );
        Volley.newRequestQueue(this).add(request);
    }

    private void hapusMobil(String kodeMobil) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Config.HAPUS_MOBIL,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        Toast.makeText(this, json.optString("message", "Data mobil dihapus"), Toast.LENGTH_SHORT).show();
                        if (json.optBoolean("success", false)) {
                            tampilDataMobil();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Respon server tidak sesuai", Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Gagal menghapus data mobil", Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("kode_mobil", kodeMobil);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }
}
