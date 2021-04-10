package me.sin.findrestaurants.ui.view.comment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.logging.Logger;

import me.sin.findrestaurants.R;
import me.sin.findrestaurants.ServiceLocator;
import me.sin.findrestaurants.data.RequestState;
import me.sin.findrestaurants.model.Comment;
import me.sin.findrestaurants.model.RestaurantView;
import me.sin.findrestaurants.model.ViewListAction;
import me.sin.findrestaurants.service.AuthService;
import me.sin.findrestaurants.service.DataTransferService;

public class CommentActivity extends AppCompatActivity {

    private CommentViewModel viewModel;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        int restId = getIntent().getIntExtra("rest_id", -1);
        if (restId == -1){
            Toast.makeText(this, "cannot find comments data", Toast.LENGTH_LONG).show();
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        AuthService authService = ServiceLocator.getService(AuthService.class);

        FloatingActionButton writeComments = findViewById(R.id.rest_comment_fab);


        if (authService.getUserId() != null){
            writeComments.setVisibility(View.VISIBLE);
            writeComments.setEnabled(true);
        }

        this.viewModel = new ViewModelProvider(this).get(CommentViewModel.class);
        this.viewModel.setRestId(restId);

        recyclerView = findViewById(R.id.comment_recycles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        refreshLayout = findViewById(R.id.comment_pull_refresh);
        refreshLayout.setOnRefreshListener(this::updateView);

        ProgressBar loadMore = findViewById(R.id.rest_comment_loading_more);

        this.viewModel.reset();
        this.viewModel.getViewList().observe(this, comment -> {
            CommentAdapter adapter = (CommentAdapter) recyclerView.getAdapter();
            if (comment.action == ViewListAction.Action.ADD && adapter != null){
                loadMore.setVisibility(View.GONE);
                adapter.addAll(comment.list);
            }else if (adapter != null){
                refreshLayout.setRefreshing(false);
                adapter.setAll(comment.list);
                findViewById(R.id.comment_empty_txt).setVisibility(comment.list.size() == 0 ? View.VISIBLE : View.GONE);
            }else{
                refreshLayout.setRefreshing(false);
                CommentAdapter commentAdapter = new CommentAdapter(this, comment.list, (v, comments) -> {
                    int pos = recyclerView.getChildAdapterPosition(v);
                    Comment cmt = comments.get(pos);
                    if (!cmt.getAuthor().equals(authService.getUserId())){
                        Toast.makeText(this, "You are not the owner of the comment", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new AlertDialog.Builder(this)
                            .setTitle("Delete Comment")
                            .setMessage(R.string.delete_ensure)
                            .setIcon(R.drawable.ic_baseline_delete_24)
                            .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                            .setPositiveButton("Yes", (dialog, which) -> {
                                viewModel.deleteData(cmt.getId());
                                dialog.dismiss();
                            }).show();
                });
                recyclerView.setAdapter(commentAdapter);
                commentAdapter.notifyDataSetChanged();
                findViewById(R.id.comment_empty_txt).setVisibility(comment.list.size() == 0 ? View.VISIBLE : View.GONE);
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

        writeComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentCreateDialog commentCreateDialog = new CommentCreateDialog(viewModel);
                commentCreateDialog.show(getSupportFragmentManager(), CommentCreateDialog.class.getSimpleName());
            }
        });
    }

    private void updateView(){
        refreshLayout.setRefreshing(true);
        CommentAdapter adapter = (CommentAdapter)recyclerView.getAdapter();
        if (adapter != null) adapter.clearAll();
        this.viewModel.reset();
    }
}