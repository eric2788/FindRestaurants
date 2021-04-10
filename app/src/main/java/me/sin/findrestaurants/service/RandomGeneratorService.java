package me.sin.findrestaurants.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.sin.findrestaurants.model.Rating;
import me.sin.findrestaurants.model.RestaurantView;

public class RandomGeneratorService {

    private final static Random RANDOM = new Random();

    public List<RestaurantView> generateRandomRestaurants(int amount, int page){
        List<RestaurantView> views = new ArrayList<>();
        for (int i = (page-1)*amount; i <= (page-1)*amount + amount; i++) {
            RestaurantView view = new RestaurantView("rest-"+i, (float)RANDOM.nextDouble() * 5, null, i, randomCategory());
            views.add(view);
        }
        return views;
    }

    private Rating[] generateRandomRatings(int amount){
        Rating[] ratings = new Rating[amount];
        List<String> users = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            ratings[i] = new Rating(randomUser(), RANDOM.nextFloat() * 5);
        }
        return ratings;
    }

    private String randomCategory(){
        String[] categories = new String[]{
                "Japanese Food", "Chinese Food", "Korean Food", "Thai Food", "Vietnam Food","Western Food"
        };
        return categories[RANDOM.nextInt(categories.length)];
    }


    private String randomUser(){
        String[] users = new String[]{
                "ericlam123", "eric2788", "lam2388", "apple232", "tim12312",
        "ivan12321", "tom2141", "may1231", "sin1231", "cat2131", "xuan21312512", "jack21321",
        "canstaw123", "1y9241", "lovehi1231"};
        return users[RANDOM.nextInt(users.length)];
    }
}
