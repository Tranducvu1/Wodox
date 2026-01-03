package com.wodox.ui.task.optioncreate

import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wodox.core.extension.debounceClick
import com.wodox.domain.home.model.local.Music
import com.wodox.home.R
import com.wodox.home.databinding.ItemMusicLayoutBinding

class MusicAdapter(
    private val onMusicClick: (Music) -> Unit
) : RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    private var musicList = listOf<Music>()
    private var selectedMusicId: String? = null
    private var currentPlayingId: String? = null
    private var mediaPlayer: MediaPlayer? = null

    fun submitList(list: List<Music>) {
        musicList = list
        notifyDataSetChanged()
    }

    fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
        currentPlayingId = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val binding = ItemMusicLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MusicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        holder.bind(musicList[position])
    }

    override fun getItemCount() = musicList.size

    inner class MusicViewHolder(
        private val binding: ItemMusicLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(music: Music) {
            binding.tvSongName.text = music.name
            binding.tvArtistName.text = music.artist
            binding.tvDuration.text = music.duration

            binding.ivSelected.visibility =
                if (music.id == selectedMusicId) View.VISIBLE else View.GONE

            val isPlaying = music.id == currentPlayingId
            if (isPlaying) {
                binding.ivPlayIcon.setImageResource(R.drawable.ic_pause)
                binding.btnPlay.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(0xFF1DB954.toInt())
            } else {
                binding.ivPlayIcon.setImageResource(R.drawable.ic_play)
                binding.btnPlay.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(0xFF1DB954.toInt())
            }

            binding.root.setOnClickListener {
                selectedMusicId = music.id
                onMusicClick(music)
                notifyDataSetChanged()
            }

            binding.btnPlay.debounceClick {
                if (currentPlayingId == music.id) {
                    stopMusic()
                } else {
                    playMusic(music)
                }
                notifyDataSetChanged()
            }

            if (music.albumArtUrl.isNotEmpty()) {

            }
        }

        private fun playMusic(music: Music) {
            try {
                mediaPlayer?.release()
                mediaPlayer = null

                mediaPlayer = MediaPlayer().apply {
                    if (music.previewUrl.isNotEmpty()) {
                        setDataSource(music.previewUrl)
                    } else {
                        setDataSource("https://example.com/preview.mp3")
                    }

                    prepareAsync()

                    setOnPreparedListener {
                        start()
                        currentPlayingId = music.id
                        notifyDataSetChanged()
                    }

                    setOnCompletionListener {
                        currentPlayingId = null
                        notifyDataSetChanged()
                    }

                    setOnErrorListener { _, _, _ ->
                        currentPlayingId = null
                        notifyDataSetChanged()
                        true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                currentPlayingId = null
                notifyDataSetChanged()
            }
        }

        private fun stopMusic() {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
            mediaPlayer = null
            currentPlayingId = null
        }
    }
}