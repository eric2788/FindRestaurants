package me.sin.findrestaurants.ui.view.comment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import me.sin.findrestaurants.R;
import me.sin.findrestaurants.ServiceLocator;
import me.sin.findrestaurants.model.Comment;
import me.sin.findrestaurants.service.AuthService;

public class CommentCreateDialog extends BottomSheetDialogFragment {

    private final AuthService authService;
    private final CommentViewModel viewModel;

    public CommentCreateDialog(CommentViewModel viewModel) {
        this.viewModel = viewModel;
        this.authService = ServiceLocator.getService(AuthService.class);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_comment, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button input = view.findViewById(R.id.create_button);
        input.setEnabled(false);
        TextView content = view.findViewById(R.id.create_content);
        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 15) {
                    content.setError("the text should at least 15 length");
                } else {
                    input.setEnabled(true);
                }
            }
        });
        if (authService.getUserId() == null) {
            Toast.makeText(getContext(), "Not Logged In", Toast.LENGTH_LONG).show();
            dismiss();
            return;
        }
        input.setOnClickListener(v -> {
            if (content.length() == 0) {
                content.setError(getString(R.string.field_required));
                return;
            }
            input.setText(R.string.submitting);
            viewModel.createData(new Comment(authService.getUserId(), content.getText().toString()));
            dismiss();
        });

    }
}
