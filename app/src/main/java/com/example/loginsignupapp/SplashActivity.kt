package com.example.loginsignupapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val videoView = findViewById<VideoView>(R.id.videoView)
        val videoUri = Uri.parse("android.resource://${packageName}/${R.raw.splash_video}")
        videoView.setVideoURI(videoUri)

        videoView.setOnCompletionListener {
            // After video finishes → go to MainActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        videoView.start()
    }
}

//        Handler(Looper.getMainLooper()).postDelayed({
//            val intent = Intent(this, welcome::class.java)
//            startActivity(intent)
//            finish()
//        }, 3000) // 3 seconds
