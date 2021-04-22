package com.example.android.politicalpreparedness.network.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class Division(
        val id: String,
        val country: String,
        val state: String
)
fun Division.toFormattedString(): String = listOf(state, country)
        .filter { it.isNotEmpty() }
        .joinToString(separator = ", ")
