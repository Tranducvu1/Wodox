package com.wodox.ui.task.optioncreate

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wodox.common.navigation.ChatNavigator
import com.wodox.common.navigation.DocNavigator
import com.wodox.common.navigation.HomeNavigator
import com.wodox.core.base.fragment.BaseFragment
import com.wodox.core.extension.addSpaceDecoration
import com.wodox.core.extension.debounceClick
import com.wodox.domain.home.model.local.Item
import com.wodox.domain.home.model.local.ItemType
import com.wodox.domain.home.model.local.Music
import com.wodox.domain.home.model.local.ReminderData
import com.wodox.home.R
import com.wodox.home.databinding.DialogReminderLayoutBinding
import com.wodox.home.databinding.DialogSpotifyMusicListBinding
import com.wodox.home.databinding.FragmentCreateOptionLayoutBinding

import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class OptionCreateFragment : BaseFragment<FragmentCreateOptionLayoutBinding, OptionCreateViewModel>(
    OptionCreateViewModel::class
) {
    @Inject
    lateinit var homeNavigator: HomeNavigator

    @Inject
    lateinit var chatNavigator: ChatNavigator

    @Inject
    lateinit var docNavigator: DocNavigator

    private val subTaskAdapter by lazy {
        ItemAdapter(
            context, object : ItemAdapter.OnItemClickListener {
                override fun onClick(item: Item) {
                    when (item.type) {
                        ItemType.TASK -> {
                            homeNavigator.openCreateTask(childFragmentManager)
                        }

                        ItemType.CHANNEL -> {
                            chatNavigator.openChannelList(requireContext())
                        }

                        ItemType.REMINDER -> {
                            showReminderDialog()
                        }

                        ItemType.DOC -> {
                            docNavigator.openDocsDetail(requireContext())
                        }

                        else -> {
                        }
                    }
                }
            })
    }

    private var selectedDate: Calendar = Calendar.getInstance()
    private var selectedMusic: Music? = null

    override fun initialize() {
        setupUi()
    }

    override fun layoutId(): Int = R.layout.fragment_create_option_layout

    private fun setupUi() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        setupRecycleView()
    }

    private fun setupRecycleView() {
        val spacing = resources.getDimensionPixelSize(com.wodox.core.R.dimen.dp_2)

        binding.rvItem.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = subTaskAdapter
            addSpaceDecoration(spacing, true)
        }
    }

    private fun showReminderDialog() {
        val binding = DialogReminderLayoutBinding.inflate(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(requireContext()).setView(binding.root).create()

        binding.cardDate.setOnClickListener {
            showDatePicker { year, month, day ->
                selectedDate.set(year, month, day)
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                binding.tvDate.text = dateFormat.format(selectedDate.time)
            }
        }

        binding.cardTime.setOnClickListener {
            showTimePicker { hour, minute ->
                selectedDate.set(Calendar.HOUR_OF_DAY, hour)
                selectedDate.set(Calendar.MINUTE, minute)
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                binding.tvTime.text = timeFormat.format(selectedDate.time)
            }
        }

        binding.cardSelectMusic.setOnClickListener {
            showSpotifyMusicDialog { music ->
                selectedMusic = music
                binding.tvSelectedMusic.text = music.name
                dialog.dismiss()
                showReminderDialog()
            }
        }

        binding.cardRepeat.setOnClickListener {
            showRepeatOptions { repeat ->
                binding.tvRepeat.text = repeat
            }
        }

        selectedMusic?.let {
            binding.tvSelectedMusic.text = it.name
        }

        binding.btnCancel.debounceClick {
            dialog.dismiss()
        }

        binding.btnSave.debounceClick {
            val reminderData = ReminderData(
                date = selectedDate.time,
                music = selectedMusic,
                repeat = binding.tvRepeat.text.toString()
            )
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDatePicker(onDateSelected: (Int, Int, Int) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                onDateSelected(year, month, day)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker(onTimeSelected: (Int, Int) -> Unit) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            requireContext(), { _, hour, minute ->
                onTimeSelected(hour, minute)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
        ).show()
    }

    private fun showSpotifyMusicDialog(onMusicSelected: (Music) -> Unit) {
        val binding = DialogSpotifyMusicListBinding.inflate(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(requireContext()).setView(binding.root).create()

        lateinit var musicAdapter: MusicAdapter

        musicAdapter = MusicAdapter { music ->
            onMusicSelected(music)
            musicAdapter.releaseMediaPlayer()
            dialog.dismiss()
        }

        binding.rvMusicList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = musicAdapter
        }

        val sampleMusic = listOf(
            Music(
                "1",
                "Blinding Lights",
                "The Weeknd",
                "3:20",
                "",
                "https://p.scdn.co/mp3-preview/..."
            ), Music(
                "2",
                "Save Your Tears",
                "The Weeknd",
                "3:35",
                "",
                "https://p.scdn.co/mp3-preview/..."
            ), Music(
                "3", "Levitating", "Dua Lipa", "3:23", "", "https://p.scdn.co/mp3-preview/..."
            ), Music(
                "4", "Good 4 U", "Olivia Rodrigo", "2:58", "", "https://p.scdn.co/mp3-preview/..."
            ), Music(
                "5", "Stay", "The Kid LAROI", "2:21", "", "https://p.scdn.co/mp3-preview/..."
            )
        )
        musicAdapter.submitList(sampleMusic)

        binding.etSearch.setOnEditorActionListener { _, _, _ ->
            val query = binding.etSearch.text.toString()
            val filtered = sampleMusic.filter {
                it.name.contains(query, ignoreCase = true) || it.artist.contains(
                    query, ignoreCase = true
                )
            }
            musicAdapter.submitList(filtered)
            true
        }

        binding.btnClose.setOnClickListener {
            musicAdapter.releaseMediaPlayer()
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            musicAdapter.releaseMediaPlayer()
        }

        dialog.show()
    }

    private fun showRepeatOptions(onRepeatSelected: (String) -> Unit) {
        val repeatOptions = arrayOf(
            "Once", "Daily", "Weekly", "Monthly", "Yearly"
        )

        MaterialAlertDialogBuilder(requireContext()).setTitle("Repeat")
            .setItems(repeatOptions) { dialog, which ->
                onRepeatSelected(repeatOptions[which])
                dialog.dismiss()
            }.show()
    }

    companion object {
        fun newInstance() = OptionCreateFragment()
    }
}
