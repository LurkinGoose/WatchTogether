package com.example.watch_together.auth

object InputValidator {

    private val profanityList = listOf(
        "arse", "arsehead", "arsehole", "ass", "ass hole", "asshole",
        "bastard", "bitch", "bloody", "bollocks", "brotherfucker", "bugger",
        "bullshit", "child-fucker", "Christ on a bike", "Christ on a cracker",
        "cock", "cocksucker", "crap", "cunt", "dammit", "damn", "damned", "damn it",
        "dick", "dick-head", "dickhead", "dumb ass", "dumb-ass", "dumbass", "dyke",
        "faggot", "father-fucker", "fatherfucker", "fuck", "fucked", "fucker", "fucking",
        "god dammit", "goddammit", "God damn", "god damn", "goddamn", "Goddamn", "goddamned",
        "goddamnit", "godsdamn", "hell", "holy shit", "horseshit", "in shit", "jackarse",
        "jack-ass", "jackass", "Jesus Christ", "Jesus fuck", "Jesus Harold Christ", "Jesus H. Christ",
        "Jesus, Mary and Joseph", "Jesus wept", "kike", "mother fucker", "mother-fucker", "motherfucker",
        "nigga", "nigra", "pigfucker", "piss", "prick", "pussy", "shit", "shit ass", "shite", "sibling fucker",
        "sisterfuck", "sisterfucker", "slut", "son of a bitch", "son of a whore", "spastic", "sweet Jesus",
        "twat", "wanker"
    )

    fun isValidName(name: String): Boolean {
        return name.length >= 2 &&
                name.all { it.isAsciiLetterOrDigit() } &&
                profanityList.none { name.contains(it, ignoreCase = true) }
    }

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6 &&
                password.all { it.isAsciiLetterOrDigit() }
    }

    private fun Char.isAsciiLetterOrDigit(): Boolean {
        return this.code in 48..57 || this.code in 65..90 || this.code in 97..122
    }
}
