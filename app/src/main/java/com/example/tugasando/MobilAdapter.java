package com.example.tugasando;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

public class MobilAdapter extends ArrayAdapter<String> {

    private final ArrayList<String> fotoMobil;
    private final ImageLoader imageLoader;

    public MobilAdapter(Context context, ArrayList<String> daftarMobil, ArrayList<String> fotoMobil) {
        super(context, 0, daftarMobil);
        this.fotoMobil = fotoMobil;
        LruCache<String, Bitmap> cache = new LruCache<>(20);
        imageLoader = new ImageLoader(Volley.newRequestQueue(context.getApplicationContext()), new ImageLoader.ImageCache() {
            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_mobil, parent, false);
        }
        TextView textMobil = view.findViewById(R.id.text_mobil);
        NetworkImageView imageMobil = view.findViewById(R.id.image_mobil);
        textMobil.setText(getItem(position));
        imageMobil.setDefaultImageResId(android.R.drawable.ic_menu_gallery);
        imageMobil.setErrorImageResId(android.R.drawable.ic_menu_report_image);
        String url = fotoMobil.get(position);
        imageMobil.setImageUrl(url.startsWith("http://") || url.startsWith("https://") ? url : null, imageLoader);
        return view;
    }
}
