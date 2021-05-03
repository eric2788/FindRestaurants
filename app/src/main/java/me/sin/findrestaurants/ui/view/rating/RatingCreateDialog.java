package me.sin.findrestaurants.ui.view.rating;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.logging.Logger;

import me.sin.findrestaurants.R;
import me.sin.findrestaurants.ServiceLocator;
import me.sin.findrestaurants.model.Rating;
import me.sin.findrestaurants.service.AuthService;

public class RatingCreateDialog extends BottomSheetDialogFragment {

    private final RatingViewModel viewModel;
    private final AuthService authService;

    public RatingCreateDialog(RatingViewModel viewModel) {
        this.viewModel = viewModel;
        this.authService = ServiceLocator.getService(AuthService.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_rating, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button input = view.findViewById(R.id.review_button);
        RatingBar star = view.findViewById(R.id.create_star);
        TextView content = view.findViewById(R.id.create_review);
        if (authService.getUserId() == null) {
            Toast.makeText(getContext(), "Not Logged In", Toast.LENGTH_LONG).show();
            dismiss();
            return;
        }
        input.setOnClickListener(v -> {
            /*
            if (content.length() == 0) {
                content.setError(getString(R.string.field_required));
                return;
            }
             */
            input.setText(R.string.submitting);
            viewModel.createData(new Rating(authService.getUserId(), star.getRating(), content.getText().toString()));
            dismiss();
        });
    }
}
