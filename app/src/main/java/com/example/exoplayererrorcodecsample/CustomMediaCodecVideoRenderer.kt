package com.example.exoplayererrorcodecsample

import android.content.Context
import android.os.Handler
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector
import com.google.android.exoplayer2.video.MediaCodecVideoRenderer
import com.google.android.exoplayer2.video.VideoRendererEventListener

class CustomMediaCodecVideoRenderer(
    context: Context,
    mediaCodecSelector: MediaCodecSelector,
    allowedJoiningTimeMs: Long,
    enableDecoderFallback: Boolean,
    eventHandler: Handler,
    eventListener: VideoRendererEventListener?,
    maxDroppedFramesToNotify: Int,
): MediaCodecVideoRenderer(
    context,
    mediaCodecSelector,
    allowedJoiningTimeMs,
    enableDecoderFallback,
    eventHandler,
    eventListener,
    maxDroppedFramesToNotify
) {

    // As I have said in the issue, it helps to solve problem

//    override fun codecNeedsSetOutputSurfaceWorkaround(name: String): Boolean {
//        return true
//    }


}