package miguel.alejandro.edu.drivesafeam.detection

import android.content.Context
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.face.Face
import java.util.concurrent.Executors

class CameraManager(private val context: Context) {

    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private val faceDetectionManager = FaceDetectionManager()
    private var lastAnalyzeTime = 0L
    var monitoringIntervalMs = 100L // 5 frames por segundo (antes 1000L)
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    fun startCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        onFacesDetected: (List<Face>) -> Unit
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                this.cameraProvider = cameraProvider

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor) { imageProxy ->
                            val currentTime = System.currentTimeMillis()
                            if (currentTime - lastAnalyzeTime >= monitoringIntervalMs) {
                                lastAnalyzeTime = currentTime
                                faceDetectionManager.detectInImage(imageProxy) { faces ->
                                    onFacesDetected(faces)
                                }
                            } else {
                                imageProxy.close()
                            }
                        }
                    }

                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
            } catch (exc: Exception) {
                Log.e("CameraManager", "Error al iniciar la cámara: ${exc.message}")
            }

        }, ContextCompat.getMainExecutor(context))
    }

    fun setTorch(enable: Boolean) {
        camera?.cameraControl?.enableTorch(enable)
    }

    fun stopCamera() {
        try {
            Log.d("CameraManager", "Pausando cámara...")
            cameraProvider?.unbindAll()
            camera = null
        } catch (e: Exception) {
            Log.e("CameraManager", "Error al pausar: ${e.message}")
        }
    }

    fun shutdown() {
        try {
            Log.d("CameraManager", "Iniciando apagado de recursos...")
            cameraProvider?.unbindAll()
            faceDetectionManager.close()
            cameraExecutor.shutdown()
            Log.d("CameraManager", "Recursos liberados correctamente")
        } catch (e: Exception) {
            Log.e("CameraManager", "Error durante el apagado: ${e.message}")
        }
    }
}
