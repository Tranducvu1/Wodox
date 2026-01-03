package com.wodox.mywork.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wodox.mywork.databinding.DialogCreateNotificationBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

@AndroidEntryPoint
class SendReminderDialogFragment : DialogFragment() {

    private lateinit var binding: DialogCreateNotificationBinding
    private lateinit var viewModel: MyWorkViewModel
    private var taskId: UUID? = null
    private var taskName: String? = null
    private var selectedDate: Long? = null
    private var selectedNotificationType: String = "SOUND"

    companion object {
        private const val ARG_TASK_ID = "task_id"
        private const val ARG_TASK_NAME = "task_name"

        fun newInstance(taskId: UUID, taskName: String): SendReminderDialogFragment {
            return SendReminderDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TASK_ID, taskId.toString())
                    putString(ARG_TASK_NAME, taskName)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskId = arguments?.getString(ARG_TASK_ID)?.let { UUID.fromString(it) }
        taskName = arguments?.getString(ARG_TASK_NAME)
        viewModel = ViewModelProvider(requireActivity()).get(MyWorkViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogCreateNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        binding.tvTaskName.text = taskName

        val notificationTypes = arrayOf(
            "🔊 Âm thanh",
            "📳 Rung",
            "🔇 Im lặng"
        )
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            notificationTypes
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerNotificationType.adapter = adapter
        binding.spinnerNotificationType.setSelection(0)
    }

    private fun setupListeners() {
        binding.btnSelectDateTime.setOnClickListener {
            showDateTimePicker()
        }

        binding.btnSendReminder.setOnClickListener {
            if (validateInputs()) {
                sendReminder()
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.spinnerNotificationType.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedNotificationType = when (position) {
                        0 -> "SOUND"
                        1 -> "VIBRATION"
                        else -> "SILENT"
                    }
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            }
    }

    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        calendar.set(year, month, dayOfMonth, hourOfDay, minute)
                        selectedDate = calendar.timeInMillis
                        updateDateTimeDisplay()
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateTimeDisplay() {
        selectedDate?.let {
            val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            binding.tvSelectedDateTime.text = format.format(Date(it))
        }
    }

    private fun validateInputs(): Boolean {
        val title = binding.etTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()

        return when {
            title.isEmpty() -> {
                binding.etTitle.error = "Vui lòng nhập tiêu đề"
                false
            }
            description.isEmpty() -> {
                binding.etDescription.error = "Vui lòng nhập mô tả"
                false
            }
            selectedDate == null -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Lỗi")
                    .setMessage("Vui lòng chọn thời gian nhắc nhở")
                    .setPositiveButton("OK") { _, _ -> }
                    .show()
                false
            }
            else -> true
        }
    }

    private fun sendReminder() {
        val title = binding.etTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()

        viewModel.handleAction(
            MyWorkUiAction.SendTaskReminder(
                taskId = taskId ?: UUID.randomUUID(),
                taskName = taskName ?: "Task",
                title = title,
                description = description,
                endTime = selectedDate ?: System.currentTimeMillis(),
                notificationType = selectedNotificationType
            )
        )
        dismiss()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}