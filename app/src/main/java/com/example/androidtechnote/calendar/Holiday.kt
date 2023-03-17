package com.example.androidtechnote.calendar

import com.google.gson.annotations.SerializedName

data class Holiday (
    @SerializedName("kind")val kind: String,
    @SerializedName("etag")val etag: String,
    @SerializedName("summary")val summary: String,
    @SerializedName("updated")val updated: String,
    @SerializedName("items")val items: List<Item>
)

data class Item (
    @SerializedName("kind")val kind: String,
    @SerializedName("etag")val etag: String,
    @SerializedName("id")val id: String,
    @SerializedName("status")val status: String,
    @SerializedName("htmlLink")val htmlLink: String,
    @SerializedName("created")val created: String,
    @SerializedName("updated")val updated: String,
    @SerializedName("summary")val summary: String,
    @SerializedName("description")val description: String,
    @SerializedName("creator")val creator: Creator,
    @SerializedName("organizer")val organizer: Creator,
    @SerializedName("start")val startDate: Date,
    @SerializedName("end")val endDate: Date,
    @SerializedName("visibility")val visibility: String,
    @SerializedName("iCalUID")val iCalUID: String,
    @SerializedName("eventType")val eventType: String
)

data class Creator (
    @SerializedName("email")
    val email: String,
    @SerializedName("displayName")
    val displayName: String,
    @SerializedName("self")
    val self: Boolean
)

data class Date (
    @SerializedName("date")
    val date: String
)