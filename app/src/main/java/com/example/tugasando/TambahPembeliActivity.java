package com.example.tugasando;

import android.os.Bundle;
import org.json.JSONObject;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class TambahPembeliActivity extends AppCompatActivity {

    private EditText etKtp, etNama, etAlamat, etNoHp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_data_pembeli);

        etKtp = findViewById(R.id.editktp);
        etNama = findViewById(R.id.editnama);
        etAlamat = findViewById(R.id.editalamat);
        etNoHp = findViewById(R.id.editnohp);
        Button btnSimpan = findViewById(R.id.tombolsimpan);

        btnSimpan.setOnClickListener(v -> {
            String ktp = etKtp.getText().toString().trim();
            String nama = etNama.getText().toString().trim();
            String alamat = etAlamat.getText().toString().trim();
            String noHp = etNoHp.getText().toString().trim();

            if (ktp.isEmpty() || nama.isEmpty() || alamat.isEmpty() || noHp.isEmpty()) {
                Toast.makeText(this, "Harap isi semua data", Toast.LENGTH_SHORT).show();
                return;
            }
            simpanData(ktp, nama, alamat, noHp);
        });
    }

    private void simpanData(String ktp, String nama, String alamat, String noHp) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Config.TAMBAH_PEMBELI,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.optBoolean("success", false)) {
                        Toast.makeText(this, json.optString("message", "Data pembeli berhasil disimpan"), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, json.optString("message", "Data pembeli gagal disimpan"), Toast.LENGTH_LONG).show();
                    }
                    } catch (Exception e) {
                        Toast.makeText(this, "Respon server tidak sesuai", Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Gagal menyimpan data pembeli", Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("ktp", ktp);
                params.put("nama_pembeli", nama);
                params.put("alamat_pembeli", alamat);
                params.put("telp_pembeli", noHp);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }
}
