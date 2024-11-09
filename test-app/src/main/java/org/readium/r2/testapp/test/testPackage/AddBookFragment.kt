package org.readium.r2.testapp.test.testPackage

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import org.readium.r2.testapp.Application
import org.readium.r2.testapp.R
import org.readium.r2.testapp.bookshelf.BookshelfViewModel
import org.readium.r2.testapp.databinding.FragmentAddBookBinding
import org.readium.r2.testapp.reader.ReaderActivityContract
import org.readium.r2.testapp.utils.viewLifecycle

class AddBookFragment : Fragment() {

    private inner class OnViewAttachedListener : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(view: View) {
            app.readium.onLcpDialogAuthenticationParentAttached(view)
        }

        override fun onViewDetachedFromWindow(view: View) {
            app.readium.onLcpDialogAuthenticationParentDetached()
        }
    }

    private val bookshelfViewModel: BookshelfViewModel by activityViewModels()
    private lateinit var appStoragePickerLauncher: ActivityResultLauncher<String>
    private var binding: FragmentAddBookBinding by viewLifecycle()
    private var onViewAttachedListener: OnViewAttachedListener = OnViewAttachedListener()

    private val app: Application
        get() = requireContext().applicationContext as Application

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.addOnAttachStateChangeListener(onViewAttachedListener)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                bookshelfViewModel.channel.receive(viewLifecycleOwner) { handleEvent(it) }
            }
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        } else {
            setupStoragePicker()
        }

        binding.addBookButton.setOnClickListener {
            appStoragePickerLauncher.launch("*/*")
        }
    }

    private fun setupStoragePicker() {
        appStoragePickerLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    if (isValidPdfFile(it)) {
                        loadDataAndOpenFragment(it)
                    } else {
                        showError("Please select a valid PDF file.")
                    }
                }
            }
    }

    private fun isValidPdfFile(uri: Uri): Boolean {
        val mimeType = requireContext().contentResolver.getType(uri)
        return mimeType == "application/pdf" || uri.toString().endsWith(".pdf", ignoreCase = true)
    }

    private fun loadDataAndOpenFragment(uri: Uri) {
        bookshelfViewModel.importPublicationFromStorage(uri)
        val fragment = OpenedBooksFragment()
        val transaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment).addToBackStack(fragment.tag)
        transaction.commit()
    }


    private fun showError(message: String) {
        binding.errorText.visibility = View.VISIBLE
        binding.errorText.text = message
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

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }
}