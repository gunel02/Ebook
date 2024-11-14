package org.readium.r2.testapp.presentation.activity

import android.annotation.SuppressLint
import org.readium.r2.testapp.presentation.viewModel.BookshelfViewModel
import org.readium.r2.testapp.presentation.fragment.OpenedBooksFragment
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.readium.r2.testapp.presentation.viewModel.MainViewModel
import org.readium.r2.testapp.R
import org.readium.r2.testapp.presentation.fragment.AddBookFragment

class MainActivity : AppCompatActivity() {
    private val bookshelfViewModel: BookshelfViewModel by viewModels()
    private val viewModel: MainViewModel by viewModels()

    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        lifecycleScope.launch {
            val books = bookshelfViewModel.getBooksWithoutFlow()
            if (books.isEmpty()) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AddBookFragment())
                    .commit()
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, OpenedBooksFragment())
                    .commit()
            }
        }

        viewModel.channel.receive(this) { handleEvent(it) }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.fragments.size == 1) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    private fun handleEvent(event: MainViewModel.Event) {
        when (event) {
            is MainViewModel.Event.ImportPublicationSuccess ->
                Snackbar.make(
                    findViewById(android.R.id.content),
                    getString(R.string.import_publication_success),
                    Snackbar.LENGTH_LONG
                ).show()

            is MainViewModel.Event.ImportPublicationError -> {
                event.error.toUserError().show(this)
            }
        }
    }
}
