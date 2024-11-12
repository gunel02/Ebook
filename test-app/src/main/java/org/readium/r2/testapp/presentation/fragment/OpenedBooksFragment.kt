package org.readium.r2.testapp.presentation.fragment

import BookshelfViewModel
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.readium.r2.testapp.Application
import org.readium.r2.testapp.R
import org.readium.r2.testapp.bookshelf.BookshelfAdapter
import org.readium.r2.testapp.data.model.Book
import org.readium.r2.testapp.databinding.FragmentOpenedBooksBinding
import org.readium.r2.testapp.reader.ReaderActivityContract
import org.readium.r2.testapp.presentation.helper.FilePickerHelper
import org.readium.r2.testapp.utils.viewLifecycle

class OpenedBooksFragment : Fragment() {

    private var isExpanded = false

    private inner class OnViewAttachedListener : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(view: View) {
            app.readium.onLcpDialogAuthenticationParentAttached(view)
        }

        override fun onViewDetachedFromWindow(view: View) {
            app.readium.onLcpDialogAuthenticationParentDetached()
        }
    }

    private val bookshelfViewModel: BookshelfViewModel by activityViewModels()
    private lateinit var bookshelfAdapter: BookshelfAdapter
    private var binding: FragmentOpenedBooksBinding by viewLifecycle()
    private var onViewAttachedListener: OnViewAttachedListener = OnViewAttachedListener()

    private lateinit var appStoragePickerLauncher: ActivityResultLauncher<String>

    private val app: Application
        get() = requireContext().applicationContext as Application

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOpenedBooksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appStoragePickerLauncher = FilePickerHelper.setupFilePicker(
            fragment = this,
            bookshelfViewModel = bookshelfViewModel
        ) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
        binding.addBookButton.setOnClickListener {
            appStoragePickerLauncher.launch("*/*")
        }

        initListener()

        view.addOnAttachStateChangeListener(onViewAttachedListener)

        bookshelfViewModel.channel.receive(viewLifecycleOwner) { handleEvent(it) }

        bookshelfAdapter = BookshelfAdapter(
            onBookClick = { book ->
                book.id?.let {
                    bookshelfViewModel.openPublication(it)
                }
            },
            onBookLongClick = { book -> confirmDeleteBook(book) }
        )

        binding.showAllBooksButton.setOnClickListener {
            toggleBooksDisplay(true)
        }

        binding.hideBooksButton.setOnClickListener {
            toggleBooksDisplay(false)
        }

        observeBooks()

        binding.bookshelfBookList.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = bookshelfAdapter
            addItemDecoration(
                VerticalSpaceItemDecoration(
                    10
                )
            )
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                bookshelfViewModel.books.collectLatest {
                    bookshelfAdapter.submitList(it)

                }
            }
        }
    }

    private fun initListener() {
        binding.iconSettings.setOnClickListener() {
            val fragment = SettingFragment()
            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment).addToBackStack(fragment.tag)
            transaction.commit()
        }
    }

    private fun handleEvent(event: BookshelfViewModel.Event) {
        when (event) {
            is BookshelfViewModel.Event.OpenPublicationError -> {
                event.error.toUserError().show(requireActivity())
            }

            is BookshelfViewModel.Event.LaunchReader -> {
                val intent = ReaderActivityContract().createIntent(
                    requireContext(),
                    event.arguments
                )
                startActivity(intent)
            }

            else -> {}
        }
    }

    class VerticalSpaceItemDecoration(private val verticalSpaceHeight: Int) :
        RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.bottom = verticalSpaceHeight
        }
    }

    private fun deleteBook(book: Book) {
        bookshelfViewModel.deletePublication(book)
    }

    private fun confirmDeleteBook(book: Book) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.confirm_delete_book_title))
            .setMessage(getString(R.string.confirm_delete_book_text))
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton(getString(R.string.delete)) { dialog, _ ->
                deleteBook(book)
                dialog.dismiss()
            }
            .show()
    }

    private fun observeBooks() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                bookshelfViewModel.books.collectLatest { books ->
                    bookshelfAdapter.submitList(books)
                    toggleBooksDisplay(isExpanded)
                }
            }
        }
    }

    private fun toggleBooksDisplay(showAll: Boolean) {
        isExpanded = showAll
        bookshelfAdapter.toggleShowAllBooks(showAll)
        binding.showAllBooksButton.visibility = if (showAll) View.GONE else View.VISIBLE
        binding.hideBooksButton.visibility = if (showAll) View.VISIBLE else View.GONE
    }

}