package com.example.tugasando;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.Locale;
import java.util.Map;

public class TambahCicilanActivity extends AppCompatActivity {

    private EditText editKodeCicilan, editTanggal, editCicilKe;
    private EditText editBesarCicilan, editSisaCicilKe, editSisa;
    private Spinner spinnerKodeKredit;
    private Button tombolSimpan;

    private final ArrayList<String> daftarKredit = new ArrayList<>();
    private final ArrayList<JSONObject> dataKredit = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_bayar_cicilan);

        editKodeCicilan = findViewById(R.id.editkodecicilan);
        spinnerKodeKredit = findViewById(R.id.spinnerkodekredit);
        editTanggal = findViewById(R.id.edittanggal);
        editCicilKe = findViewById(R.id.editcicilke);
        editBesarCicilan = findViewById(R.id.editbesarcicilan);
        editSisaCicilKe = findViewById(R.id.editsisacicilke);
        editSisa = findViewById(R.id.editsisa);
        tombolSimpan = findViewById(R.id.tombolsimpan);
        tombolSimpan.setEnabled(false);

        editTanggal.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(
                    this,
                    (view, year, month, day) -> editTanggal.setText(
                            String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        spinnerKodeKredit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isiOtomatis(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                tombolSimpan.setEnabled(false);
            }
        });

        tombolSimpan.setOnClickListener(v -> simpanDataCicilan());
        loadKredit();
    }

    private void loadKredit() {
        StringRequest request = new StringRequest(
                Request.Method.GET,
                Config.TAMPIL_KREDIT_HITUNG,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        daftarKredit.clear();
                        dataKredit.clear();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject item = array.getJSONObject(i);
                            String kode = item.optString("kode_kredit");
                            if (!kode.isEmpty()) {
                                daftarKredit.add(kode);
                                dataKredit.add(item);
                            }
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this, android.R.layout.simple_spinner_item, daftarKredit);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerKodeKredit.setAdapter(adapter);

                        if (daftarKredit.isEmpty()) {
                            Toast.makeText(this, "Data kredit belum tersedia", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Format data kredit tidak sesuai", Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Gagal memuat perhitungan kredit", Toast.LENGTH_LONG).show()
        );
        Volley.newRequestQueue(this).add(request);
    }

    private void isiOtomatis(int posisi) {
        if (posisi < 0 || posisi >= dataKredit.size()) {
            tombolSimpan.setEnabled(false);
            return;
        }

        JSONObject item = dataKredit.get(posisi);
        int tenor = item.optInt("tenor", 0);
        int sisaKe = item.optInt("sisa_ke", 0);

        editCicilKe.setText(item.optString("cicilan_ke"));
        editBesarCicilan.setText(item.optString("bayar_kredit"));
        editSisaCicilKe.setText(item.optString("sisa_ke"));
        editSisa.setText(item.optString("sisa_harga"));

        tombolSimpan.setEnabled(tenor > 0 && sisaKe > 0);
        if (tenor <= 0) {
            Toast.makeText(this, "Tenor paket kredit tidak ditemukan", Toast.LENGTH_LONG).show();
        } else if (sisaKe <= 0) {
            Toast.makeText(this, "Cicilan kredit ini sudah lunas", Toast.LENGTH_LONG).show();
        }
    }

    private void simpanDataCicilan() {
        String kodeCicilan = editKodeCicilan.getText().toString().trim();
        String tanggal = editTanggal.getText().toString().trim();
        String cicilanKe = editCicilKe.getText().toString().trim();
        String jumlah = editBesarCicilan.getText().toString().trim();
        String sisaCicilKe = editSisaCicilKe.getText().toString().trim();
        String sisa = editSisa.getText().toString().trim();
        Object selected = spinnerKodeKredit.getSelectedItem();
        String kodeKredit = selected == null ? "" : selected.toString();

        if (kodeCicilan.isEmpty() || kodeKredit.isEmpty() || tanggal.isEmpty()
                || cicilanKe.isEmpty() || jumlah.isEmpty()
                || sisaCicilKe.isEmpty() || sisa.isEmpty()) {
            Toast.makeText(this, "Harap lengkapi semua kolom", Toast.LENGTH_SHORT).show();
            return;
        }

        tombolSimpan.setEnabled(false);
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Config.TAMBAH_CICILAN,
                response -> {
                    tombolSimpan.setEnabled(true);
                    try {
                        JSONObject hasil = new JSONObject(response);
                        boolean sukses = hasil.optBoolean("success");
                        String pesan = hasil.optString("message");
                        Toast.makeText(this, pesan, Toast.LENGTH_LONG).show();
                        if (sukses) {
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Respon server tidak sesuai", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    tombolSimpan.setEnabled(true);
                    Toast.makeText(this, "Gagal mengirim data ke server", Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("kode_cicilan", kodeCicilan);
                params.put("kode_kredit", kodeKredit);
                params.put("tanggal_cicilan", tanggal);
                params.put("cicilanke", cicilanKe);
                params.put("jumlah_cicilan", jumlah);
                params.put("sisacicilke", sisaCicilKe);
                params.put("sisa_cicilan", sisa);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }
}
