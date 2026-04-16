package com.example.imagetocalories.data.remote


import android.graphics.Bitmap
import com.example.imagetocalories.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

class GeminiService @Inject constructor() {
    private val apiKey = BuildConfig.GEMINI_API_KEY

    private val model = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = apiKey,
        generationConfig = generationConfig {
            responseMimeType = "application/json"
        },
        safetySettings = listOf(
            SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.ONLY_HIGH),
            SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.ONLY_HIGH),
            SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.ONLY_HIGH),
            SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.ONLY_HIGH)
        ),
        systemInstruction = content {
            text("You are a specialized nutritional analysis AI. Your task is to analyze food images and provide accurate meal names, estimated weights in grams, and total calories in a structured JSON format.")
        }
    )

    suspend fun analyzeMealImage(bitmap: Bitmap): GeminiCalorieResponse? = withContext(Dispatchers.IO) {
        try {
            val prompt = """
            Analyze this food image. Provide the meal name, weight in grams, total calories, and a confidence score (0.0 to 1.0).
            Return strictly JSON: 
            {"mealName": "Pizza", "weightGram": 250, "calories": 600, "confidence": 0.85}
            """.trimIndent()

            val inputContent = content {
                image(bitmap)
                text(prompt)
            }

            val response = model.generateContent(inputContent)
            val jsonText = response.text?.replace("```json", "")?.replace("```", "")?.trim()

            if (jsonText != null) {
                val jsonObject = JSONObject(jsonText)
                GeminiCalorieResponse(
                    mealName = jsonObject.getString("mealName"),
                    weightGram = jsonObject.getInt("weightGram"),
                    calories = jsonObject.getInt("calories"),
                    confidence = jsonObject.optDouble("confidence", 0.0)
                )
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}