package me.sin.findrestaurants.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ActionBarOverlayLayout;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import me.sin.findrestaurants.R;
import me.sin.findrestaurants.ui.view.RestaurantViewModel;

public class FilterBottomSheet extends BottomSheetDialogFragment {

    private final RestaurantsViewModel viewModel;
    private final String[] defaultCategories;

    public FilterBottomSheet(RestaurantsViewModel viewModel) {
        this.viewModel = viewModel;
        this.defaultCategories = viewModel.getDefaultCategories();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.filter_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        String[] selectedCategories = viewModel.getCategories().getValue();
        if (selectedCategories == null) {
            Toast.makeText(view.getContext(),"Categories value is null", Toast.LENGTH_LONG).show();
            dismiss();
            return;
        }
        List<String> selectedList = Arrays.asList(selectedCategories);
        ChipGroup chipGroup = view.findViewById(R.id.filter_chip_group);
        for (int i = 0; i < defaultCategories.length; i++) {
            String category = defaultCategories[i];
            Chip chip = new Chip(view.getContext());
            chip.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            chip.setText(category);
            chip.setCheckable(true);
            if (selectedList.contains(category)){
                chip.setChecked(true);
            }
            chipGroup.addView(chip, i);
        }
        Button btn = view.findViewById(R.id.filter_button);
        btn.setOnClickListener(v -> {
            List<Integer> selected = chipGroup.getCheckedChipIds();
            List<String> checked = new ArrayList<>();
            for (Integer id : selected) {
                Chip p = chipGroup.findViewById(id);
                if (p != null){
                    checked.add(p.getText().toString());
                }
            }
            viewModel.getCategories().postValue(checked.toArray(new String[0]));
            dismiss();
        });
    }
}
