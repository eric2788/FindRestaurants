package me.sin.findrestaurants.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.function.BiConsumer;

import me.sin.findrestaurants.R;
import me.sin.findrestaurants.ServiceLocator;
import me.sin.findrestaurants.model.RestaurantView;
import me.sin.findrestaurants.service.Base64Service;
import me.sin.findrestaurants.ui.view.ViewActivity;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private final Context context;
    private final List<RestaurantView> restaurantViews;
    private final Base64Service base64Service;
    private final View.OnClickListener clickListener;

    public RestaurantAdapter(Context context, List<RestaurantView> restaurantViews, BiConsumer<View, List<RestaurantView>> clickListener) {
        this.context = context;
        this.restaurantViews = restaurantViews;
        this.clickListener = v -> clickListener.accept(v, this.restaurantViews);
        this.base64Service = ServiceLocator.getService(Base64Service.class);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycleview_rests, parent, false);
        view.setOnClickListener(clickListener);
        return new ViewHolder(view);
    }

    public void addAll(List<RestaurantView> restaurantViews){
        this.restaurantViews.addAll(restaurantViews);
        this.notifyDataSetChanged();
    }

    public void clearAll(){
        this.restaurantViews.clear();
        this.notifyDataSetChanged();
    }

    public void setAll(List<RestaurantView> restaurantViews){
        this.restaurantViews.clear();
        this.restaurantViews.addAll(restaurantViews);
        this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final RestaurantView restaurantView = restaurantViews.get(position);
        if (restaurantView.getImageBase64() != null){
            holder.imageView.setImageBitmap(base64Service.convertString64ToImage(restaurantView.getImageBase64()));
        }else{
            holder.imageView.setImageBitmap(null);
        }
        holder.title.setText(restaurantView.getTitle());
        holder.ratingBar.setRating(restaurantView.getTotalRating());
        holder.category.setText(restaurantView.getCategory());
    }



    @Override
    public int getItemCount() {
        return restaurantViews.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final ImageView imageView;
        final TextView title;
        final RatingBar ratingBar;
        final TextView category;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_rest);
            title = itemView.findViewById(R.id.rest_title);
            ratingBar = itemView.findViewById(R.id.rest_rating);
            category = itemView.findViewById(R.id.rest_cate);
        }
    }
}
