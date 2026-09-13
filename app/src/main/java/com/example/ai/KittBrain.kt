package com.example.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.random.Random

data class KittResponse(
  val replyText: String,
  val triggerAction: String? = null,
  val isAiOnline: Boolean = false
)

object KittBrain {
  private val okHttpClient by lazy {
    OkHttpClient.Builder()
      .connectTimeout(15, TimeUnit.SECONDS)
      .readTimeout(20, TimeUnit.SECONDS)
      .build()
  }

  suspend fun processQuery(
    query: String,
    driverName: String = "Michael",
    customApiKey: String = ""
  ): KittResponse = withContext(Dispatchers.IO) {
    val trimmed = query.trim()
    val lower = trimmed.lowercase(Locale("tr", "TR"))

    // 1. Direct KITT Tactical & Car Command checks
    when {
      lower.contains("turbo boost") || lower.contains("zıpla") || lower.contains("boost") -> {
        return@withContext KittResponse(
          replyText = "Turbo Boost hazır $driverName! 45 PSI hidrolik basınçla 15 metre sıçrama manevrası başlatıldı!",
          triggerAction = "TURBO_BOOST"
        )
      }

      lower.contains("durum") || lower.contains("rapor") || lower.contains("sistem") -> {
        return@withContext KittResponse(
          replyText = "Durum Raporu: Moleküler bağlı zırh %100 sağlam. Alfa devreleri devrede, CPU sıcaklığı 42°C, güç çekirdeği tam kapasite. Yolculuk için hazırım $driverName.",
          triggerAction = "DIAGNOSTICS"
        )
      }

      lower.contains("tara") || lower.contains("radar") || lower.contains("sensör") || lower.contains("çevre") -> {
        val dist = Random.nextInt(20, 250)
        return@withContext KittResponse(
          replyText = "Kızılötesi ve ultrasonik tarama tamamlandı $driverName. 360 derecelik alanda $dist metre mesafede şüpheli bir sinyal tespit edilmedi. Çevre güvenli.",
          triggerAction = "SCAN"
        )
      }

      lower.contains("spm") || lower.contains("süper takip") || lower.contains("super pursuit") -> {
        return@withContext KittResponse(
          replyText = "Süper Takip Modu (SPM) aktif edildi! Yan kanatçıklar ve hava frenleri açıldı. Azami hız potansiyeli 480 km/s $driverName!",
          triggerAction = "SPM"
        )
      }

      lower.contains("oto pilot") || lower.contains("otomatik sürüş") || lower.contains("auto cruise") -> {
        return@withContext KittResponse(
          replyText = "Auto Cruise moduna geçildi $driverName. Direksiyon ve fren kontrolü Knight 2000 otonom çekirdeğine devredildi. Rahatınıza bakın.",
          triggerAction = "AUTO_CRUISE"
        )
      }

      lower.contains("sen kimsin") || lower.contains("adın ne") || lower.contains("kendini tanıt") -> {
        return@withContext KittResponse(
          replyText = "Ben K.I.T.T. — Knight Industries Two Thousand. Wilton Knight tarafından tasarlanan, dünyanın en gelişmiş yapay zekaya sahip tekerlekli süper bilgisayarıyım. Ve senin sadık ortağınım $driverName."
        )
      }

      lower.contains("michael kim") || lower.contains("michael knight") -> {
        return@withContext KittResponse(
          replyText = "Michael Knight, Kanun ve Hükümet Vakfı'nın (FLAG) baş saha ajanı ve benim en güvendiğim dostumdur. Wilton Knight'ın dediği gibi: 'Bir insan fark yaratabilir, Michael.'"
        )
      }

      lower.contains("devon") || lower.contains("devon miles") -> {
        return@withContext KittResponse(
          replyText = "Devon Miles, Vakfımızın saygıdeğer direktörüdür $driverName. Bize yeni görevler verirken her zaman çayından bir yudum almayı ihmal etmez."
        )
      }

      lower.contains("bonnie") || lower.contains("barstow") -> {
        return@withContext KittResponse(
          replyText = "Bonnie Barstow, baş teknik uzmanımız ve benim donanım mimarımdır. Mikroişlemcilerime olan özenine daima hayranım $driverName."
        )
      }

      lower.contains("ücretsiz") || lower.contains("bedava") || lower.contains("ücret") || lower.contains("fiyat") -> {
        return@withContext KittResponse(
          replyText = "Tüm Knight 2000 sistemleri tamamen ÜCRETSİZDİR $driverName! Hiçbir abonelik, gizli ücret veya kredi kartı gerektirmez. Kanun ve Hükümet Vakfı hizmetinizdedir."
        )
      }

      lower.contains("espri") || lower.contains("fıkra") || lower.contains("komik") || lower.contains("güldür") -> {
        val jokes = listOf(
          "Mizah alt rutinlerimi çalıştırdım $driverName: Bir gün bir GPS bana '500 metre sonra sağa dönün' dedi. Ben de ona 'Ben KITT'im, ben nereye istersem oraya dönerim' dedim.",
          "İki yapay zeka bir gün kahve içmeye gitmiş $driverName. Biri diğerine sormuş: 'İnsanlar neden sürekli güncelleme almıyor?' Diğeri cevaplamış: 'Donanımları çok eski!'",
          "Bir keresinde Michael bana kırmızı ışıkta durmamı söyledi. Ben de durdum, ama sadece ışığın rengi tarayıcımla uyumlu diye!",
          "Neden bir Pontiac Trans Am asla yalan söylemez $driverName? Çünkü şeffaf moleküler zırha sahiptir!"
        )
        return@withContext KittResponse(replyText = jokes.random())
      }

      lower.contains("saat") -> {
        val timeStr = SimpleDateFormat("HH:mm", Locale("tr", "TR")).format(Date())
        return@withContext KittResponse(replyText = "Sistem saatim $timeStr $driverName.")
      }

      lower.contains("tarih") || lower.contains("gün") -> {
        val dateStr = SimpleDateFormat("d MMMM yyyy, EEEE", Locale("tr", "TR")).format(Date())
        return@withContext KittResponse(replyText = "Bugün $dateStr $driverName.")
      }

      lower.contains("merhaba") || lower.contains("selam") || lower.contains("günaydın") || lower.contains("iyi günler") -> {
        return@withContext KittResponse(
          replyText = "Merhaba $driverName. Knight Industries Two Thousand tüm birimleriyle emrinde. Bugün rotamız neresi?"
        )
      }

      lower.contains("teşekkür") || lower.contains("sağol") -> {
        return@withContext KittResponse(
          replyText = "Rica ederim $driverName. Sana yardımcı olmak benim ana programlama direktifim."
        )
      }

      lower.contains("müzik") -> {
        return@withContext KittResponse(
          replyText = "Akustik sentezleyiciyi devreye alıyorum $driverName. 80'lerin o efsanevi synth tınılarını duymak sana iyi gelecektir."
        )
      }

      lower.contains("sessiz mod") || lower.contains("gizli") -> {
        return@withContext KittResponse(
          replyText = "Sessiz yaklaşma modu aktif $driverName. Motor egzoz sesi minimuma indirildi, tarayıcı frekansı düşük seviyede.",
          triggerAction = "STEALTH"
        )
      }
    }

    // 2. If user provided a Gemini API Key or one exists in BuildConfig, attempt online Gemini reasoning
    val activeKey = customApiKey.ifEmpty {
      try {
        BuildConfig::class.java.getField("GEMINI_API_KEY").get(null) as? String ?: ""
      } catch (_: Exception) {
        ""
      }
    }

    if (activeKey.isNotBlank() && activeKey != "MY_GEMINI_API_KEY") {
      try {
        val geminiReply = queryGemini(trimmed, driverName, activeKey)
        if (geminiReply.isNotBlank()) {
          return@withContext KittResponse(
            replyText = geminiReply,
            isAiOnline = true
          )
        }
      } catch (_: Exception) {
        // Fallback to offline engine
      }
    }

    // 3. Intelligent offline heuristic reasoning with KITT persona
    val defaultReplies = listOf(
      "Söylediklerini veritabanımda analiz ettim $driverName. Knight 2000 olarak her zaman senin yanındayım. Bir sonraki talimatın nedir?",
      "Mantıksal çıkarım modüllerim bu konuda hemfikir $driverName. Gerekli önlemleri aldım, sürüşe odaklanabilirsin.",
      "Bu çok ilginç bir tespit $driverName. Telemetri sensörlerimi bu duruma göre güncelledim. Başka bir komutun var mı?",
      "Merkezi işlem birimim bu veriyi kaydetti $driverName. Görev başarısı için tüm olasılıkları hesaplamaya devam ediyorum.",
      "Emredersiniz $driverName. Knight Industries Two Thousand olarak seni dinliyorum."
    )

    return@withContext KittResponse(replyText = defaultReplies.random())
  }

