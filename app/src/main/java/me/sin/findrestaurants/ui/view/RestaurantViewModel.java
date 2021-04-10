package me.sin.findrestaurants.ui.view;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

import me.sin.findrestaurants.ServiceLocator;
import me.sin.findrestaurants.model.MutableListViewModel;
import me.sin.findrestaurants.model.Restaurant;
import me.sin.findrestaurants.model.RestaurantView;
import me.sin.findrestaurants.service.RestaurantService;

public class RestaurantViewModel extends MutableListViewModel<Restaurant> {

    private final MutableLiveData<MutationState> restaurantMutableLiveData = new MutableLiveData<>();
    private final RestaurantService restaurantService;

    public RestaurantViewModel() {
        this.restaurantService = ServiceLocator.getService(RestaurantService.class);
    }

    public static class MutationState{
        private String errorMessage;
        private Restaurant restaurant;

        public MutationState(Restaurant restaurant) {
            this.restaurant = restaurant;
        }

        public MutationState(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public Restaurant getRestaurant() {
            return restaurant;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    public MutableLiveData<MutationState> getRestaurantMutableLiveData() {
        return restaurantMutableLiveData;
    }

    public void readRestaurant(RestaurantView restaurantView) {
        ExecutorService executorService = ForkJoinPool.commonPool();
        executorService.submit(() -> {
            try {
                Restaurant restaurant = restaurantService.getRestaurant(restaurantView.getId());
                this.restaurantMutableLiveData.postValue(new MutationState(restaurant));
            } catch (Exception e) {
                e.printStackTrace();
                this.restaurantMutableLiveData.postValue(new MutationState(e.getMessage()));
            }
        });
    }

    @Override
    protected boolean insertToDataSource(Restaurant data) {
        restaurantService.createRestaurant(data);
        return true;
    }

    @Override
    protected boolean deleteFromDataSource(int id) {
        return restaurantService.deleteRestaurant(id);
    }

    @Override
    protected List<Restaurant> readFromDataSource(int page) {
        throw new UnsupportedOperationException("not listable");
    }
}
