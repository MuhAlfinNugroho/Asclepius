package com.dicoding.asclepius.view

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications

class ResultActivity : AppCompatActivity(), ImageClassifierHelper.ClassifierListener {

    private lateinit var binding: ActivityResultBinding
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))

        imageClassifierHelper = ImageClassifierHelper(
            contextValue = this,
            classifierListenerValue = this
        )

        imageUri?.let {
            binding.resultImage.setImageURI(it)
            imageClassifierHelper.classifyImage(it)
        }
    }

    override fun onError(errorMsg: String) {
        showToast(errorMsg)
    }

    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
        results?.let {
            showResults(it)
        } ?: run {
            showToast("No result found")
        }
    }

    private fun showResults(results: List<Classifications>) {
        val result = results[0]
        val title = result.categories[0].label
        val confidence = result.categories[0].score
        val prediction = "$title: ${(confidence * 100).toInt()}%"

        binding.resultText.text = prediction
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }
}