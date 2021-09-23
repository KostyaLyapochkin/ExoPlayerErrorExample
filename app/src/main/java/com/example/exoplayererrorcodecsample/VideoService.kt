package com.example.exoplayererrorcodecsample

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.ads.AdsLoader
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.StyledPlayerView
import java.util.*
import com.google.ads.interactivemedia.v3.internal.w

class VideoService : Service() {

    private var player: SimpleExoPlayer? = null
    private var adsLoader: AdsLoader? = null

    override fun onBind(intent: Intent?): Binder = VideoServiceBinder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_STICKY

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
            .setAdTagUri(Uri.parse("https://raw.githubusercontent.com/KostyaLyapochkin/VAST-test/master/group.xml"))
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

    private inner class VideoServiceBinder : Binder(), IVideoService {

        override fun preparePlayerIfNeed() {
            if (player != null) {
                return
            }

            val mediaItem = createMediaItem()

            val renderFactory = CustomRenderersFactory(applicationContext)
                .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF)

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

        override fun attachView(view: StyledPlayerView) {
            view.player = player

            if (player?.playWhenReady == false) {
                player?.playWhenReady = true
            }
        }

        override fun detachView(view: StyledPlayerView) {
            view.player = null
            showNotification()
        }

        override fun destroy() {
            player?.release()
            stopSelf()
        }
    }

    private fun showNotification() {
        val notificationManager = PlayerNotificationManager.Builder(this, NOTIFICATION_ID , CHANNEL_EXAMPLE_ID)
            .setChannelNameResourceId(R.string.channel_exo_player_issue)
            .setNotificationListener(NotificationListener())
            .setMediaDescriptionAdapter(MediaDescriptionAdapter())
            .build().apply {
                setUseNextAction(false)
                setUsePreviousAction(false)
                setUsePlayPauseActions(true)
                setUseStopAction(true)
            }
        notificationManager.setPlayer(player)
    }

    private inner class NotificationListener : PlayerNotificationManager.NotificationListener {
        override fun onNotificationPosted(notificationId: Int, notification: Notification, ongoing: Boolean) {
            startForeground(notificationId, notification)
        }

        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            stopForeground(true)
        }
    }

    private inner class MediaDescriptionAdapter : PlayerNotificationManager.MediaDescriptionAdapter {
        override fun getCurrentContentTitle(player: Player) = "Title"

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            val intent = Intent(this@VideoService, MainActivity::class.java)
            return PendingIntent.getActivity(this@VideoService, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        override fun getCurrentContentText(player: Player) = "Test"

        override fun getCurrentLargeIcon(player: Player, callback: PlayerNotificationManager.BitmapCallback): Bitmap? {
            return null
        }
    }

    private companion object {
        const val NOTIFICATION_ID = 10
        const val CHANNEL_EXAMPLE_ID = "CHANNEL_EXAMPLE_ID"
    }
}

interface IVideoService {
    fun preparePlayerIfNeed()
    fun attachView(view: StyledPlayerView)
    fun detachView(view: StyledPlayerView)
    fun destroy()
}