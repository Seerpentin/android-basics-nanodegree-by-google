package com.example.smsticket.data

/**
 * Model used by RecyclerView to draw a simple SMS-like chat row.
 */
data class ChatMessage(
    val text: String,
    val isIncoming: Boolean
)
