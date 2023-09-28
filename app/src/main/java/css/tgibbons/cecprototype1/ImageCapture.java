package css.tgibbons.cecprototype1;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysis.Analyzer;
import androidx.camera.core.ImageProxy;
//import androidx.camera.lifecycle.ProcessCameraProvider;
//import androidx.camera.view.PreviewView;
import androidx.lifecycle.LifecycleOwner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.widget.Toast;
// Capture an image directly with the camera instead of using intents and the camera app
public class ImageCapture {

    private ExecutorService cameraExecutor;
    private Context context;

    public ImageCapture(Context context) {
        this.context = context;
    }

    public interface OnImageCapturedListener {
        void onImageCaptured(Bitmap bitmap);
    }

    /*
    public void startCamera(PreviewView view, LifecycleOwner lifecycleOwner, final OnImageCapturedListener listener) {
        cameraExecutor = Executors.newSingleThreadExecutor();

        final HandlerThread handlerThread = new HandlerThread("CameraXAnalysis");
        handlerThread.start();
        final Handler handler = new Handler(handlerThread.getLooper());

        ProcessCameraProvider cameraProvider = null;

        try {
            cameraProvider = ProcessCameraProvider.getInstance(context).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cameraProvider != null) {

            cameraProvider.addListener(() -> {
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1920, 1080))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, new Analyzer() {
                    @Override
                    public void analyze(ImageProxy imageProxy) {
                        Bitmap bitmap = convertImageProxyToBitmap(imageProxy);
                        if (bitmap != null) {
                            listener.onImageCaptured(bitmap);
                            imageProxy.close();
                            handler.post(() -> {
                                Toast.makeText(context, "Image captured", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                });

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageAnalysis);
            });
        }


    }
*/
    public void stopCamera() {
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }

    private Bitmap convertImageProxyToBitmap(ImageProxy imageProxy) {
        // Convert the ImageProxy to a Bitmap
        // Here, you'll need to implement the conversion logic
        // You can use YuvToRgbConverter or ImageDecoder depending on the format of the image

        // For example, you can use YuvToRgbConverter as follows:
        // YuvToRgbConverter converter = new YuvToRgbConverter(context);
        // Bitmap bitmap = converter.yuvToRgb(imageProxy.getImage());

        // Replace the above logic with your actual conversion code

        return null; // Return the converted Bitmap
    }



}
