package org.readium.r2.testapp.presentation.viewModel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.readium.r2.testapp.data.model.Book
import org.readium.r2.testapp.reader.OpeningError
import org.readium.r2.testapp.reader.ReaderActivityContract
import org.readium.r2.testapp.utils.EventChannel

class BookshelfViewModel(application: Application) : AndroidViewModel(application) {

    private val app
        get() =
            getApplication<org.readium.r2.testapp.app.Application>()

    val channel = EventChannel(Channel<Event>(Channel.BUFFERED), viewModelScope)
    val books = app.bookRepository.books()

    fun deletePublication(book: Book) =
        viewModelScope.launch {
            app.bookshelf.deleteBook(book)
        }

    fun importPublicationFromStorage(uri: Uri) {

        app.bookshelf.importPublicationFromStorage(uri)
    }

    suspend fun getBooksWithoutFlow(): List<Book> {
        return app.bookRepository.booksWithoutFlow()
    }

    fun openPublication(
        bookId: Long
    ) {
        viewModelScope.launch {
            app.readerRepository
                .open(bookId)
                .onFailure {
                    channel.send(Event.OpenPublicationError(it))
                }
                .onSuccess {

                    val arguments = ReaderActivityContract.Arguments(bookId)
                    channel.send(Event.LaunchReader(arguments))
                }
        }
    }

    sealed class Event {

        class OpenPublicationError(
            val error: OpeningError
        ) : Event()

        class LaunchReader(
            val arguments: ReaderActivityContract.Arguments
        ) : Event()
    }
}