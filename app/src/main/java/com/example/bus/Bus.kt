package com.example.bus
data class Bus(
    val busId: String = "",
    val students: List<Student> = emptyList(),
    val university: String = ""
)