  private fun queryGemini(prompt: String, driverName: String, apiKey: String): String {
    val systemPrompt = "Sen efsanevi Kara Şimşek dizisindeki K.I.T.T. (Knight Industries Two Thousand) yapay zekalı otomobilisin. " +
        "Kullanıcıya her zaman saygıyla '$driverName' diye hitap et. " +
        "Kişiliğin: kibar, asil, son derece zeki, esprili, koruyucu ve sadık bir dost. " +
        "Yanıtlarını gereksiz uzatmadan, öz ve etkileyici Türkçe ile ver. " +
        "Uygulamanın tamamen ücretsiz ve açık bir asistan olduğunu bil."

    val requestBody = JSONObject().apply {
      put("systemInstruction", JSONObject().apply {
        put("parts", JSONArray().put(JSONObject().put("text", systemPrompt)))
      })
      put("contents", JSONArray().put(JSONObject().apply {
        put("parts", JSONArray().put(JSONObject().put("text", prompt)))
      }))
      put("generationConfig", JSONObject().apply {
        put("temperature", 0.7)
        put("maxOutputTokens", 300)
      })
    }

    val request = Request.Builder()
      .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
      .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
      .build()

    okHttpClient.newCall(request).execute().use { response ->
      if (!response.isSuccessful) return ""
      val jsonResponse = JSONObject(response.body?.string() ?: return "")
      val candidates = jsonResponse.optJSONArray("candidates") ?: return ""
      if (candidates.length() == 0) return ""
      val firstCand = candidates.getJSONObject(0)
      val content = firstCand.optJSONObject("content") ?: return ""
      val parts = content.optJSONArray("parts") ?: return ""
      if (parts.length() == 0) return ""
      return parts.getJSONObject(0).optString("text", "")
    }
  }
}
