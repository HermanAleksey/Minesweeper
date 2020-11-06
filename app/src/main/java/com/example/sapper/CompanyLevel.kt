package com.example.sapper

data class CompanyLevel(
    val id: Int,
    val height: Int,
    val width: Int,
    val minesCount: Int,
    val minutes: Int,
    val seconds: Int,
    var completed: Boolean
) {
}