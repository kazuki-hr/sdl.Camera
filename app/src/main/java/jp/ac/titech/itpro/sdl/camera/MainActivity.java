package jp.ac.titech.itpro.sdl.camera;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static int REQ_PHOTO = 1234;
    private Bitmap photoImage = null;

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
                startActivityForResult(intent, REQ_PHOTO);
            } else {
                Toast.makeText(MainActivity.this, R.string.toast_no_activities, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showPhoto() {
        if (photoImage == null) {
            return;
        }
        ImageView photoView = findViewById(R.id.photo_view);
        photoView.setImageBitmap(photoImage);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        if (reqCode == REQ_PHOTO) {
            if (resCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                photoImage = (Bitmap) extras.get("data");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showPhoto();
    }
}