package com.wodox.chat.ui.chat

import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wodox.core.extension.addSpaceDecoration
import com.wodox.core.extension.launchWhenStarted
import com.wodox.chat.R
import com.wodox.chat.databinding.FragmentChatLayoutBinding
import com.wodox.chat.ui.channel.ChannelAdapter
import com.wodox.common.navigation.ChatNavigator
import com.wodox.domain.chat.model.UserWithFriendStatus
import com.wodox.domain.chat.model.local.Notification
import com.wodox.common.navigation.HomeNavigator
import com.wodox.core.base.fragment.BaseFragment
import com.wodox.core.extension.debounceClick
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatLayoutBinding, ChatViewModel>(ChatViewModel::class) {
    override fun layoutId(): Int = R.layout.fragment_chat_layout

    @Inject
    lateinit var homeNavigator: HomeNavigator

    @Inject
    lateinit var chatNavigator: ChatNavigator
    private val adapterUser by lazy {
        UserAdapter(
            context,
            object : UserAdapter.OnItemClickListener {
                override fun onAccept(item: UserWithFriendStatus) {
                    viewModel.dispatch(ChatUiAction.AcceptFriend(item.user.id))
                }

                override fun onReject(item: UserWithFriendStatus) {
                    viewModel.dispatch(ChatUiAction.RejectFriend(item.user.id))
                }

                override fun onChat(item: UserWithFriendStatus) {
                    chatNavigator.openMessage(requireContext(), item)
                }
            }
        )
    }


    private val channelAdapter by lazy {
        ChannelAdapter(
            onChannelClick = { channel ->
                chatNavigator.openChannelChat(requireContext(), channel.id)
            },
            onJoinClick = { channel ->
                chatNavigator.openChannelChat(requireContext(), channel.id)
            }
        )
    }


    private val notificationAdapter by lazy {
        NotificationAdapter(
            context,
            object : NotificationAdapter.OnItemClickListener {
                override fun onViewTask(notification: Notification) {
                    viewModel.dispatch(ChatUiAction.LoadTask(notification.taskId))
                }

                override fun onMarkDone(notification: Notification) {
                    viewModel.dispatch(ChatUiAction.MarkNotificationAsRead(notification.id))
                }

                override fun onDismiss(notification: Notification) {
                    viewModel.dispatch(ChatUiAction.DismissNotification(notification))
                }
            }
        )
    }

    override fun initialize() {
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        setupUI()
        setupAction()
    }

    private fun setupUI() {
        setupRecycleViewNotification()
        setupRecycleViewPeople()
        observer()
        setupRecycleViewChannels()
    }

    private fun setupAction() {
        binding.ivCreateChannel.debounceClick {
            chatNavigator.openChannelList(requireContext())
        }
        binding.btnViewAllChannels.debounceClick {
            chatNavigator.openChannelList(requireContext())
        }
    }

    private fun setupRecycleViewNotification() {
        val spacing = resources.getDimensionPixelSize(com.wodox.core.R.dimen.dp_4)

        binding.rvNotifications.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = notificationAdapter
            addSpaceDecoration(spacing, false)
        }
    }


    private fun setupRecycleViewPeople() {
        val spacing = resources.getDimensionPixelSize(com.wodox.core.R.dimen.dp_4)

        binding.rvPeople.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = adapterUser
            addSpaceDecoration(spacing, false)
        }
    }

    private fun setupRecycleViewChannels() {
        val spacing = resources.getDimensionPixelSize(com.wodox.core.R.dimen.dp_4)

        binding.rvChannels.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = channelAdapter
            addSpaceDecoration(spacing, false)
        }
    }

    private fun observer() {
        launchWhenStarted {
            viewModel.uiState.collect { it ->
                binding.tvNotificationBadge.text = it.unreadNotificationCount.toString()
                if (it.hasNewNotification) {
                    triggerBellShakeAnimation()
                }
            }
        }
        launchWhenStarted {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    ChatUiEvent.ResetNotificationAnimation -> triggerBellShakeAnimation()
                    ChatUiEvent.NavigatorTask -> openTask()
                }
            }
        }
    }


    private fun openTask() {
        val openTask = viewModel.uiState.value.task ?: return
        homeNavigator.openTask(requireContext(), openTask)
    }

    private fun triggerBellShakeAnimation() {
        val shakeAnimation = AnimationUtils.loadAnimation(
            context,
            R.anim.shake_animation
        )
        binding.ivNotificationSettings.startAnimation(shakeAnimation)
    }


    override fun onResume() {
        super.onResume()
        viewModel.dispatch(ChatUiAction.LoadUser)
    }


    companion object {
        fun newInstance() = ChatFragment()
    }
}