package com.example.exoplayererrorcodecsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.exoplayererrorcodecsample.PlayerFragment.Companion.PLAYER_FRAGMENT_TAG
import kotlinx.android.synthetic.main.fragment_player.*

class MainActivity : AppCompatActivity() {
    private lateinit var playerFragment: PlayerFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var playerFragment = supportFragmentManager.findFragmentByTag(PLAYER_FRAGMENT_TAG) as? PlayerFragment

        if (playerFragment == null) {
            playerFragment = PlayerFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.videoView, playerFragment, PLAYER_FRAGMENT_TAG)
                .commit()
        }

        this.playerFragment = playerFragment
    }
}