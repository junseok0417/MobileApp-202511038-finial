package com.example.jaru
fun add(a: Int, b: Int) = a + b

fun main() {
    println("hello")
    println("hello")
    println("hello")
    val result = add(10,20)
    println("$result")

    val score = 75

    val grade = if (score >= 90) {
        "A"
    }
    else if (score >= 80) {
        "B"
    }
    else if (score >= 70) {
        "C"
    }
    else {
        "F"
    }

    println("you ${grade}")  // 👉 "당신의 학점은 C 입니다."
}


