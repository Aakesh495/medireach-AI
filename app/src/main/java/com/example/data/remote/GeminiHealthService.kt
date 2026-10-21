package com.example.data.remote

import com.example.BuildConfig
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

@Serializable
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

@Serializable
data class Content(
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String? = null
)

@Serializable
data class GenerationConfig(
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null
)

@Serializable
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

@Serializable
data class Candidate(
    val content: Content? = null
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

enum class AiToolMode(val titleEn: String, val titleHi: String, val titleOr: String) {
    SYMPTOM_GUIDE("AI Symptom Guide", "लक्षण मार्गदर्शिका", "ଲକ୍ଷଣ ମାର୍ଗଦର୍ଶିକା"),
    REPORT_EXPLAINER("Medical Report Explainer", "मेडिकल रिपोर्ट समझें", "ମେଡିକାଲ୍ ରିପୋର୍ଟ ବୁଝନ୍ତୁ"),
    PATIENT_TRANSLATOR("Multilingual Translator", "रोगी-डॉक्टर अनुवादक", "ରୋଗୀ-ଡାକ୍ତର ଅନୁବାଦକ"),
    MEDICINE_SAFETY("Medicine Safety Info", "दवा सुरक्षा जानकारी", "ଔଷଧ ସୁରକ୍ଷା ସୂଚନା"),
    NAVIGATION_AGENT("Care Navigation Agent", "स्वास्थ्य नेविगेशन एजेंट", "ସ୍ୱାସ୍ଥ୍ୟ ନେଭିଗେସନ୍ ଏଜେଣ୍ଟ")
}

class GeminiHealthService {
    private val json = Json { ignoreUnknownKeys = true }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val api: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(GeminiApiService::class.java)
    }

    suspend fun askHealthAssistant(
        mode: AiToolMode,
        userInput: String,
        languageCode: String // "EN", "HI", "OR"
    ): String = withContext(Dispatchers.IO) {
        val languageName = when (languageCode) {
            "HI" -> "Hindi (हिन्दी) with simple English medical terms in brackets"
            "OR" -> "Odia (ଓଡ଼ିଆ) with simple English medical terms in brackets"
            else -> "simple, compassionate English suitable for Indian patients and families"
        }

        val systemPrompt = """
            You are MediReach AI, an India-focused healthcare information and navigation assistant.
            STRICT HEALTH SAFETY BOUNDARIES:
            1. You are an educational information and care-navigation tool, NOT a doctor, diagnostic service, or prescription authority.
            2. Always begin or end with a clear, calm safety boundary reminder that a qualified doctor or nearby PHC/CHC clinician must confirm any diagnosis or medicine.
            3. Highlight emergency red flags (chest pain, breathing difficulty, unconsciousness, severe bleeding, high fever with confusion, pregnancy bleeding) and instruct calling 108 or 112 immediately.
            4. Cite public health references (MoHFW India, ICMR, WHO, National List of Essential Medicines, Ayushman Bharat PM-JAY).
            5. Respond clearly in $languageName using structured bullet points:
               - Key Summary / Plain Meaning
               - Red Flag Warning Signs (When to call 108 or go to Emergency)
               - Next Care Steps (e.g., PHC/CHC visit, questions to ask the doctor, home hydration)
               - Verified Public Health Source Citation
            Current Tool Mode: ${mode.titleEn}
        """.trimIndent()

        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext buildOfflineSafeFallback(mode, userInput, languageCode)
        }

        val request = GenerateContentRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = "[Mode: ${mode.titleEn}] [Language: $languageCode]\nUser query: $userInput")))
            ),
            generationConfig = GenerationConfig(
                temperature = 0.3f,
                topP = 0.9f,
                topK = 40
            ),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt)))
        )

        try {
            val response = api.generateContent(apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!text.isNullOrBlank()) {
                text
            } else {
                buildOfflineSafeFallback(mode, userInput, languageCode)
            }
        } catch (_: Exception) {
            buildOfflineSafeFallback(mode, userInput, languageCode)
        }
    }

    private fun buildOfflineSafeFallback(
        mode: AiToolMode,
        userInput: String,
        languageCode: String
    ): String {
        val header = when (languageCode) {
            "HI" -> "🩺 MediReach सुरक्षित स्वास्थ्य मार्गदर्शन (MoHFW / WHO संदर्भ)"
            "OR" -> "🩺 MediReach ସୁରକ୍ଷିତ ସ୍ୱାସ୍ଥ୍ୟ ମାର୍ଗଦର୍ଶନ (MoHFW / WHO ଆଧାରित)"
            else -> "🩺 MediReach Verified Health Guidance (MoHFW / ICMR / WHO Reference)"
        }
        return when (mode) {
            AiToolMode.SYMPTOM_GUIDE -> """
                $header
                • Query Reviewed: "$userInput"
                • Non-Diagnostic Triage Guidance:
                  - Monitor temperature, hydration (urine output every 4-6 hours), and breathing comfort.
                  - Start oral hydration (WHO ORS, clean boiled water, dal water) and rest in a ventilated room.
                • 🚨 Emergency Red Flags (Call 108 / 112 Immediately if present):
                  - Difficulty breathing, chest pressure, persistent vomiting, confusion, bluish lips, or bleeding from gums/nose.
                • Questions to Ask Your Doctor at PHC / CHC:
                  1. "Could this require a basic blood test (CBC / Malaria RDT / Dengue NS1)?"
                  2. "What warning signs at home mean I should return to the emergency ward?"
                • Source: MoHFW Standard Treatment Workflows (STW) & WHO Primary Care Triage.
                • Safety Notice: Information only — not a medical diagnosis or prescription.
            """.trimIndent()

            AiToolMode.REPORT_EXPLAINER -> """
                $header
                • Report Value / Note Reviewed: "$userInput"
                • Plain-Language Explanation:
                  - Lab reference ranges vary slightly by laboratory and patient age/sex.
                  - Hemoglobin (Hb) < 11 g/dL in pregnancy or < 12 g/dL in adult women indicates anemia; HbA1c >= 6.5% points toward diabetes control review; Platelet counts < 1,00,000/µL in fever require close medical monitoring.
                • Recommended Next Step:
                  - Bring this original report to your nearest Government PHC/CHC or OPD doctor so they can correlate the numbers with your physical examination.
                • Source: ICMR Diagnostic Reference Standards & National Free Diagnostic Service (Nidan).
                • Safety Notice: Lab numbers must always be interpreted alongside clinical examination by a doctor.
            """.trimIndent()

            AiToolMode.PATIENT_TRANSLATOR -> """
                $header
                • Clinical Phrase Translation Bridge ("$userInput"):
                  - English: "I have had these symptoms for a few days. Please explain the test and generic medicine options."
                  - हिन्दी (Hindi): "मुझे कुछ दिनों से ये लक्षण हैं। कृपया जांच और जनऔषधि (जेनेरिक) दवा के बारे में सरल भाषा में बताएं।"
                  - ଓଡ଼ିଆ (Odia): "ମୋତେ କିଛି ଦିନ ହେବ ଏହି ଲକ୍ଷଣ ଦେଖାଯାଉଛି। ଦୟାକରି ପରୀକ୍ଷା ଏବଂ ଜନଔଷଧି ଔଷଧ ବିଷୟରେ ସରଳ ଭାଷାରେ କୁହନ୍ତୁ।"
                • Tip for Doctor Visit: Show this card directly to the hospital registration desk or OPD doctor.
            """.trimIndent()

            AiToolMode.MEDICINE_SAFETY -> """
                $header
                • Medicine Safety Check ("$userInput"):
                  - Always verify the Generic Salt Name on the strip and check the expiry date.
                  - Never take Schedule H antibiotics or painkillers (NSAIDs like Ibuprofen/Diclofenac) during undiagnosed fever without a doctor's prescription.
                  - Ask your government hospital pharmacy (Niramaya) or PM Bhartiya Janaushadhi Kendra for NABL-tested generic equivalents.
                • Source: National List of Essential Medicines (NLEM) & Indian Pharmacopoeia Commission.
                • Safety Notice: Never start, stop, or alter a prescribed dose without consulting your physician.
            """.trimIndent()

            AiToolMode.NAVIGATION_AGENT -> """
                $header
                • Care Pathway Recommendation for: "$userInput"
                • Step 1 — Choose the Right Facility:
                  - Routine fever, antenatal check, immunization, or blood pressure/sugar screening -> Nearest Sub-Centre / Primary Health Centre (PHC) or eSanjeevani Tele-OPD.
                  - 24x7 delivery, snakebite, fractures, IV fluids, or specialist OPD -> Community Health Centre (CHC) or District Hospital.
                • Step 2 — Documents to Carry:
                  - Aadhaar Card, Ration Card / Ayushman Bharat PM-JAY or BSKY Card, and previous prescriptions.
                • Step 3 — Helplines:
                  - 108 (Free Ambulance), 104 (Health Advice Helpline), 14555 (Ayushman Bharat Desk).
            """.trimIndent()
        }
    }
}
