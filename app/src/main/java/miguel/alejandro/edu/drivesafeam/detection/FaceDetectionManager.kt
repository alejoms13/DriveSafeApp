package miguel.alejandro.edu.drivesafeam.detection

import android.media.Image
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceDetectionManager {
    // Configuramos ML Kit para detección rápida, con clasificación (ojos) y tracking de rostros.
    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .enableTracking()
        .build()

    private var detector = FaceDetection.getClient(options)

    fun detectInImage(imageProxy: androidx.camera.core.ImageProxy, onResult: (List<Face>) -> Unit) {
        @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
        val mediaImage: Image? = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            detector.process(image)
                .addOnSuccessListener { faces ->
                    onResult(faces)
                }
                .addOnFailureListener { e ->
                    Log.e("FaceDetectionManager", "Error en detección: ${e.message}")
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    fun close() {
        try {
            detector.close()
            Log.d("FaceDetectionManager", "Detector cerrado correctamente")
        } catch (e: Exception) {
            Log.e("FaceDetectionManager", "Error al cerrar el detector: ${e.message}")
        }
    }
}
