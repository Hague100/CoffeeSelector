package com.example.coffeeselector.data.model

class ValidationException(val errors: List<String>) : IllegalArgumentException(errors.joinToString("\n"))
