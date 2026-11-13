package com.example.tugasando;

import android.content.Intent;
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

public class EditMobilActivity extends AppCompatActivity {

    private EditText editkode, editmerk, edittype, editwarna, editharga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mobil);

        editkode = findViewById(R.id.editkode);
        editmerk = findViewById(R.id.editmerk);
        edittype = findViewById(R.id.edittype);
        editwarna = findViewById(R.id.editwarna);
        editharga = findViewById(R.id.editharga);
        Button tombolupdate = findViewById(R.id.tombolupdate);

        Intent intent = getIntent();
        editkode.setText(intent.getStringExtra("kode_mobil"));
        editmerk.setText(intent.getStringExtra("merk_mobil"));
        edittype.setText(intent.getStringExtra("type_mobil"));
        editwarna.setText(intent.getStringExtra("warna_mobil"));
        editharga.setText(intent.getStringExtra("harga_mobil"));
        editkode.setEnabled(false);

        tombolupdate.setOnClickListener(view -> updateMobil());
    }

    private void updateMobil() {
        String kodeMobil = editkode.getText().toString().trim();
        String merkMobil = editmerk.getText().toString().trim();
        String typeMobil = edittype.getText().toString().trim();
        String warnaMobil = editwarna.getText().toString().trim();
        String hargaMobil = editharga.getText().toString().trim();

        if (merkMobil.isEmpty() || typeMobil.isEmpty() || warnaMobil.isEmpty() || hargaMobil.isEmpty()) {
            Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Config.EDIT_MOBIL,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.optBoolean("success", false)) {
                        Toast.makeText(this, json.optString("message", "Data berhasil diupdate"), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, json.optString("message", "Gagal mengupdate data"), Toast.LENGTH_LONG).show();
                    }
                    } catch (Exception e) {
                        Toast.makeText(this, "Respon server tidak sesuai", Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Gagal mengupdate data mobil", Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("kode_mobil", kodeMobil);
                params.put("merk_mobil", merkMobil);
                params.put("type_mobil", typeMobil);
                params.put("warna_mobil", warnaMobil);
                params.put("harga_mobil", hargaMobil);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}
