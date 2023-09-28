package css.tgibbons.cecprototype1;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity2 extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private ImageCapture cameraCaptureUtil;
    private PreviewView previewView;
    private ImageView imageView;
    private Button captureImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.previewView);
        imageView = findViewById(R.id.imageView);
        captureImageButton = findViewById(R.id.button1);

        if (allPermissionsGranted()) {
            //startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        //cameraCaptureUtil = new CameraCaptureUtil(this);

        captureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                //startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
/*
    private void startCamera() {
        cameraCaptureUtil.startCamera(previewView, this, new ImageCapture.OnImageCapturedListener() {
            @Override
            public void onImageCaptured(Bitmap bitmap) {
                // Display the captured image in the ImageView
                imageView.setImageBitmap(bitmap);
            }
        });
    }
 */

    @RequiresPermission(Manifest.permission.CAMERA)
    private void captureImage() {
        // You can call the capture method here if needed.
        // cameraCaptureUtil.captureImage();
        // The image will be received through the callback in startCamera.
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}