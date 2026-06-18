package com.masareefy.app.domain

import java.time.LocalDate

data class ParsedExpense(
    val amount: Double?,
    val category: String?,
    val date: LocalDate,
    val note: String?,
    val confidence: Float
)

object VoiceParser {

    // Arabic number words dictionary — supports Egyptian dialect
    private val onesMap = mapOf(
        "واحد" to 1, "اتنين" to 2, "تنين" to 2, "تلاتة" to 3, "تلاته" to 3,
        "اربعة" to 4, "اربعه" to 4, "خمسة" to 5, "خمسه" to 5,
        "ستة" to 6, "سته" to 6, "سبعة" to 7, "سبعه" to 7,
        "تمانية" to 8, "تمانيه" to 8, "تسعة" to 9, "تسعه" to 9,
        "عشرة" to 10, "عشره" to 10, "عشرين" to 20, "تلاتين" to 30,
        "اربعين" to 40, "خمسين" to 50, "ستين" to 60, "سبعين" to 70,
        "تمانين" to 80, "تسعين" to 90
    )

    private val hundredsMap = mapOf(
        "ميه" to 100, "مية" to 100, "ميتين" to 200, "تلتميه" to 300,
        "تلتمية" to 300, "اربعميه" to 400, "اربعمية" to 400,
        "خمسميه" to 500, "خمسمية" to 500, "ستميه" to 600, "ستمية" to 600,
        "سبعميه" to 700, "سبعمية" to 700, "تمنميه" to 800, "تمنمية" to 800,
        "تسعميه" to 900, "تسعمية" to 900
    )

    private val thousandsMap = mapOf(
        "الف" to 1000, "ألف" to 1000, "الفين" to 2000, "ألفين" to 2000,
        "تلت الاف" to 3000, "اربع الاف" to 4000, "خمس الاف" to 5000,
        "عشر الاف" to 10000
    )

    private val categoryKeywords = mapOf(
        listOf("اكل", "أكل", "مطعم", "طعام", "فطار", "غدا", "غداء", "عشا", "عشاء",
               "ماكدونالدز", "كنتاكي", "بيتزا", "كافيه", "مقهي", "مقهى", "فول", "طعميه") 
            to "أكل ومطاعم",
        listOf("مواصلات", "اوبر", "أوبر", "كريم", "تاكسي", "ميكروباص", "اتوبيس",
               "مترو", "بنزين", "وقود", "عربيه", "عربية", "سيارة") 
            to "نقل ومواصلات",
        listOf("سوبرماركت", "بقاله", "بقالة", "جمله", "جملة", "كارفور", "هايبر",
               "اسواق", "أسواق", "خضار", "فاكهه", "فاكهة") 
            to "بقالة وسوبرماركت",
        listOf("ايجار", "إيجار", "ايجاار", "فاتوره", "فاتورة", "كهربا", "كهرباء",
               "ميه", "مياه", "غاز", "سكن", "شقه", "شقة") 
            to "إيجار وفواتير",
        listOf("صيدليه", "صيدلية", "دكتور", "دوا", "دواء", "مستشفي", "مستشفى",
               "عيادة", "عياده", "تحاليل", "اشعه", "أشعة", "صحه", "صحة") 
            to "صحة ودواء",
        listOf("موبايل", "تليفون", "انترنت", "فودافون", "اتصالات", "اورانج",
               "we", "فاتوره موبايل", "شحن") 
            to "فواتير واتصالات",
        listOf("هدوم", "ملابس", "جاكيت", "حذاء", "احذيه", "شنطه", "تسوق",
               "مول", "شوبينج") 
            to "ملابس وتسوق",
        listOf("سينما", "ترفيه", "نادي", "رياضه", "رياضة", "جيم", "العاب",
               "تذكره", "حفله", "حفلة") 
            to "ترفيه ورياضة"
    )

    fun parseArabicExpenseText(input: String): ParsedExpense {
        // Normalize Arabic text
        val normalized = input
            .replace("أ", "ا").replace("إ", "ا").replace("آ", "ا")
            .replace("ة", "ه").replace("ى", "ي")
            .lowercase()
            .trim()

        val amount = extractAmount(normalized, input)
        val category = extractCategory(normalized)
        val date = extractDate(normalized)
        val note = extractNote(input)
        val confidence = if (amount != null && category != "أخرى") 0.9f 
                         else if (amount != null) 0.7f else 0.4f

        return ParsedExpense(
            amount = amount,
            category = category,
            date = date,
            note = note,
            confidence = confidence
        )
    }

    private fun extractAmount(normalized: String, original: String): Double? {
        // 1. Try to find Arabic-Indic or Western digits first
        val digitRegex = Regex("[٠-٩0-9]+(?:[.,][٠-٩0-9]+)?")
        val digitMatch = digitRegex.find(original)
        if (digitMatch != null) {
            return digitMatch.value
                .replace("٠","0").replace("١","1").replace("٢","2")
                .replace("٣","3").replace("٤","4").replace("٥","5")
                .replace("٦","6").replace("٧","7").replace("٨","8")
                .replace("٩","9").replace(",","").replace("،","")
                .toDoubleOrNull()
        }

        // 2. Try compound Arabic number words
        return parseArabicNumberWords(normalized)
    }

    private fun parseArabicNumberWords(text: String): Double? {
        var total = 0.0
        var found = false

        // Check thousands first
        for ((key, value) in thousandsMap) {
            if (text.contains(key)) {
                total += value
                found = true
                break
            }
        }

        // Check hundreds
        for ((key, value) in hundredsMap) {
            if (text.contains(key)) {
                total += value
                found = true
                break
            }
        }

        // Check tens and ones — collect ALL matches (handles "مية وخمسين")
        var maxOnesFound = 0
        for ((key, value) in onesMap) {
            if (text.contains(key) && value > maxOnesFound) {
                // Check if this is in context of a compound number
                maxOnesFound = value
                found = true
            }
        }
        if (maxOnesFound > 0) {
            total += maxOnesFound
        }

        return if (found && total > 0) total else null
    }

    private fun extractCategory(normalized: String): String {
        for ((keywords, category) in categoryKeywords) {
            for (keyword in keywords) {
                if (normalized.contains(keyword)) return category
            }
        }
        return "أخرى"
    }

    private fun extractDate(normalized: String): LocalDate {
        val today = LocalDate.now()
        return when {
            normalized.contains("امبارح") || normalized.contains("إمبارح") || 
            normalized.contains("امبارح") -> today.minusDays(1)
            normalized.contains("اول امبارح") -> today.minusDays(2)
            normalized.contains("الاسبوع اللي فات") -> today.minusWeeks(1)
            else -> today
        }
    }

    private fun extractNote(original: String): String {
        // Remove amount-related words and return the rest as note
        return original
            .replace(Regex("صرفت|دفعت|اشتريت|مصروف|النهارده|امبارح|اليوم|جنيه|ج.م"), "")
            .trim()
            .takeIf { it.isNotBlank() } ?: original
    }
}
