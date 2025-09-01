package com.example.loginsignupapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Button
import android.widget.VideoView

class welcome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome)

        val getStartedButton = findViewById<Button>(R.id.getStartedButton)
        getStartedButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        val videoView = findViewById<VideoView>(R.id.videoView)
        val videoPath = "android.resource://" + packageName +"/" + R.raw.videobg
        videoView.setVideoPath(videoPath)

        videoView.setOnPreparedListener { mp ->
            mp.isLooping = true // Set the video to loop
            // No need to call videoView.start() here again, as it's called below.
            // mp.setVolume(0f, 0f) // Optional: mute the video if it's just a background visual
        }

        // It's generally good to start the video after setting up the listener
        videoView.start()

        // Handle window insets for edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.videoView)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        // Ensure video resumes playback if activity is paused and then resumed
        val videoView = findViewById<VideoView>(R.id.videoView)
        if (!videoView.isPlaying) {
            videoView.start()
        }
    }

    override fun onPause() {
        super.onPause()
        // Pause video when activity goes to background
        val videoView = findViewById<VideoView>(R.id.videoView)
        if (videoView.isPlaying) {
            videoView.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release MediaPlayer resources when activity is destroyed
        findViewById<VideoView>(R.id.videoView).stopPlayback()
    }
}


