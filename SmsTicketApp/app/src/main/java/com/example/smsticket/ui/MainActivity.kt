package com.example.smsticket.ui

import android.os.Bundle
import android.text.InputFilter
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smsticket.databinding.ActivityMainBinding
import com.example.smsticket.viewmodel.TicketViewModel

/**
 * Single-screen SMS simulation activity.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: TicketViewModel by viewModels()
    private val adapter = MessageAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInput()
        setupRecyclerView()
        observeViewModel()
        setupActions()
    }

    private fun setupInput() {
        binding.etBoardNumber.filters = arrayOf(InputFilter.LengthFilter(4))
    }

    private fun setupRecyclerView() {
        binding.rvMessages.layoutManager = LinearLayoutManager(this)
        binding.rvMessages.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.messages.observe(this) { messages ->
            adapter.submitList(messages)
            if (messages.isNotEmpty()) {
                binding.rvMessages.scrollToPosition(messages.lastIndex)
            }
        }

        viewModel.inputError.observe(this) { errorText ->
            binding.tilBoardNumber.error = errorText
        }
    }

    private fun setupActions() {
        binding.btnSend.setOnClickListener {
            sendBoardNumber()
        }

        binding.etBoardNumber.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendBoardNumber()
                true
            } else {
                false
            }
        }
    }

    private fun sendBoardNumber() {
        val inputText = binding.etBoardNumber.text?.toString().orEmpty()
        val wasSent = viewModel.sendBoardNumber(inputText)
        if (wasSent) {
            binding.etBoardNumber.text?.clear()
        }
    }
}
