package me.sin.findrestaurants.ui.view.comment;

import java.util.List;

import me.sin.findrestaurants.ServiceLocator;
import me.sin.findrestaurants.model.Comment;
import me.sin.findrestaurants.model.MutableListViewModel;
import me.sin.findrestaurants.service.CommentService;

public class CommentViewModel extends MutableListViewModel<Comment> {

    private final CommentService commentService = ServiceLocator.getService(CommentService.class);

    private int restId;

    public void setRestId(int restId) {
        this.restId = restId;
    }

    @Override
    protected List<Comment> readFromDataSource(int page) {
        return commentService.listComments(restId, page);
    }

    @Override
    protected boolean insertToDataSource(Comment data) {
        return commentService.createComment(restId, data);
    }

    @Override
    protected boolean deleteFromDataSource(int id) {
        return commentService.deleteComment(id);
    }
}
