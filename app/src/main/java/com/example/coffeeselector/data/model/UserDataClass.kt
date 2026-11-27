package com.example.coffeeselector.data.model


import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import java.util.Calendar
import java.util.Date


/**
 * https://developer.android.com/reference/java/util/Date#Date(int,%20int,%20int)
 * regex pattern code:
 * https://www.servicenow.com/community/itsm-forum/required-regex-for-mobile-number-validation/m-p/651990
 * https://blog.logrocket.com/guide-regular-expression-kotlin/
 * Regular expression:
 * https://www.baeldung.com/kotlin/regular-expressions
 * https://stackoverflow.com/questions/5871793/remove-punctuation-from-string-with-regex
 */
data class UserDataClass(
    @DocumentId
    val id: String = "",
    val username: String = "",
    val name: NameDataClass = NameDataClass("", ""),
    val phoneNo: String = "",
    val address: AddressDataClass? = null,
    val dateOfBirth: Date? = null,
    val recentOrders: List<String>? = null,
    val unfinishedOrder: String? = null,
    val notificationsId:  List<String>? = null
) {
    fun validate(): List<String> {
        val errors = mutableListOf<String>()

        if (!usernameRegex.matches(username)) {
            errors.add("Username must be 3–20 characters (letters, numbers, or underscores) " +
                    "and cannot be blank.")
        }
        errors.addAll(name.validate())
        if (!phoneNoRegex.matches(phoneNo)) {
            errors.add("Phone number format is invalid and cannot be blank")
        }
        if (dateOfBirth != null) {
            val thirteenYearsAgo = Calendar.getInstance().apply {
                add(Calendar.YEAR, -13)
            }.time
            if(dateOfBirth.after(Date())) errors.add("Date of birth cannot be in the future.")
            else if(dateOfBirth.after(thirteenYearsAgo)) errors.add("Must be at least 13 years old")
        }


        return errors
    }
    @Exclude
    fun isValid(): Boolean = validate().isEmpty()

    companion object {
        private val phoneNoRegex = Regex("""^(?:\+44|0)(?:\s?\d){10}$""")
        private val usernameRegex = Regex("""^[a-zA-Z0-9_]{3,20}$""")
    }
}
