package com.example.domain

import java.time.LocalDate

data class ParsedExpense(
    val amount: Double?,
    val category: String?,
    val date: LocalDate,
    val note: String?,
    val confidence: Float
)

object VoiceParser {
    fun parseArabicExpenseText(input: String): ParsedExpense {
        val normalized = input
            .replace("أ", "ا").replace("إ", "ا").replace("آ", "ا")
            .replace("ة", "ه").replace("ى", "ي")

        var amount: Double? = null
        var category = "أخرى"
        var date = LocalDate.now()
        val note = input

        // Very basic extraction for amounts (regex for numbers)
        val numRegex = Regex("(\\d+)")
        val match = numRegex.find(input)
        if (match != null) {
            amount = match.value.toDouble()
        } else {
            // Check text numbers
            val numberMap = mapOf(
                "عشره" to 10.0, "عشرين" to 20.0, "تلاتين" to 30.0, "اربعين" to 40.0, "خمسين" to 50.0,
                "ستين" to 60.0, "سبعين" to 70.0, "تمانين" to 80.0, "تسعين" to 90.0, "ميه" to 100.0,
                "ميتين" to 200.0, "تلتميه" to 300.0, "الف" to 1000.0, "الفين" to 2000.0
            )
            for ((key, value) in numberMap) {
                if (normalized.contains(key)) {
                    amount = value
                    // Try compounds (rough)
                    break
                }
            }
        }

        // Category keywords
        val catMap = mapOf(
            "اكل" to "أكل ومطاعم",
            "مطعم" to "أكل ومطاعم",
            "غدا" to "أكل ومطاعم",
            "فطار" to "أكل ومطاعم",
            "مواصلات" to "نقل ومواصلات",
            "اوبر" to "نقل ومواصلات",
            "بنزين" to "نقل ومواصلات",
            "سوبرماركت" to "بقالة",
            "صيدليه" to "صحة",
            "دوا" to "صحة",
            "ايجار" to "إيجار وسكن"
        )

        for ((key, value) in catMap) {
            if (normalized.contains(key)) {
                category = value
                break
            }
        }

        if (normalized.contains("امبارح")) {
            date = date.minusDays(1)
        }

        val confidence = if (amount != null && category != "أخرى") 0.9f else 0.5f

        return ParsedExpense(
            amount = amount,
            category = category,
            date = date,
            note = note,
            confidence = confidence
        )
    }
}
