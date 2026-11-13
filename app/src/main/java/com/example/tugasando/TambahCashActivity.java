package com.example.tugasando;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TambahCashActivity extends AppCompatActivity {

    private EditText editKodeCash, editTanggalCash, editBayarCash;
    private Spinner spinnerKtpCash, spinnerMobilCash;

    private final List<String> listPembeli = new ArrayList<>();
    private final List<String> listMobil = new ArrayList<>();
    private final List<String> idPembeli = new ArrayList<>();
    private final List<String> idMobil = new ArrayList<>();
    private final List<String> hargaMobil = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_beli_cash);

        editKodeCash = findViewById(R.id.edit_kode_cash);
        editTanggalCash = findViewById(R.id.edit_cash_tgl);
        editBayarCash = findViewById(R.id.edit_cash_bayar);
        spinnerKtpCash = findViewById(R.id.spinner_pembeli);
        spinnerMobilCash = findViewById(R.id.spinner_mobil);
        Button tombolSimpanCash = findViewById(R.id.btn_simpan);

        editTanggalCash.setOnClickListener(v -> showDatePickerDialog());
        tombolSimpanCash.setOnClickListener(v -> simpanDataCash());

        spinnerMobilCash.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                if (position >= 0 && position < hargaMobil.size() && editBayarCash.getText().toString().trim().isEmpty()) {
                    editBayarCash.setText(hargaMobil.get(position));
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        loadPembeli();
        loadMobil();
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(
                this,
                (view, year, month, day) -> editTanggalCash.setText(
                        String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void loadPembeli() {
        StringRequest request = new StringRequest(Request.Method.GET, Config.TAMPIL_PEMBELI,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        listPembeli.clear();
                        idPembeli.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject data = jsonArray.getJSONObject(i);
                            String ktp = data.optString("ktp");
                            String nama = data.optString("nama_pembeli");
                            idPembeli.add(ktp);
                            listPembeli.add(nama + " (" + ktp + ")");
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listPembeli);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerKtpCash.setAdapter(adapter);
                    } catch (Exception e) {
                        Toast.makeText(this, "Gagal membaca data pembeli", Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Gagal memuat data pembeli", Toast.LENGTH_LONG).show());
        Volley.newRequestQueue(this).add(request);
    }

    private void loadMobil() {
        StringRequest request = new StringRequest(Request.Method.GET, Config.TAMPIL_MOBIL,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        listMobil.clear();
                        idMobil.clear();
                        hargaMobil.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject data = jsonArray.getJSONObject(i);
                            String kode = data.optString("kode_mobil");
                            String nama = data.optString("merk");
                            String harga = data.optString("harga");
                            idMobil.add(kode);
                            hargaMobil.add(harga);
                            listMobil.add(nama + " - Rp " + harga);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listMobil);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerMobilCash.setAdapter(adapter);
                    } catch (Exception e) {
                        Toast.makeText(this, "Gagal membaca data mobil", Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Gagal memuat data mobil", Toast.LENGTH_LONG).show());
        Volley.newRequestQueue(this).add(request);
    }

    private void simpanDataCash() {
        String kode = editKodeCash.getText().toString().trim();
        String tanggal = editTanggalCash.getText().toString().trim();
        String bayar = editBayarCash.getText().toString().trim();
        int posPembeli = spinnerKtpCash.getSelectedItemPosition();
        int posMobil = spinnerMobilCash.getSelectedItemPosition();

        if (kode.isEmpty() || tanggal.isEmpty() || bayar.isEmpty()
                || posPembeli < 0 || posPembeli >= idPembeli.size()
                || posMobil < 0 || posMobil >= idMobil.size()) {
            Toast.makeText(this, "Harap isi semua kolom dan pilih pembeli serta mobil", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.TAMBAH_CASH,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.optBoolean("success", false)) {
                        Toast.makeText(this, json.optString("message", "Data cash berhasil disimpan"), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, json.optString("message", "Data cash gagal disimpan"), Toast.LENGTH_LONG).show();
                    }
                    } catch (Exception e) {
                        Toast.makeText(this, "Respon server tidak sesuai", Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Gagal menyimpan data cash", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("kode_cash", kode);
                params.put("ktp", idPembeli.get(posPembeli));
                params.put("kode_mobil", idMobil.get(posMobil));
                params.put("cash_bayar", bayar);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }
}
