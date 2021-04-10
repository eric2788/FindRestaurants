package me.sin.findrestaurants.ui.home;

import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.logging.Logger;

import me.sin.findrestaurants.ServiceLocator;
import me.sin.findrestaurants.model.ListViewModel;
import me.sin.findrestaurants.model.RestaurantView;
import me.sin.findrestaurants.service.RestaurantService;

public class RestaurantsViewModel extends ListViewModel<RestaurantView> {

    private final RestaurantService restaurantService = ServiceLocator.getService(RestaurantService.class);
    private final MutableLiveData<String[]> categories = new MutableLiveData<>(new String[0]);

    private String[] defaultCategories = new String[0];

    public String[] getDefaultCategories() {
        return defaultCategories;
    }

    public void setDefaultCategories(String[] defaultCategories) {
        this.defaultCategories = defaultCategories;
        this.categories.setValue(defaultCategories);
    }

    private String query = "";

    public void setQuery(String query) {
        this.query = query;
    }

    public MutableLiveData<String[]> getCategories() {
        return categories;
    }

    @Override
    public List<RestaurantView> readFromDataSource(int page) {
        if (categories.getValue() == null){
            Logger.getGlobal().warning("categories value is null, cannot search restaurants.");
            return restaurantService.listRestaurants(page);
        }
        if (query.trim().isEmpty() && categories.getValue().length == defaultCategories.length) {
            return restaurantService.listRestaurants(page);
        } else {
            return restaurantService.searchRestaurants(query, categories.getValue(), page);
        }
    }

}
