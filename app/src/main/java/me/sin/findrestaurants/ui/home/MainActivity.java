package me.sin.findrestaurants.ui.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.logging.Logger;

import me.sin.findrestaurants.R;
import me.sin.findrestaurants.ServiceLocator;
import me.sin.findrestaurants.data.LoginRepository;
import me.sin.findrestaurants.data.RequestState;
import me.sin.findrestaurants.model.RestaurantView;
import me.sin.findrestaurants.model.ViewListAction;
import me.sin.findrestaurants.service.AuthService;
import me.sin.findrestaurants.service.DataTransferService;
import me.sin.findrestaurants.service.DatabaseService;
import me.sin.findrestaurants.ui.login.AuthActivity;
import me.sin.findrestaurants.ui.update.UpdateAcitivty;
import me.sin.findrestaurants.ui.view.ViewActivity;

public class MainActivity extends AppCompatActivity {

    private AuthService authService;
    private RestaurantsViewModel restaurantsViewModel;
    private RecyclerView recyclerView;
    private DataTransferService dataTransferService;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DatabaseService databaseService = ServiceLocator.getService(DatabaseService.class);
        databaseService.initializeDatabase(this);
        this.authService = ServiceLocator.getService(AuthService.class);
        this.dataTransferService = ServiceLocator.getService(DataTransferService.class);

        restaurantsViewModel = new ViewModelProvider(this).get(RestaurantsViewModel.class);
        restaurantsViewModel.setDefaultCategories(getResources().getStringArray(R.array.category_list));

        this.recyclerView = findViewById(R.id.recycle_rests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        refreshLayout = findViewById(R.id.rest_list_pull_refresh);
        refreshLayout.setOnRefreshListener(this::updateView);

        ProgressBar loadMore = findViewById(R.id.rest_list_loading_more);

        this.updateView();
        restaurantsViewModel.getViewList().observe(this, restaurantViews -> {
            RestaurantAdapter recyclerViewAdapter = (RestaurantAdapter) recyclerView.getAdapter();
            if (restaurantViews.action == ViewListAction.Action.ADD && recyclerViewAdapter != null) {
                loadMore.setVisibility(View.GONE);
                recyclerViewAdapter.addAll(restaurantViews.list);
            } else if (recyclerViewAdapter != null) {
                refreshLayout.setRefreshing(false);
                findViewById(R.id.empty_hints).setVisibility(restaurantViews.list.size() == 0 ? View.VISIBLE : View.GONE);
                recyclerViewAdapter.setAll(restaurantViews.list);
            } else {
                refreshLayout.setRefreshing(false);
                findViewById(R.id.empty_hints).setVisibility(restaurantViews.list.size() == 0 ? View.VISIBLE : View.GONE);
                RestaurantAdapter adapter = new RestaurantAdapter(this, restaurantViews.list, (v, list) -> {
                    int pos = recyclerView.getChildAdapterPosition(v);
                    RestaurantView restaurantView = list.get(pos);
                    Intent intent = new Intent(MainActivity.this, ViewActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    dataTransferService.putData("toView", restaurantView);
                    startActivityForResult(intent, RequestState.VIEW_REQUEST);
                });
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.getAdapter() == null || recyclerView.getAdapter().getItemCount() < 20)
                    return;
                if (!recyclerView.canScrollVertically(1)) {
                    Logger.getGlobal().info("loading more!");
                    loadMore.setVisibility(View.VISIBLE);
                    restaurantsViewModel.loadMore();
                }
            }
        });

        SearchView search = findViewById(R.id.search_rest);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String q = query.trim();
                Logger.getGlobal().info("Searching with query: "+q);
                restaurantsViewModel.setQuery(q);
                updateView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        search.setOnCloseListener(() -> {
            restaurantsViewModel.setQuery("");
            updateView();
            return false;
        });

        FloatingActionButton addIcon = findViewById(R.id.add_rest_btn);

        addIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UpdateAcitivty.class);
            intent.putExtra("create", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityForResult(intent, RequestState.EDIT_REQUEST);
        });

        restaurantsViewModel.getCategories().observe(this, cate -> updateView());


        // ========= Unpaid Section ===========
        // remove after paid

        new AlertDialog.Builder(this)
                .setTitle("你正在使用未付費版本。")
                .setMessage("製作者: Eric Lam \n網址: github.com/eric2788\n應繳費用: 900HKD")
                .setNegativeButton("知道了", (d, x)-> d.dismiss())
                .show();

        //=====================================

    }


    public void onFilterClick(MenuItem item) {
        FilterBottomSheet bottomSheet = new FilterBottomSheet(restaurantsViewModel);
        bottomSheet.show(getSupportFragmentManager(), FilterBottomSheet.class.getSimpleName());
    }

    private Menu menu;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RequestState.LOGIN_REQUEST && resultCode == RequestState.Result.LOGIN_SUCCESS) {
            //this.updateView();
            this.updateLoginView();
        } //else if (requestCode == RequestState.EDIT_REQUEST && resultCode == RequestState.Result.SAVE_SUCCESS){
        //this.updateView();
        //}
        this.updateView();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateView() {
        refreshLayout.setRefreshing(true);
        RestaurantAdapter adapter = ((RestaurantAdapter) recyclerView.getAdapter());
        if (adapter != null) adapter.clearAll();
        restaurantsViewModel.reset();
    }

    private void updateLoginView() {
        if (authService.getUserId() != null) {
            findViewById(R.id.add_rest_btn).setVisibility(View.VISIBLE);
            if (menu != null) {
                menu.findItem(R.id.action_login).setTitle("Logout");
            }
        } else {
            findViewById(R.id.add_rest_btn).setVisibility(View.GONE);
            if (menu != null) {
                menu.findItem(R.id.action_login).setTitle("Login");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void onLoginClick(MenuItem item) {
        if (authService.getUserId() == null) {
            Intent intent = new Intent(this, AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityForResult(intent, RequestState.LOGIN_REQUEST);
        } else {
            LoginRepository.getInstance().logout();
            this.updateLoginView();
            Toast.makeText(this, "Logout success", Toast.LENGTH_SHORT).show();
        }
    }
}