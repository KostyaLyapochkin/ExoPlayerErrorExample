package com.example.exoplayererrorcodecsample

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_player.*

class PlayerFragment : Fragment() {

    private var videoService: IVideoService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        retainInstance = true

        val intent = Intent(requireContext(), VideoService::class.java)

        val serviceConnection: ServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                videoService = service as IVideoService
                preparePlayerIfNeed()
                tryToAttachView()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                videoService = null
            }
        }

        requireContext().startService(intent)
        requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_player, container, false)

    override fun onStart() {
        super.onStart()
        tryToAttachView()
    }

    private fun preparePlayerIfNeed() {
        videoService?.preparePlayerIfNeed()
    }

    private fun tryToAttachView() {
        if (videoService != null && playerView != null) {
            videoService?.attachView(playerView)
        }
    }

    override fun onStop() {
        super.onStop()
        videoService?.detachView(playerView)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (!requireActivity().isChangingConfigurations) {
            videoService?.destroy()
            videoService = null
        }
    }

    companion object {
        const val PLAYER_FRAGMENT_TAG = "PLAYER_FRAGMENT_TAG"
    }
}