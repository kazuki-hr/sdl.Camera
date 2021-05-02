package jp.ac.titech.itpro.sdl.camera;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static int REQ_PHOTO = 1234;
    private File currentPhotoFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button photoButton = findViewById(R.id.photo_button);
        photoButton.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            PackageManager manager = getPackageManager();
            @SuppressLint("QueryPermissionsNeeded")
            List<ResolveInfo> activities = manager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (!activities.isEmpty()) {
                // Create the File where the photo should go
                currentPhotoFile = null;
                try {
                    currentPhotoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Toast.makeText(MainActivity.this, R.string.cannot_create_image_file, Toast.LENGTH_LONG).show();
                }
                // Continue only if the File was successfully created
                if (currentPhotoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "jp.ac.titech.itpro.sdl.camera.fileprovider",
                            currentPhotoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(intent, REQ_PHOTO);
                }
            } else {
                Toast.makeText(MainActivity.this, R.string.toast_no_activities, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showPhoto() {
        if (currentPhotoFile == null) {
            return;
        }
        ImageView photoView = findViewById(R.id.photo_view);
        photoView.setImageURI(Uri.fromFile(currentPhotoFile));
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        if (reqCode == REQ_PHOTO && resCode != RESULT_OK) {
            currentPhotoFile = null;
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        showPhoto();
    }
}