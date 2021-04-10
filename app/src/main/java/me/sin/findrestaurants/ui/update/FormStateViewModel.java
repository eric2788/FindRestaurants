package me.sin.findrestaurants.ui.update;

import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import me.sin.findrestaurants.R;
import me.sin.findrestaurants.ServiceLocator;
import me.sin.findrestaurants.model.Restaurant;
import me.sin.findrestaurants.service.RestaurantService;

public class FormStateViewModel extends ViewModel {

    private final MutableLiveData<RequiredFormState> formState = new MutableLiveData<>();

    private final MutableLiveData<String> savingState = new MutableLiveData<>();

    private final RestaurantService restaurantService;

    public FormStateViewModel(){
        this.restaurantService = ServiceLocator.getService(RestaurantService.class);
    }

    public MutableLiveData<RequiredFormState> getFormState() {
        return formState;
    }

    public MutableLiveData<String> getSavingState() {
        return savingState;
    }

    public void onDataChanged(TextView title, TextView address, TextView phone, TextView website){
        RequiredFormState state = new RequiredFormState();
        if (title.getText().toString().isEmpty()){
            state.setErrorField(title);
        }else if (website.length() > 0 && !Pattern.compile(".+\\..+").matcher(website.getText()).find()){
            state.setErrorField(website);
            state.setErrorText(R.string.website_error);
        }else if (phone.getText().toString().trim().length() > 0 && phone.getText().toString().trim().length() != 8){
            state.setErrorField(phone);
            state.setErrorText(R.string.phone_num_error);
        }else if (address.getText().toString().isEmpty()){
            state.setErrorField(address);
        }else{
            state.setDataValid(true);
        }
        formState.setValue(state);
    }

    public void saveRestaurant(Restaurant restaurant, boolean create){
        ExecutorService executorService = ForkJoinPool.commonPool();
        executorService.submit(() -> {
            try{
                if (create){
                    restaurantService.createRestaurant(restaurant);
                }else{
                    restaurantService.updateRestaurant(restaurant);
                }
                this.savingState.postValue(null);
            }catch (Throwable e){
                e.printStackTrace();
                this.savingState.postValue(e.getMessage());
            }
        });
    }


}
