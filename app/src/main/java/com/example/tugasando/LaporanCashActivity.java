package com.example.tugasando;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class LaporanCashActivity extends AppCompatActivity {
    Spinner spinnerPeriode;
    EditText etTanggal;
    ListView listLaporan;
    ArrayList<String> listData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.laporanjualcash);

        spinnerPeriode = findViewById(R.id.spinnerpilihan);
        etTanggal = findViewById(R.id.edittanggal);
        listLaporan = findViewById(R.id.listcash);

        String[] periode = {"Bulanan", "Tahunan"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, periode);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPeriode.setAdapter(adapter);

        Calendar sekarang = Calendar.getInstance();
        etTanggal.setText(String.format(Locale.US, "%04d-%02d", sekarang.get(Calendar.YEAR), sekarang.get(Calendar.MONTH) + 1));

        etTanggal.setOnClickListener(v -> showDatePicker());
        spinnerPeriode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tanggal = etTanggal.getText().toString();
                if (position == 1 && tanggal.length() >= 4) {
                    etTanggal.setText(tanggal.substring(0, 4));
                } else if (position == 0 && tanggal.length() == 4) {
                    etTanggal.setText(tanggal + "-" + String.format(Locale.US, "%02d", Calendar.getInstance().get(Calendar.MONTH) + 1));
                }
                loadData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void showDatePicker() {
        Calendar kalender = Calendar.getInstance();
        String isi = etTanggal.getText().toString();
        if (isi.length() >= 4) {
            try {
                kalender.set(Calendar.YEAR, Integer.parseInt(isi.substring(0, 4)));
                if (isi.length() >= 7) {
                    kalender.set(Calendar.MONTH, Integer.parseInt(isi.substring(5, 7)) - 1);
                }
            } catch (NumberFormatException e) {
            }
        }
        DatePickerDialog dialog = new DatePickerDialog(this, (view, tahun, bulan, hari) -> {
            if (spinnerPeriode.getSelectedItemPosition() == 1) {
                etTanggal.setText(String.valueOf(tahun));
            } else {
                etTanggal.setText(String.format(Locale.US, "%04d-%02d", tahun, bulan + 1));
            }
            loadData();
        }, kalender.get(Calendar.YEAR), kalender.get(Calendar.MONTH), kalender.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void loadData() {
        String periode = spinnerPeriode.getSelectedItemPosition() == 1 ? "tahun" : "bulan";
        String tanggal = etTanggal.getText().toString().trim();
        if (tanggal.isEmpty()) return;

        String url = Config.LAPORAN_CASH + "?periode=" + periode + "&tanggal=" + tanggal;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray arr = new JSONArray(response);
                        listData.clear();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String tampil = "Kode Cash: " + obj.optString("kode_cash") + "\n" +
                            "Pembeli: " + obj.optString("nama_pembeli") + "\n" +
                            "Mobil: " + obj.optString("merk") + " " + obj.optString("type") + "\n" +
                            "Tanggal: " + obj.optString("cash_tanggal") + "\n" +
                            "Bayar: " + obj.optString("cash_bayar");
                            listData.add(tampil);
                        }
                        if (listData.isEmpty()) {
                            listData.add("Tidak ada data laporan");
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
                        listLaporan.setAdapter(adapter);
                    } catch (Exception e) {
                        Toast.makeText(this, "Gagal memproses data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Gagal mengambil laporan", Toast.LENGTH_SHORT).show());
        Volley.newRequestQueue(this).add(request);
    }
}
