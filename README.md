# mosaic-business-demo
This repo is public
<a id="readme-top"></a>

<details> <summary>Table of Contents</summary> <ol> <li> <a href="#about-the-project">About the Project</a> <ul> <li><a href="#built-with">Built With</a></li> </ul> </li> <li> <a href="#why-kotlin">Why Kotlin?</a> <ul> <li><a href="#key-features-of-kotlin">Key Features of Kotlin</a></li> <li><a href="#comparison-with-java">Comparison with Java</a></li> </ul> </li> <li><a href="#system-diagram">System Diagram</a></li> <li><a href="#what-is-ocr">What is OCR</a></li> <li> <a href="#menu-item-extraction-app-demo">Menu Item Extraction App Demo</a> <ul> <li><a href="#step-1-setting-up-your-environment">Step 1: Setting up Your Environment</a></li> <li><a href="#step-2-building-the-ui">Step 2: Building the UI</a></li> <li><a href="#step-3-extract-and-create-menu-items">Step 3: Extract and Create Menu Items</a></li> <li><a href="#step-4-displaying-and-refining-results">Step 4: Displaying and Refining Results</a></li> </ul> </li>  <li><a href="#q-and-a-and-wrap-up">Q&A and Wrap-Up</a></li> </ol> </details>


# About the Project

This project introduces Kotlin, its benefits for Android development, and demonstrates building a Menu Item Extraction App. The app uses OCR and ChatGPT to extract text from images and display extracted menu items in a clean, user-friendly interface.
## Built With
  * Kotlin – for concise and modern Android development.
  * ML Kit (OCR) – for text recognition.
  * XML layouts – for building the UI.
  * Coroutines – for asynchronous programming.
  
<p align="right">(<a href="#readme-top">back to top</a>)</p>

# Why Kotlin?

## Key Features of Kotlin
### Conciseness:
  * Replace boilerplate Java code with shorter Kotlin code.

```java
// Java
public class User {
    private String name;
    private int age;
    
    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

```kotlin
// Kotlin
data class User(val name: String, val age: Int)
```

### Null Safety:
  * Eliminate NullPointerException errors at compile time.

```kotlin
val name: String? = null // Nullable variable
println(name?.length)    // Safe call operator
```
### Coroutines for Async Programming:
  * Simplified threading for network calls or heavy processing.

```kotlin
GlobalScope.launch {
    val data = fetchData() // Runs in the background
    updateUI(data)        // Updates on the main thread
}
```

### Extension Functions:
  * Add functionality to existing classes without modifying them.

```kotlin
fun String.capitalizeWords(): String {
    return split(" ").joinToString(" ") { it.capitalize() }
}
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>


# Comparison with Java

| Feature           | Kotlin                                   | Java                           |
|-------------------|------------------------------------------|--------------------------------|
| Conciseness       | Reduces boilerplate code.               | Verbose, requires more code.  |
| Null Safety       | Built-in.                               | Requires manual checks.       |
| Async Handling    | Coroutines simplify async tasks.        | Relies on threads and callbacks. |
| Interoperability  | Seamlessly integrates with Java codebases. | Limited Kotlin support.       |
| Cross-Platform    | Focused on Android.                     | Ideal for desktop + Android.  |

<p align="right">(<a href="#readme-top">back to top</a>)</p>

