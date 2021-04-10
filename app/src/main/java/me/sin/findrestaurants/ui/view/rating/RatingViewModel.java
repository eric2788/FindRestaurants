package me.sin.findrestaurants.ui.view.rating;

import java.util.List;

import me.sin.findrestaurants.ServiceLocator;
import me.sin.findrestaurants.model.MutableListViewModel;
import me.sin.findrestaurants.model.Rating;
import me.sin.findrestaurants.service.RatingService;

public class RatingViewModel extends MutableListViewModel<Rating> {

    private final RatingService ratingService = ServiceLocator.getService(RatingService.class);

    private int restId;

    public void setRestId(int restId) {
        this.restId = restId;
    }

    @Override
    protected boolean insertToDataSource(Rating data) {
        return ratingService.createRating(restId, data);
    }

    @Override
    protected boolean deleteFromDataSource(int id) {
        return ratingService.deleteRating(id);
    }

    @Override
    protected List<Rating> readFromDataSource(int page) {
        return ratingService.listRatings(restId, page);
    }
}
