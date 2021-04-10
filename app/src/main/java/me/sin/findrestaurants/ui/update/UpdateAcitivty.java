package me.sin.findrestaurants.ui.update;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import me.sin.findrestaurants.R;
import me.sin.findrestaurants.ServiceLocator;
import me.sin.findrestaurants.data.RequestState;
import me.sin.findrestaurants.model.Restaurant;
import me.sin.findrestaurants.service.AuthService;
import me.sin.findrestaurants.service.Base64Service;
import me.sin.findrestaurants.service.DataTransferService;
import me.sin.findrestaurants.service.RestaurantService;

public class UpdateAcitivty extends AppCompatActivity {


    private ImageView photo;
    private Base64Service base64Service;
    private FormStateViewModel stateViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        stateViewModel = new ViewModelProvider(this).get(FormStateViewModel.class);

        AuthService authService = ServiceLocator.getService(AuthService.class);
        this.base64Service = ServiceLocator.getService(Base64Service.class);
        DataTransferService dataTransferService = ServiceLocator.getService(DataTransferService.class);

        Restaurant toEdit = dataTransferService.getData("toEdit");

        if (authService.getUserId() == null) finish();

        Restaurant editor = toEdit == null ? new Restaurant(authService.getUserId()) : toEdit;

        TextView title = findViewById(R.id.input_title);
        TextView phone = findViewById(R.id.input_phone);
        TextView website = findViewById(R.id.input_website);
        TextView address = findViewById(R.id.input_address);
        this.photo = findViewById(R.id.input_photo);
        Spinner spinner = findViewById(R.id.input_category);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.category_list, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (toEdit != null){
            title.setText(toEdit.getTitle());
            String phoneTxt = toEdit.getPhone()+"";
            phone.setText(phoneTxt);
            website.setText(toEdit.getWebsite());
            address.setText(toEdit.getAddress());
            List<String> arr = Arrays.asList(getResources().getStringArray(R.array.category_list));
            spinner.setSelection(arr.indexOf(toEdit.getCategory()));
            Bitmap bitmap = base64Service.convertString64ToImage(toEdit.getImageBase64());
            photo.setImageBitmap(bitmap);
        }



        Button photoButton = findViewById(R.id.take_photo);
        Button photoAltButton = findViewById(R.id.take_photo_alt);

        FloatingActionButton fab = findViewById(R.id.save_btn);


        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityForResult(intent, RequestState.PHOTO_CAPTURE_REQUEST);
            }
        });

        photoAltButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickPhoto.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
                pickPhoto.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                pickPhoto.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                startActivityForResult(Intent.createChooser(pickPhoto, "Choose Picture") , RequestState.GALLERY_PHOTO_REQUEST);
            }
        });

        stateViewModel.getFormState().observe(this, state -> {

            if (state.getErrorField() != null){
                state.getErrorField().setError(getString(state.getErrorText() != null ? state.getErrorText() : R.string.field_required));
            }

            fab.setEnabled(state.isDataValid());
            if (state.isDataValid()){
                fab.show();
            }else{
                fab.hide();
            }

        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                stateViewModel.onDataChanged(title, address, phone, website);
            }
        };

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stateViewModel.onDataChanged(title, address, phone, website);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        title.addTextChangedListener(afterTextChangedListener);
        address.addTextChangedListener(afterTextChangedListener);
        website.addTextChangedListener(afterTextChangedListener);
        phone.addTextChangedListener(afterTextChangedListener);

        fab.setOnClickListener(view -> {

            findViewById(R.id.save_loading).setVisibility(View.VISIBLE);

            BitmapDrawable bitmapDrawable = (BitmapDrawable) photo.getDrawable();
            String img = null;
            if (bitmapDrawable != null){
                Bitmap bitmap = bitmapDrawable.getBitmap();
                img = base64Service.bitMapToString64(bitmap);
            }

            editor.setTitle(title.getText().toString());
            editor.setImage64(img);
            editor.setCategory((String)spinner.getSelectedItem());
            editor.setWebsite(website.getText().toString());
            if (!phone.getText().toString().isEmpty()) editor.setPhone(Integer.parseInt(phone.getText().toString()));
            editor.setAddress(address.getText().toString());

            stateViewModel.saveRestaurant(editor, toEdit == null);
        });

        stateViewModel.getSavingState().observe(this, state -> {
            if (state != null) {
                findViewById(R.id.save_loading).setVisibility(View.GONE);
                Toast.makeText(this, state, Toast.LENGTH_LONG).show();
                setResult(RequestState.Result.SAVE_FAILED);
                return;
            }
            setResult(RequestState.Result.SAVE_SUCCESS);
            finish();
            Toast.makeText(getApplicationContext(), "Save Success", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) return;
        if (requestCode == RequestState.PHOTO_CAPTURE_REQUEST){
            Logger.getGlobal().info("got data from image capture");
            if (data.getExtras() != null){
                Bitmap bitmap = (Bitmap)data.getExtras().get("data");
                photo.setImageBitmap(bitmap);
            }else{
                Toast.makeText(this, "data.getExtras() is null", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == RequestState.GALLERY_PHOTO_REQUEST){
            Logger.getGlobal().info("got data from gallery select");
            try {
               if (data.getData() != null){
                   Uri imageUri = data.getData();
                   final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                   final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                   photo.setImageBitmap(selectedImage);
               }else{
                   Toast.makeText(this, "data.getData() is null", Toast.LENGTH_LONG).show();
               }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

    }
}