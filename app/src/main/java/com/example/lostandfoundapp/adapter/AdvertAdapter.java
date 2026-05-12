package com.example.lostandfoundapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lostandfoundapp.activities.DetailAdvertActivity;
import com.example.lostandfoundapp.R;
import com.example.lostandfoundapp.model.Advert;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdvertAdapter extends RecyclerView.Adapter<AdvertAdapter.AdvertViewHolder> {

    private final Context context;
    private final ArrayList<Advert> adverts;

    public AdvertAdapter(Context context, ArrayList<Advert> adverts) {
        this.context = context;
        this.adverts = adverts;
    }

    @NonNull
    @Override
    public AdvertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_advert, parent, false);
        return new AdvertViewHolder(view);
    }

    // Binds each advert to one of the items in the RecyclerView.
    @Override
    public void onBindViewHolder(@NonNull AdvertViewHolder holder, int position) {
        Advert advert = adverts.get(position);

        holder.tvTitle.setText(advert.getTitle());
        holder.tvCategory.setText(advert.getCategory());
        holder.tvLocation.setText(advert.getLocation());
        holder.tvDateTime.setText(advert.getDateTime());

        if (advert.getImageUri() != null && !advert.getImageUri().isEmpty()) {
            // Loads the saved image URI with Glide. A placeholder is shown if the image cannot be loaded.
            Glide.with(context)
                    .load(Uri.parse(advert.getImageUri()))
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .centerCrop()
                    .into(holder.ivAdvert);
        } else {
            holder.ivAdvert.setImageResource(android.R.drawable.ic_menu_gallery);
        }
        // Opens the detail screen for the selected advert and passes its database ID.
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailAdvertActivity.class);
            intent.putExtra("advert_id", advert.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return adverts.size();
    }

    public void updateData(ArrayList<Advert> newAdverts) {
        adverts.clear();
        adverts.addAll(newAdverts);
        notifyDataSetChanged();
    }

    public static class AdvertViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAdvert;
        TextView tvTitle, tvCategory, tvLocation, tvDateTime;

        public AdvertViewHolder(@NonNull View itemView) {
            super(itemView);

            ivAdvert = itemView.findViewById(R.id.ivAdvertItem);
            tvTitle = itemView.findViewById(R.id.tvAdvertTitle);
            tvCategory = itemView.findViewById(R.id.tvAdvertCategory);
            tvLocation = itemView.findViewById(R.id.tvAdvertLocation);
            tvDateTime = itemView.findViewById(R.id.tvAdvertDateTime);
        }
    }
}
