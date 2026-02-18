package com.example.smsticket.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smsticket.data.ChatMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

/**
 * ViewModel that validates input, creates ticket messages and exposes the chat list.
 */
class TicketViewModel : ViewModel() {

    private val _messages = MutableLiveData<List<ChatMessage>>(emptyList())
    val messages: LiveData<List<ChatMessage>> = _messages

    private val _inputError = MutableLiveData<String?>()
    val inputError: LiveData<String?> = _inputError

    /**
     * Returns true if the number is valid and messages were sent.
     */
    fun sendBoardNumber(input: String): Boolean {
        val sanitizedInput = input.trim()
        if (!sanitizedInput.matches(Regex("^\\d{4}$"))) {
            _inputError.value = "Introdu exact 4 cifre"
            return false
        }

        _inputError.value = null

        appendMessage(ChatMessage(text = sanitizedInput, isIncoming = false))
        appendMessage(ChatMessage(text = "Solicitarea este in curs de procesare", isIncoming = true))

        viewModelScope.launch {
            delay(2_000)
            appendMessage(ChatMessage(text = buildTicketMessage(sanitizedInput), isIncoming = true))
        }
        return true
    }

    private fun appendMessage(message: ChatMessage) {
        val updatedList = _messages.value.orEmpty().toMutableList().apply { add(message) }
        _messages.value = updatedList
    }

    private fun buildTicketMessage(boardNumber: String): String {
        val locale = Locale("ro", "MD")
        val now = Calendar.getInstance()
        val end = Calendar.getInstance().apply {
            timeInMillis = now.timeInMillis
            add(Calendar.HOUR_OF_DAY, 1)
        }

        val dateFormatter = SimpleDateFormat("dd.MM.yyyy", locale)
        val timeFormatter = SimpleDateFormat("HH:mm", locale)

        return """
            Bilet electronic nr.
            ${UUID.randomUUID()}

            ${dateFormatter.format(now.time)}

            Valabil 1 ora (de la ${timeFormatter.format(now.time)} pina la ${timeFormatter.format(end.time)})

            Pret 6 MDL

            Numar de bord $boardNumber
        """.trimIndent()
    }
}
