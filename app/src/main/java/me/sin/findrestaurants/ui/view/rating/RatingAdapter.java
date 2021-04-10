package me.sin.findrestaurants.ui.view.rating;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.function.BiConsumer;

import me.sin.findrestaurants.R;
import me.sin.findrestaurants.model.Rating;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.ViewHolder> {


    private final Context context;
    private final List<Rating> ratings;
    private final BiConsumer<View, List<Rating>> clickListener;

    public RatingAdapter(Context context, List<Rating> ratings, BiConsumer<View, List<Rating>> clickListener) {
        this.context = context;
        this.ratings = ratings;
        this.clickListener = clickListener;
    }

    public void addAll(List<Rating> ratings) {
        this.ratings.addAll(ratings);
        this.notifyDataSetChanged();
    }

    public void clearAll() {
        this.ratings.clear();
        this.notifyDataSetChanged();
    }

    public void setAll(List<Rating> ratings) {
        this.ratings.clear();
        this.ratings.addAll(ratings);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_ratings, parent, false);
        view.setOnLongClickListener(v -> {
            this.clickListener.accept(v, ratings);
            return true;
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Rating rating = ratings.get(position);
        holder.user.setText(rating.getAuthor());
        holder.rated.setText(String.format("Rated %s", rating.getRating()));
        holder.review.setText(rating.getReview());
    }

    @Override
    public int getItemCount() {
        return ratings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView user;
        final TextView rated;
        final TextView review;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.user = itemView.findViewById(R.id.rating_user);
            this.rated = itemView.findViewById(R.id.rating_rated);
            this.review = itemView.findViewById(R.id.rating_review);
        }
    }
}
