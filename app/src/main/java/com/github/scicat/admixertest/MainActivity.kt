package com.github.scicat.admixertest

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.scicat.admixertest.databinding.ActivityMainBinding
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import net.admixer.sdk.ResultCode
import net.admixer.sdk.instreamvideo.Quartile
import net.admixer.sdk.instreamvideo.VideoAd
import net.admixer.sdk.instreamvideo.VideoAdLoadListener
import net.admixer.sdk.instreamvideo.VideoAdPlaybackListener
import net.admixer.sdk.instreamvideo.VideoAdPlaybackListener.PlaybackCompletionState

const val ZONE_ID = "DEBUG_ZONE_ID"

class MainActivity : AppCompatActivity() {

    private var player: SimpleExoPlayer? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var videoAd: VideoAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("dnek", "onCreate")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnLoadAd.setOnClickListener { videoAd.loadAd() }
        videoAd = VideoAd(this, ZONE_ID).apply {
            adLoadListener = object : VideoAdLoadListener {
                override fun onAdLoaded(videoAd: VideoAd) {
                    Log.d("dnek", "onAdLoaded1")
                    if (videoAd.isReady) {
                        Log.d("dnek", "onAdLoaded2")
                        // Play instream ad inside the container
                        videoAd.playAd(binding.adContainer)
                    }
                }

                override fun onAdRequestFailed(videoAd: VideoAd, errorCode: ResultCode) {
                    Log.d("dnek", "onAdRequestFailed errorCode:$errorCode")
                }
            }

            videoPlaybackListener = object : VideoAdPlaybackListener {
                override fun onAdPlaying(videoAd: VideoAd) {
                    Log.d("dnek", "onAdPlaying")
                }

                override fun onQuartile(view: VideoAd, quartile: Quartile) {
                    Log.d("dnek", "onQuartile:$quartile")
                }

                override fun onAdCompleted(view: VideoAd, playbackState: PlaybackCompletionState) {
                    Log.d("dnek", "onAdCompleted $playbackState")
                }

                override fun onAdMuted(view: VideoAd, isMute: Boolean) {
                    Log.d("dnek", "onAdMuted:$isMute")
                }

                override fun onAdClicked(adView: VideoAd) {
                    Log.d("dnek", "onAdClicked")
                }

                override fun onAdClicked(videoAd: VideoAd, clickUrl: String) {
                    Log.d("dnek", "onAdClicked:$clickUrl")
                }
            }
        }
    }

    private fun initializePlayer() {
        Log.d("dnek", "initializePlayer")
        // Create a SimpleExoPlayer and set it as the player for content and ads.
        player = SimpleExoPlayer.Builder(this).build()
        binding.playerView.player = player
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(this, Util.getUserAgent(this, getString(R.string.app_name)))
        val mediaSourceFactory = ProgressiveMediaSource.Factory(dataSourceFactory)

        // Create the MediaSource for the content you wish to play.
        val mediaSource: MediaSource = mediaSourceFactory.createMediaSource(Uri.parse(getString(R.string.content_url)))

        // Prepare the content and ad to be played with the SimpleExoPlayer.
        player?.prepare(mediaSource)

        // Set PlayWhenReady. If true, content and ads autoplay.
        player?.playWhenReady = false
    }

    private fun releasePlayer() {
        Log.d("dnek", "releasePlayer")
        binding.playerView.player = null
        player?.release()
        player = null
    }

    override fun onStart() {
        super.onStart()
        Log.d("dnek", "onStart")
        if (Util.SDK_INT > 23) {
            initializePlayer()
            binding.playerView.onResume()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("dnek", "onResume")
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer()
            binding.playerView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("dnek", "onPause")
        if (Util.SDK_INT <= 23) {
            binding.playerView.onPause()
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d("dnek", "onStop")
        if (Util.SDK_INT > 23) {
            binding.playerView.onPause()
            releasePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("dnek", "onDestroy")
    }
}