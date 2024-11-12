package org.readium.r2.testapp.test.testPackage// org.readium.r2.testapp.test.testPackage.SplashScreenActivity.kt
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.readium.r2.testapp.MainActivity
import org.readium.r2.testapp.test.testPackage.utils.SharedPreference

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

