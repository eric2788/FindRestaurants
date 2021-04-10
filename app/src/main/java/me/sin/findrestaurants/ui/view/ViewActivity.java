package me.sin.findrestaurants.ui.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.logging.Logger;

import me.sin.findrestaurants.R;
import me.sin.findrestaurants.ServiceLocator;
import me.sin.findrestaurants.data.RequestState;
import me.sin.findrestaurants.model.Comment;
import me.sin.findrestaurants.model.Restaurant;
import me.sin.findrestaurants.model.RestaurantView;
import me.sin.findrestaurants.service.AuthService;
import me.sin.findrestaurants.service.Base64Service;
import me.sin.findrestaurants.service.DataTransferService;
import me.sin.findrestaurants.ui.update.UpdateAcitivty;
import me.sin.findrestaurants.ui.view.comment.CommentActivity;
import me.sin.findrestaurants.ui.view.rating.RatingActivity;
import me.sin.findrestaurants.ui.view.rating.RatingAdapter;

public class ViewActivity extends AppCompatActivity {

    private AuthService authService;
    private RestaurantViewModel restaurantViewModel;
    private Base64Service base64Service;
    private DataTransferService dataTransferService;
    private Restaurant viewing = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        setSupportActionBar(findViewById(R.id.toolbar));

        this.restaurantViewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);

        this.authService = ServiceLocator.getService(AuthService.class);
        this.base64Service = ServiceLocator.getService(Base64Service.class);
        this.dataTransferService = ServiceLocator.getService(DataTransferService.class);

        RestaurantView preRead = dataTransferService.getData("toView");

        if (preRead == null){
            Logger.getGlobal().info("preRead is null");
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        TextView title = findViewById(R.id.view_title);
        TextView address = findViewById(R.id.view_address);
        TextView category = findViewById(R.id.view_category);
        TextView phone = findViewById(R.id.view_phone);
        TextView website = findViewById(R.id.view_website);
        ImageView photo = findViewById(R.id.view_photo);
        TextView joined = findViewById(R.id.view_joined_date);

        TextView commentCount = findViewById(R.id.view_comments_count);
        TextView totalRating = findViewById(R.id.view_total_rating);


        this.reloadRestaurantShow(preRead);

        FloatingActionButton editButton = findViewById(R.id.edit_rest_fab);
        FloatingActionButton deleteButton = findViewById(R.id.delete_rest_fab);


        restaurantViewModel.getRestaurantMutableLiveData().observe(this, state -> {
            Restaurant restaurant = state.getRestaurant();
            if (restaurant == null){
                Toast.makeText(ViewActivity.this, "Cannot find restaurant data: ".concat(state.getErrorMessage() == null ? "null" : state.getErrorMessage()), Toast.LENGTH_LONG).show();
                setResult(RESULT_CANCELED);
                finish();
                return;
            }
            findViewById(R.id.view_loading).setVisibility(View.GONE);
            if (restaurant.getOwner().equals(authService.getUserId())){
                editButton.setVisibility(View.VISIBLE);
                editButton.show();
                editButton.setEnabled(true);
                deleteButton.setVisibility(View.VISIBLE);
                deleteButton.show();
                deleteButton.setEnabled(true);
            }
            title.setText(restaurant.getTitle());
            address.setText(restaurant.getAddress());
            website.setText(restaurant.getWebsite());
            category.setText(restaurant.getCategory());
            phone.setText(String.valueOf(restaurant.getPhone()));
            String joinDateTxt = SimpleDateFormat.getDateInstance().format(restaurant.getJoinDate())+" joined";
            joined.setText(joinDateTxt);
            Bitmap bitmap = base64Service.convertString64ToImage(restaurant.getImageBase64());
            photo.setImageBitmap(bitmap);

            final String commentTxt = restaurant.getTotalComments() + " Comments";
            commentCount.setText(commentTxt);
            final String ratingTxt =  "Rating " + restaurant.getTotalRating();
            totalRating.setText(ratingTxt);

            viewing = restaurant;
        });


        editButton.setOnClickListener(view -> {
            if (viewing == null){
                Toast.makeText(ViewActivity.this, "Data not loaded", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            Intent intent = new Intent(ViewActivity.this, UpdateAcitivty.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            dataTransferService.putData("toEdit", viewing);
            startActivityForResult(intent, RequestState.EDIT_REQUEST);
        });

        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete This Restaurant")
                    .setMessage(R.string.delete_ensure)
                    .setIcon(R.drawable.ic_baseline_delete_24)
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Yes", (dialog, which) -> {
                        restaurantViewModel.deleteData(viewing.getId());
                        dialog.dismiss();
                    }).show();
        });

        restaurantViewModel.getMutationState().observe(this, state -> {
            if (state.success){
                setResult(RequestState.Result.SAVE_SUCCESS);
                Toast.makeText(this, "Delete Success", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(this, state.errorMessage, Toast.LENGTH_LONG).show();
            }
        });


    }

    private boolean changed = false;

    @Override
    public void onBackPressed() {
        if (changed){
            setResult(RequestState.Result.SAVE_SUCCESS);
            finish();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RequestState.Result.SAVE_SUCCESS){
            Logger.getGlobal().info("received update, reloading");
            if (viewing != null){
                this.changed = true;
                this.reloadRestaurantShow(viewing);
            }
        }
    }

    private void reloadRestaurantShow(RestaurantView view){
        findViewById(R.id.view_loading).setVisibility(View.VISIBLE);
        restaurantViewModel.readRestaurant(view);
    }

    public void onRatingClick(View view) {
        Intent intent = new Intent(this, RatingActivity.class);
        intent.putExtra("rest_id", viewing.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityForResult(intent, RequestState.RATINGS_VIEW_REQUEST);
    }

    public void onCommentsClick(View view) {
        Intent intent = new Intent(this, CommentActivity.class);
        intent.putExtra("rest_id", viewing.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityForResult(intent, RequestState.COMMENTS_VIEW_REQUEST);
    }
}