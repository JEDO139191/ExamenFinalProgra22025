package com.example.examenfinalapp.ui.viewmodel

fun isValidEmail(s: String) = s.contains("@") && s.contains(".")
fun isValidPassword(s: String) = s.length >= 6
fun notBlank(s: String) = s.isNotBlank()
