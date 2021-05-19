package com.example.exoplayererrorcodecsample

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.exoplayererrorcodecsample.PlayerErrorDialogFragment.Companion.PLAYER_ERROR_DIALOG_FRAGMENT_TAG
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.ads.AdsLoader
import com.google.android.exoplayer2.source.ads.AdsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AdViewProvider
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.fragment_player.*
import java.util.*

class PlayerFragment : Fragment(), Player.EventListener {

    private var player: SimpleExoPlayer? = null
    private var adsLoader: AdsLoader? = null
    private val applicationContext by lazy { requireActivity().applicationContext }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_player, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPlayer()

        playerView.player = player
    }

    private fun initPlayer() {
        if (player == null) {
            val mediaItem = createMediaItem()

            val renderFactory = CustomRenderersFactory(applicationContext)
                .setExtensionRendererMode(EXTENSION_RENDERER_MODE_OFF)

            val trackSelector = DefaultTrackSelector(applicationContext)

            val mediaSourceFactory = DefaultMediaSourceFactory(applicationContext)
                .setAdViewProvider { null }
                .setAdsLoaderProvider(::getAdsLoader)

            player = SimpleExoPlayer.Builder(applicationContext, renderFactory)
                .setMediaSourceFactory(mediaSourceFactory)
                .setTrackSelector(trackSelector)
                .build()

            player?.setMediaItem(mediaItem)
            player?.prepare()
        }

        player?.playWhenReady = true
    }

    private fun getAdsLoader(adsConfiguration: MediaItem.AdsConfiguration): AdsLoader {
        adsLoader = ImaAdsLoader.Builder(applicationContext)
            .setAdErrorListener {

            }
            .setAdEventListener {

            }.build()

        adsLoader!!.setPlayer(player)

        return adsLoader!!
    }

    private fun createMediaItem(): MediaItem {
        return MediaItem.Builder()
            .setUri(Uri.parse("https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears.mpd"))
            .setMimeType("application/dash+xml")
            .setAdTagUri(Uri.parse("https://raw.githubusercontent.com/KostyaLyapochkin/VAST-test/master/last-vmap.xml"))
            .setMediaMetadata(MediaMetadata.Builder().setTitle("result").build())
            .populateDrmProperties()
    }

    private fun MediaItem.Builder.populateDrmProperties(): MediaItem {
        return setDrmUuid(UUID.fromString("edef8ba9-79d6-4ace-a3c8-27dcd51d21ed"))
            .setDrmLicenseUri("https://proxy.uat.widevine.com/proxy?provider=widevine_test")
            .setDrmMultiSession(false)
            .setDrmForceDefaultLicenseUri(false)
            .setDrmLicenseRequestHeaders(mapOf()).build()
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        Log.e("AAA", "playWhenReady = $playWhenReady, reason = $reason")
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        Log.e("AAA", "$error")
        PlayerErrorDialogFragment.newInstance("${error.message}", "${error.cause}")
            .show(childFragmentManager, PLAYER_ERROR_DIALOG_FRAGMENT_TAG)
    }

    override fun onResume() {
        super.onResume()

        player?.addListener(this)
    }

    override fun onPause() {
        super.onPause()

        player?.removeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        player?.release()
        adsLoader?.release()
        player = null
    }

    companion object {
        const val PLAYER_FRAGMENT_TAG = "PLAYER_FRAGMENT_TAG"
    }
}