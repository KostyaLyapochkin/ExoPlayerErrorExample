package com.example.exoplayererrorcodecsample

import android.content.Context
import android.os.Handler
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.Renderer
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.video.VideoRendererEventListener
import java.util.*

class CustomRenderersFactory(context: Context) : DefaultRenderersFactory(context) {

    private var enableAsyncQueueing = false
    private var forceAsyncQueueingSynchronizationWorkaround = false
    private var enableSynchronizeCodecInteractionsWithQueueing = false

    override fun buildVideoRenderers(
        context: Context,
        extensionRendererMode: Int,
        mediaCodecSelector: MediaCodecSelector,
        enableDecoderFallback: Boolean,
        eventHandler: Handler,
        eventListener: VideoRendererEventListener,
        allowedVideoJoiningTimeMs: Long,
        out: ArrayList<Renderer>
    ) {
        val videoRenderer = CustomMediaCodecVideoRenderer(
            context,
            mediaCodecSelector,
            allowedVideoJoiningTimeMs,
            enableDecoderFallback,
            eventHandler,
            eventListener,
            MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY
        )
        videoRenderer.experimentalSetAsynchronousBufferQueueingEnabled(enableAsyncQueueing)
        videoRenderer.experimentalSetForceAsyncQueueingSynchronizationWorkaround(forceAsyncQueueingSynchronizationWorkaround)
        videoRenderer.experimentalSetSynchronizeCodecInteractionsWithQueueingEnabled(enableSynchronizeCodecInteractionsWithQueueing)
        out.add(videoRenderer)

        if (extensionRendererMode == EXTENSION_RENDERER_MODE_OFF) {
            return
        }
        var extensionRendererIndex = out.size
        if (extensionRendererMode == EXTENSION_RENDERER_MODE_PREFER) {
            extensionRendererIndex--
        }

        try {
            // Full class names used for constructor args so the LINT rule triggers if any of them move.
            // LINT.IfChange
            val clazz = Class.forName("com.google.android.exoplayer2.ext.vp9.LibvpxVideoRenderer")
            val constructor = clazz.getConstructor(
                Long::class.javaPrimitiveType,
                Handler::class.java,
                VideoRendererEventListener::class.java,
                Int::class.javaPrimitiveType
            )
            // LINT.ThenChange(../../../../../../../proguard-rules.txt)
            val renderer = constructor.newInstance(
                allowedVideoJoiningTimeMs,
                eventHandler,
                eventListener,
                MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY
            ) as Renderer
            out.add(extensionRendererIndex++, renderer)
            Log.i("CustomRenderersFactory", "Loaded LibvpxVideoRenderer.")
        } catch (e: ClassNotFoundException) {
            // Expected if the app was built without the extension.
        } catch (e: Exception) {
            // The extension is present, but instantiation failed.
            throw RuntimeException("Error instantiating VP9 extension", e)
        }

        try {
            // Full class names used for constructor args so the LINT rule triggers if any of them move.
            // LINT.IfChange
            val clazz = Class.forName("com.google.android.exoplayer2.ext.av1.Libgav1VideoRenderer")
            val constructor = clazz.getConstructor(
                Long::class.javaPrimitiveType,
                Handler::class.java,
                VideoRendererEventListener::class.java,
                Int::class.javaPrimitiveType
            )
            // LINT.ThenChange(../../../../../../../proguard-rules.txt)
            val renderer = constructor.newInstance(
                allowedVideoJoiningTimeMs,
                eventHandler,
                eventListener,
                MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY
            ) as Renderer
            out.add(extensionRendererIndex++, renderer)
            Log.i("CustomRenderersFactory", "Loaded Libgav1VideoRenderer.")
        } catch (e: ClassNotFoundException) {
            // Expected if the app was built without the extension.
        } catch (e: Exception) {
            // The extension is present, but instantiation failed.
            throw RuntimeException("Error instantiating AV1 extension", e)
        }
    }

}