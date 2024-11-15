package org.readium.r2.testapp.presentation.activity// org.readium.r2.testapp.presentation.activity.SplashScreenActivity.kt
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.readium.r2.testapp.presentation.helper.SharedPreference

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val nextFragment = if (SharedPreference.hasBooks()) "OpenedBooks" else "AddBook"
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("FRAGMENT_TO_LOAD", nextFragment)
        }
        startActivity(intent)
        finish()


    }

}

