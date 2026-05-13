package com.example.pz3

object RomanConverter {
    private val romanMap = mapOf(
        1000 to "M", 900 to "CM", 500 to "D", 400 to "CD",
        100 to "C", 90 to "XC", 50 to "L", 40 to "XL",
        10 to "X", 9 to "IX", 5 to "V", 4 to "IV", 1 to "I"
    )

    fun toRoman(number: Int): String {
        if (number <= 0) return "N/A"
        var num = number
        val sb = StringBuilder()
        for ((value, symbol) in romanMap) {
            while (num >= value) {
                sb.append(symbol)
                num -= value
            }
        }
        return sb.toString()
    }

    fun fromRoman(roman: String): Int? {
        var res = 0
        var i = 0
        val s = roman.uppercase()
        val values = mapOf(
            'I' to 1, 'V' to 5, 'X' to 10, 'L' to 50,
            'C' to 100, 'D' to 500, 'M' to 1000
        )

        while (i < s.length) {
            val s1 = values[s[i]] ?: return null
            if (i + 1 < s.length) {
                val s2 = values[s[i + 1]] ?: return null
                if (s1 >= s2) {
                    res += s1
                    i++
                } else {
                    res += s2 - s1
                    i += 2
                }
            } else {
                res += s1
                i++
            }
        }
        return if (toRoman(res) == s) res else null // Basic validation
    }
}
