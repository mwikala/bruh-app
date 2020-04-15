package uk.co.mwikala.bruh

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import github.com.vikramezhil.dks.speech.Dks
import github.com.vikramezhil.dks.speech.DksListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var dks: Dks
    private lateinit var mediaPlayer: MediaPlayer
    private val recordAudioPermission: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dks = Dks(application, supportFragmentManager, object : DksListener {
            override fun onDksLiveSpeechResult(liveSpeechResult: String) {}

            override fun onDksFinalSpeechResult(speechResult: String) {
                if (speechResult.contains("bra") || speechResult.contains("bro")) {
                    // play bruh audio
                    playBruhAudio(application)
                }
            }

            override fun onDksLanguagesAvailable(
                defaultLanguage: String?,
                supportedLanguages: ArrayList<String>?
            ) {
                if (supportedLanguages != null && supportedLanguages.contains("en-US")) {
                    dks.currentSpeechLanguage = "en-US"
                }
            }

            override fun onDksLiveSpeechFrequency(frequency: Float) {}

            override fun onDksSpeechError(errMsg: String) {
                // TODO handle speech error
                Toast.makeText(application, "Error: $errMsg", Toast.LENGTH_LONG).show()
            }
        })

        btn_start_dks.setOnClickListener {
            checkPerms()
        }
    }

    private fun playBruhAudio(context: Context) {
        mediaPlayer = MediaPlayer.create(context, R.raw.bruh)
        mediaPlayer.start()
    }

    private fun checkPerms() {
        // Check the perms exist
        if (ContextCompat.checkSelfPermission(
                application,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                recordAudioPermission
            )
        } else {
            startSpeechRecognition()
        }
    }

    private fun startSpeechRecognition() {
        dks.startSpeechRecognition()
        Toast.makeText(application, "Good to go!", Toast.LENGTH_SHORT).show()
        btn_start_dks.text = "Stop"
        btn_start_dks.setBackgroundColor(Color.RED)
        btn_start_dks.setOnClickListener {
            dks.closeSpeechOperations()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            recordAudioPermission -> {
                // if request is cancelled
                if ((grantResults.isNotEmpty()) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startSpeechRecognition()
                } else {
                    Toast.makeText(
                        application,
                        "You need to allow this application to record your audio.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
