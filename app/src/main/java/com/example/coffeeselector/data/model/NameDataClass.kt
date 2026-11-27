package com.example.coffeeselector.data.model

import com.google.firebase.database.Exclude

data class NameDataClass(
    val first: String = "",
    val last: String = ""
) {

    fun validate(): List<String> {
        val errors = mutableListOf<String>()
        if (!nameRegex.matches(first)) {
            errors.add("First name must be 1–35 characters (letters, spaces, or hyphens) " +
                    "and cannot be blank.")
        }
        if (!nameRegex.matches(last)) {
            errors.add("Last name must be 1–35 characters (letters, spaces, or hyphens) " +
                    "and cannot be blank.")
        }
        return errors
    }
    @Exclude
    fun isValid(): Boolean = validate().isEmpty()

    companion object {
        private val nameRegex = Regex("""^(?=.*[a-z])[a-z ,.'-]{1,35}$""", RegexOption.IGNORE_CASE)
    }
}
