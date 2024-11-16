package com.app.mosaicbusiness

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.app.mosaicbusiness.ui.theme.MosaicBusinessTheme
import com.google.mlkit.vision.common.InputImage
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONObject


class MainActivity : ComponentActivity() {
    // Initialize the mutable state list
    val menuItems = mutableStateListOf<String>()

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val inputImage = InputImage.fromFilePath(this, it)
            processImage(inputImage)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            MosaicBusinessTheme {
                MenuScreen(menuItems){
                    captureImage(menuItems)
                }
            }
        }
    }

    fun updateMenuItems(chatGptResponse: String, menuItems: MutableList<String>) {
        // Split the response into lines
        val lines = chatGptResponse.trim().lines()

        // Filter and process lines that look like menu items
        val items = lines.filter { it.matches(Regex("\\d+\\).*")) } // Lines starting with a number followed by ')'
            .map { it.trim() } // Remove any extra whitespace
            .map { line ->
                // Remove numbering (e.g., "1) ") for a cleaner display
                line.replace(Regex("^\\d+\\)\\s*"), "")
            }

        // Clear the current list and add new items
        menuItems.clear()
        menuItems.addAll(items)
    }


    private fun captureImage(menuItems: MutableList<String>) {
        imagePickerLauncher.launch("image/*")
    }

    private fun processImage(inputImage: InputImage) {
        // Simulate processing and updating the menu items
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizer.process(inputImage)
            .addOnSuccessListener { result ->
                val extractedText = result.text
                Log.d("OCR", "Extracted text: $extractedText")
                processTextWithChatGPT(extractedText) // Send extracted text to ChatGPT
            }
            .addOnFailureListener { exception ->
                Log.e("OCR", "Error: ${exception.message}")
            }
    }

    fun cleanPrompt(input: String): String {
        return input
            .trim()                                     // Remove leading/trailing spaces
            .replace(Regex("\\s+"), " ")               // Replace multiple spaces/newlines with a single space
            .replace("\"", "\\\"")                     // Escape double quotes
            .replace("\n", " ")                        // Replace newlines with spaces
            .replace("W/", "with")                     // Replace abbreviations like W/ with "with"
            .replace("w/", "with")
            .replace("/", " ")
            .replace("&", "and")                       // Replace & with "and"
            .replace(Regex("[^\\x20-\\x7E]"), "")      // Remove non-printable characters
    }


    fun processTextWithChatGPT(extractedText: String) {

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = callChatGPTApi(extractedText) // API call to ChatGPT
                withContext(Dispatchers.Main) {
                    updateMenuItems(response, menuItems) // Update UI with the result
                }
            } catch (e: Exception) {
                Log.e("ChatGPT", "Error: ${e.message}")
            }
        }
    }

    fun callChatGPTApi(prompt: String): String {
        val apiKey = APIClass.getOpenAIKey()
        val url = "https://api.openai.com/v1/completions"

        // Create the JSON payload for the Completions endpoint
        val jsonObject = JSONObject().apply {
            put("model", "gpt-3.5-turbo-instruct")
            put("prompt", "Extract and format menu items from the following text: ${cleanPrompt(prompt)}")
            put("max_tokens", 500)
            put("temperature", 0.7)
        }
        val requestBody = jsonObject.toString()

        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string()
            if (!response.isSuccessful) {
                Log.e("ChatGPT Error", "Code: ${response.code}, Body: $responseBody")
                throw IOException("Unexpected code $response")
            }
            // Parse and return the generated text from the response
            return JSONObject(responseBody ?: "").getJSONArray("choices")
                .getJSONObject(0).getString("text")
        }
    }


}

@Composable
fun MenuScreen(menuItems: List<String>, onCaptureImage: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Menu Extraction App", fontSize = 24.sp)

        Button(onClick = { onCaptureImage() }) {
            Text("Capture Menu")
        }

        LazyColumn {
            items(menuItems) { item ->
                // Display each menu item bolded add space between each item
                Text(text = item, fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Text(text = " ", fontSize = 12.sp)
            }
        }
    }
}
