package com.example.tugasando;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TambahMobilActivity extends AppCompatActivity {

    private EditText editKodeMobil, editMerek, editTipe, editWarna, editHarga;
    private ImageView imagePreview;
    private String encodedImage = "";
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_mobil);

        editKodeMobil = findViewById(R.id.edit_kode_mobil);
        editMerek = findViewById(R.id.edit_merek);
        editTipe = findViewById(R.id.edit_tipe);
        editWarna = findViewById(R.id.edit_warna);
        editHarga = findViewById(R.id.edit_harga);
        imagePreview = findViewById(R.id.image_preview);
        Button tombolPilihGambar = findViewById(R.id.tombol_pilih_gambar);
        Button tombolSimpanMobil = findViewById(R.id.tombol_simpan_mobil);

        tombolPilihGambar.setOnClickListener(v -> pilihGambar());
        tombolSimpanMobil.setOnClickListener(v -> simpanDataMobil());
    }

    private void pilihGambar() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (bitmap == null) {
                    Toast.makeText(this, "Gambar tidak dapat dibaca", Toast.LENGTH_SHORT).show();
                    return;
                }
                int lebar = bitmap.getWidth();
                int tinggi = bitmap.getHeight();
                if (lebar > 1200 || tinggi > 1200) {
                    float skala = Math.min(1200f / lebar, 1200f / tinggi);
                    Bitmap hasil = Bitmap.createScaledBitmap(bitmap, Math.round(lebar * skala), Math.round(tinggi * skala), true);
                    bitmap.recycle();
                    bitmap = hasil;
                }
                imagePreview.setImageBitmap(bitmap);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                encodedImage = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP);
            } catch (Exception e) {
                Toast.makeText(this, "Gagal memilih gambar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void simpanDataMobil() {
        String kode = editKodeMobil.getText().toString().trim();
        String merek = editMerek.getText().toString().trim();
        String tipe = editTipe.getText().toString().trim();
        String warna = editWarna.getText().toString().trim();
        String harga = editHarga.getText().toString().trim();

        if (kode.isEmpty() || merek.isEmpty() || tipe.isEmpty() || warna.isEmpty() || harga.isEmpty() || encodedImage.isEmpty()) {
            Toast.makeText(this, "Harap isi semua kolom dan pilih gambar", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Config.TAMBAH_MOBIL,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.optBoolean("success", false)) {
                            Toast.makeText(this, json.optString("message", "Data mobil berhasil disimpan"), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, json.optString("message", "Data mobil gagal disimpan"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Respon server tidak sesuai", Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Gagal menyimpan data mobil", Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("kode_mobil", kode);
                params.put("merk", merek);
                params.put("type", tipe);
                params.put("warna", warna);
                params.put("harga", harga);
                params.put("foto", encodedImage);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000, 0, 1.0f));
        stringRequest.setShouldCache(false);
        Volley.newRequestQueue(this).add(stringRequest);
    }
}
