package me.sin.findrestaurants.ui.view.comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.function.BiConsumer;

import me.sin.findrestaurants.R;
import me.sin.findrestaurants.model.Comment;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private final Context context;
    private final List<Comment> comments;
    private final BiConsumer<View, List<Comment>> clickListener;

    public CommentAdapter(Context context, List<Comment> comments, BiConsumer<View, List<Comment>> clickListener) {
        this.context = context;
        this.comments = comments;
        this.clickListener = clickListener;
    }

    public void addAll(List<Comment> comments){
        this.comments.addAll(comments);
        this.notifyDataSetChanged();
    }

    public void clearAll(){
        this.comments.clear();
        this.notifyDataSetChanged();
    }

    public void setAll(List<Comment> comments){
        this.comments.clear();
        this.comments.addAll(comments);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycleview_comments, parent, false);
        view.setOnLongClickListener(v -> {
            this.clickListener.accept(v, comments);
            return true;
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.user.setText(comment.getAuthor());
        holder.content.setText(comment.getComments());
        holder.date.setText(SimpleDateFormat.getDateInstance().format(comment.getDate()));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        final TextView user;
        final TextView content;
        final TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.user = itemView.findViewById(R.id.comment_user);
            this.content = itemView.findViewById(R.id.comment_content);
            this.date = itemView.findViewById(R.id.comment_date);
        }
    }
}
