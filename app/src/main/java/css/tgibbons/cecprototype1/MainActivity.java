package css.tgibbons.cecprototype1;

import static androidx.camera.core.ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.widget.Toast;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

public class MainActivity extends AppCompatActivity {

    TextView tvStatus;
    Button buttonUpdate;
    ImageView imageViewCamera;
    PreviewView previewView;
    // ImageView captureImage;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    LifecycleCameraController cameraController;
    ImageCapture imageCapture;
    ImageAnalysis imageAnalysis;
    Preview imagePreview;
    Bitmap bitmap;      // Bitmap from the image proxy
    Image image;        // Image from the image proxy

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tvStatus);
        imageViewCamera = findViewById(R.id.imageView);

        previewView = findViewById(R.id.previewView);
        //captureImage = findViewById(R.id.captureImg);

        setupButton1();
        setupButton2();
        startCameraProvider();
        //startCameraController();  // The simpler and newer CameraContoller does not seem to work yet...

    }
    private void setupButton1() {
        buttonUpdate = findViewById(R.id.button1);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //capturePhotoProvider();
                analyzePhotoProvider();
            }
        });
    }
    private void setupButton2() {
        buttonUpdate = findViewById(R.id.button2);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //analyzePhotoProvider();
                updateDisplay();
                //capturePhotoController();
                //Intent act2=new Intent(view.getContext(),MainActivity2.class);
                //startActivity(act2);
            }
        });
    }

    private void updateDisplay() {
        int value = getPixelValue(bitmap);
        //int value = getPixelValue(BitmapFactory.decodeResource(getResources(), R.drawable.color_image_p));
        //tvStatus.setText("Updated "+value);
        int redValue = Color.red(value);
        int blueValue = Color.blue(value);
        int greenValue = Color.green(value);
        tvStatus.setText("Hex = " + Integer.toHexString(value)+" and Red = "+redValue);
    }

    //http://www.41post.com/3719/programming/android-how-to-return-rgb-values-from-an-image-file
    private int getPixelValue(Bitmap bitmap) {
        Log.i("TEG","getPixelValue --- 111 ");
        Log.i("TEG","bmp width = "+bitmap.getWidth());
        Log.i("TEG", "bmp height = "+bitmap.getHeight());
        //Bitmap bmp;
        int[][] rgbValues;

        //bmp = BitmapFactory.decodeResource(getResources(), R.drawable.color_image_p);

        //define the array size
        rgbValues = new int[bitmap.getWidth()][bitmap.getHeight()];

        for(int i=0; i < bitmap.getWidth(); i++)
        {
            for(int j=0; j < bitmap.getHeight(); j++)
            {
                rgbValues[i][j] = bitmap.getPixel(i, j);
            }
        }


        int x = 100;
        int y = 200;
        int colorValue = bitmap.getPixel(x,y);
        return colorValue;
    }


    private void startCameraController() {
        cameraController = new LifecycleCameraController(this);
        cameraController.setCameraSelector(CameraSelector.DEFAULT_BACK_CAMERA);
        previewView.setController(cameraController);
        //imageCapture = cameraController.g
        cameraController.bindToLifecycle(this);

    }


    // =========================================================================================
    //https://akhilbattula.medium.com/android-camerax-java-example-aeee884f9102
    // =========================================================================================
    private Executor executor = Executors.newSingleThreadExecutor();
    private void startCameraProvider() {

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        //cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        //cameraProviderFuture = ProcessCameraProvider.getInstance(context).get();

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
                //bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
    }

    void startCameraX(@NonNull ProcessCameraProvider cameraProvider){
        //Camera Selector Use Case
        cameraProvider.unbind();
        CameraSelector cameraSelector = new CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build();

        // Preview Use Case
        imagePreview = new Preview.Builder().build();
        imagePreview.setSurfaceProvider(previewView.getSurfaceProvider());

        // Image Analysis Use Case
        imageAnalysis = new ImageAnalysis.Builder()
            // enable the following line if RGBA output is needed.
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            //.setTargetResolution(new Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build();

        // Image Capture Use Case
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();
        
        cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, imageCapture, imagePreview);
    }

    private void analyzePhotoProvider() {
        Log.i("TEG","Trying to Analyze Photo --- 111");
        executor = Executors.newSingleThreadExecutor();
        Log.i("TEG","Trying to Analyze Photo --- 222");
        imageAnalysis.setAnalyzer(executor, new ImageAnalysis.Analyzer() {
            @OptIn(markerClass = ExperimentalGetImage.class) @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                Log.i("TEG","Trying to Analyze Photo --- analyze callback 1");
                int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
                Log.i("TEG","analyze callback 1 --- rotationDegrees = "+rotationDegrees);
                // Get the image proxy plane's buffer which is where the pixels are
                ImageProxy.PlaneProxy[] planes = imageProxy.getPlanes();
                Log.i("TEG","analyze callback 1 --- planes = "+planes.length);

                ByteBuffer buffer = planes[0].getBuffer();
                // Create a blank bitmap
                Log.i("TEG","analyze callback 2");
                bitmap = imageProxy.toBitmap();
                image = imageProxy.getImage();
                //bitmap = Bitmap.createBitmap(imageProxy.getWidth(),imageProxy.getHeight(),Bitmap.Config.ARGB_8888);
                // copy the image proxy plane into the bitmap
                //bitmap.copyPixelsFromBuffer(buffer);
                Log.i("TEG", "analyze callback 2 --- bmp height = "+bitmap.getHeight());

                // Crop!
                //bitmap = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height())

                // after done, release the ImageProxy object
                imageProxy.close();
            }
        });
    }

    private void capturePhotoProvider() {
        //Toast.makeText(getApplicationContext(), "Trying to Capture Photo", Toast.LENGTH_SHORT );
        Log.i("TEG","Trying to Capture Photo");

        //SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        //File file = new File(getBatchDirectoryName(), mDateFormat.format(new Date())+ ".jpg");
        File file = new File(getApplicationContext().getExternalCacheDir() + File.separator + System.currentTimeMillis() + ".png");

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
        Log.i("TEG","Trying to Capture Photo 222");

        executor = Executors.newSingleThreadExecutor();
        Log.i("TEG","Trying to Capture Photo 333");

        imageCapture.takePicture(
                outputFileOptions,
                executor,
                new ImageCapture.OnImageSavedCallback () {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Log.i("TEG","onImageSaved -- Photo has been taken and saved");
                        //Toast.makeText(getApplicationContext(), "Photo has been taken and saved", Toast.LENGTH_SHORT );
                    }
                    @Override
                    public void onError(@NonNull ImageCaptureException error) {
                        //Toast.makeText(getApplicationContext(), "Error taking and saving photo", Toast.LENGTH_SHORT );
                        error.printStackTrace();
                    }
                });

    }

    private void capturePhotoController() {
        Log.i("TEG","Trying to Capture Photo with Controller 111");
        File file = new File(getApplicationContext().getExternalCacheDir() + File.separator + System.currentTimeMillis() + ".png");

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
        Log.i("TEG","Trying to Capture Photo with Controller 222");

        executor = Executors.newSingleThreadExecutor();
        Log.i("TEG","Trying to Capture Photo with Controller 333");

        cameraController.takePicture(
                outputFileOptions,
                executor,
                new ImageCapture.OnImageSavedCallback () {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Log.i("TEG","onImageSaved -- Photo has been taken and saved");
                        //Toast.makeText(getApplicationContext(), "Photo has been taken and saved", Toast.LENGTH_SHORT );
                    }
                    @Override
                    public void onError(@NonNull ImageCaptureException error) {
                        //Toast.makeText(getApplicationContext(), "Error taking and saving photo", Toast.LENGTH_SHORT );
                        error.printStackTrace();
                    }
                });
    }

    private String getBatchDirectoryName() {

        String folder_path = "";
        folder_path = Environment.getExternalStorageDirectory().toString() + "/images";
        File dir = new File(folder_path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return folder_path;
    }

    private int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};

    private boolean allPermissionsGranted(){

        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCameraProvider();
            } else {
                Log.i("TEG","Permissions not granted by the user.");
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }

}