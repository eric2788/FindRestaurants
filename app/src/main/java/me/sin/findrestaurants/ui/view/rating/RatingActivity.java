package me.sin.findrestaurants.ui.view.rating;

import android.app.AlertDialog;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import me.sin.findrestaurants.R;
import me.sin.findrestaurants.ServiceLocator;
import me.sin.findrestaurants.data.RequestState;
import me.sin.findrestaurants.model.Comment;
import me.sin.findrestaurants.model.Rating;
import me.sin.findrestaurants.model.ViewListAction;
import me.sin.findrestaurants.service.AuthService;
import me.sin.findrestaurants.ui.view.comment.CommentAdapter;
import me.sin.findrestaurants.ui.view.comment.CommentCreateDialog;
import me.sin.findrestaurants.ui.view.comment.CommentViewModel;

public class RatingActivity extends AppCompatActivity {

    private RatingViewModel viewModel;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        int restId = getIntent().getIntExtra("rest_id", -1);
        if (restId == -1){
            Toast.makeText(this, "cannot find comments data", Toast.LENGTH_LONG).show();
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        AuthService authService = ServiceLocator.getService(AuthService.class);
        FloatingActionButton addFab = findViewById(R.id.add_rating_fab);
        if (authService.getUserId() != null){
            addFab.setVisibility(View.VISIBLE);
            addFab.setEnabled(true);
        }

        this.viewModel = new ViewModelProvider(this).get(RatingViewModel.class);
        this.viewModel.setRestId(restId);

        recyclerView = findViewById(R.id.rating_recycles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        refreshLayout = findViewById(R.id.rating_pull_refresh);
        refreshLayout.setOnRefreshListener(this::updateView);

        ProgressBar loadMore = findViewById(R.id.rest_rating_loading_more);

        this.viewModel.reset();
        this.viewModel.getViewList().observe(this, rating -> {
            RatingAdapter adapter = (RatingAdapter) recyclerView.getAdapter();
            if (rating.action == ViewListAction.Action.ADD && adapter != null){
                loadMore.setVisibility(View.GONE);
                adapter.addAll(rating.list);
            }else if (adapter != null){
                refreshLayout.setRefreshing(false);
                adapter.setAll(rating.list);
                findViewById(R.id.rating_empty_txt).setVisibility(rating.list.size() == 0 ? View.VISIBLE : View.GONE);
            }else{
                refreshLayout.setRefreshing(false);
                RatingAdapter ratingAdapter = new RatingAdapter(this, rating.list, (v, ratings) -> {
                    int pos = recyclerView.getChildAdapterPosition(v);
                    Rating rat = ratings.get(pos);
                    if (!rat.getAuthor().equals(authService.getUserId())){
                        Toast.makeText(this, "You are not the owner of the rating", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new AlertDialog.Builder(this)
                            .setTitle("Delete Rating")
                            .setMessage(R.string.delete_ensure)
                            .setIcon(R.drawable.ic_baseline_delete_24)
                            .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                            .setPositiveButton("Yes", (dialog, which) -> {
                                viewModel.deleteData(rat.getId());
                                dialog.dismiss();
                            }).show();
                });
                recyclerView.setAdapter(ratingAdapter);
                ratingAdapter.notifyDataSetChanged();
                findViewById(R.id.rating_empty_txt).setVisibility(rating.list.size() == 0 ? View.VISIBLE : View.GONE);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.getAdapter() == null || recyclerView.getAdapter().getItemCount() < 20)
                    return;
                if (!recyclerView.canScrollVertically(1)) {
                    loadMore.setVisibility(View.VISIBLE);
                    viewModel.loadMore();
                }
            }
        });

        viewModel.getMutationState().observe(this, state -> {
            if (state.success){
                setResult(RequestState.Result.SAVE_SUCCESS);
                Toast.makeText(this, "Save Success", Toast.LENGTH_SHORT).show();
                this.updateView();
            }else{
                Toast.makeText(this, state.errorMessage, Toast.LENGTH_LONG).show();
            }
        });

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RatingCreateDialog commentCreateDialog = new RatingCreateDialog(viewModel);
                commentCreateDialog.show(getSupportFragmentManager(), RatingCreateDialog.class.getSimpleName());
            }
        });
    }

    private void updateView(){
        refreshLayout.setRefreshing(true);
        RatingAdapter adapter = (RatingAdapter) recyclerView.getAdapter();
        if (adapter != null) adapter.clearAll();
        this.viewModel.reset();
    }
}