package me.sin.findrestaurants.ui.update;

import android.widget.TextView;

import androidx.annotation.Nullable;

public class RequiredFormState {

    @Nullable
    private TextView errorField;

    @Nullable
    private Integer errorText;

    private boolean dataValid;

    @Nullable
    public TextView getErrorField() {
        return errorField;
    }

    public void setErrorField(@Nullable TextView errorField) {
        this.errorField = errorField;
    }

    @Nullable
    public Integer getErrorText() {
        return errorText;
    }

    public void setErrorText(@Nullable Integer errorText) {
        this.errorText = errorText;
    }

    public void setDataValid(boolean dataValid) {
        this.dataValid = dataValid;
    }



    public boolean isDataValid() {
        return dataValid;
    }
}