# System Diagram
Diagram Description:
The system diagram shows a single input (menu image) branching into two processes (OCR and ChatGPT). Both processes lead to the result being displayed to the user.
```mermaid
graph TD
    A[User Uploads Image of Menu] --> B{Choose Extraction Method}
    B -->|OCR| C[Process Image Using OCR]
    B -->|ChatGPT| D[Send Image or Text to ChatGPT API]
    C --> E[Display Extracted Menu Items to User]
    D --> E[Display Extracted Menu Items to User]
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

# What is OCR
OCR stands for Optical Character Recognition. It is a technology that converts different types of documents, such as scanned paper documents, PDF files, or images taken by a camera, into editable and searchable data.

## How OCR Works

1. Image Capture:
 * A document or text is scanned or photographed to produce a digital image.
2. Preprocessing
 * The image is cleaned up to improve recognition accuracy, such as by adjusting brightness, contrast, and removing noise.
3. Text Recognition:
 * The OCR software analyzes the image to detect characters, words, and numbers.
4. Output:
 * The recognized text is converted into a digital format like plain text, Word, or a searchable PDF.


<p align="right">(<a href="#readme-top">back to top</a>)</p>
 
# Menu Item Extraction App Demo
## Step 1: Setting up Your Environment

1. Install Android Studio:
  * Download Android Studio.
  * Configure Kotlin in your project.

2. Add Dependencies:
   * Add ML Kit and Coroutines dependencies in build.gradle.
    ```kotlin
    implementation "com.google.mlkit:text-recognition:16.0.1"
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0"
    ```

   
<p align="right">(<a href="#readme-top">back to top</a>)</p>


## Step 2: Building the UI
 Use Jetpack Compose for modern UI design or traditional XML layouts.

 ### Jetpack Compose Example
 ```kotlin
  @Composable
 fun MenuScreen(menuItems: MutableList<String>) {
     Column(
         modifier = Modifier.fillMaxSize(),
         horizontalAlignment = Alignment.CenterHorizontally,
     ) {
         Text("Menu Extraction App", fontSize = 24.sp)
         
         // Button to simulate adding a new menu item
         Button(onClick = { menuItems.add("New Menu Item ${menuItems.size + 1}") }) {
             Text("Capture Menu")
         }
 
         LazyColumn(
             modifier = Modifier.fillMaxSize()
         ) {
             items(menuItems) { item ->
                 Text(text = item, fontSize = 18.sp)
             }
         }
     }
 }
```

### XML Layout Example
```XML
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <TextView
        android:id="@+id/header"
        android:text="Menu Extraction App"
        android:textSize="24sp" />

    <Button
        android:id="@+id/captureButton"
        android:text="Capture Menu" />

    <RecyclerView
        android:id="@+id/menuList"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
</LinearLayout>
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Step 3: Extract and Create Menu Items

### Launch Image Picker
```kotlin
// This goes inside your Activity class
private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val inputImage = InputImage.fromFilePath(this, it)
            processImage(inputImage)
        }
    }
```

### Capture Image and Process with the OCR ML Kit to extract text
```kotlin
fun processImage(inputImage: InputImage) {
    val recognizer = TextRecognition.getClient()
    recognizer.process(inputImage)
        .addOnSuccessListener { result ->
            val extractedText = result.text
            processTextWithChatGPT(extractedText)
        }
        .addOnFailureListener { exception ->
            Log.e("OCR", "Error: ${exception.message}")
        }
}
```

### Process extracted Text with ChatGPT
```kotlin
fun processTextWithChatGPT(extractedText: String) {
    val prompt = """
        Extract and format menu items from the following text:
        $extractedText
    """.trimIndent()

    GlobalScope.launch(Dispatchers.IO) {
        try {
            val response = callChatGPTApi(prompt) // API call to ChatGPT
            withContext(Dispatchers.Main) {
                updateMenuItems(response) // Update UI with the result
            }
        } catch (e: Exception) {
            Log.e("ChatGPT", "Error: ${e.message}")
        }
    }
}

suspend fun callChatGPTApi(prompt: String): String {
    val apiKey = "your_openai_api_key"
    val url = "https://api.openai.com/v1/chat/completions"

    val client = OkHttpClient()
    val requestBody = """
        {
            "model": "gpt-4",
            "messages": [{"role": "user", "content": "$prompt"}]
        }
    """.trimIndent()

    val request = Request.Builder()
        .url(url)
        .addHeader("Authorization", "Bearer $apiKey")
        .post(RequestBody.create("application/json".toMediaTypeOrNull(), requestBody))
        .build()

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) throw IOException("Unexpected code $response")
        return JSONObject(response.body?.string() ?: "").getJSONArray("choices")
            .getJSONObject(0).getJSONObject("message").getString("content")
    }
}
```

## Step 4: Displaying Extracted Items

Use RecyclerView or LazyColumn to show menu items dynamically.

### Kotlin RecyclerView Adapter
 ```kotlin
 class MenuAdapter(private val items: List<String>) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {
     inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
         val textView: TextView = view.findViewById(R.id.textView)
     }
 
     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
         val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
         return ViewHolder(view)
     }
 
     override fun onBindViewHolder(holder: ViewHolder, position: Int) {
         holder.textView.text = items[position]
     }
 
     override fun getItemCount() = items.size
 }
 ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>


