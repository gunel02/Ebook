package org.readium.r2.testapp.test.testPackage.utils
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import org.readium.r2.testapp.bookshelf.BookshelfViewModel

object FilePickerHelper {
    private const val PERMISSION_REQUEST_CODE = 1001

    fun setupFilePicker(
        fragment: Fragment,
        bookshelfViewModel: BookshelfViewModel,
        errorTextCallback: (String) -> Unit
    ): ActivityResultLauncher<String> {
        val filePickerLauncher = fragment.registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                if (isValidPdfFile(fragment.requireContext(), it)) {
                    bookshelfViewModel.importPublicationFromStorage(it)
                } else {
                    errorTextCallback("Please select a valid PDF file.")
                }
            }
        }

        fragment.view?.let {
            if (ContextCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(fragment.requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
            }

        }

        return filePickerLauncher
    }

    private fun isValidPdfFile(context: Context, uri: Uri): Boolean {
        val mimeType = context.contentResolver.getType(uri)
        return mimeType == "application/pdf" || uri.toString().endsWith(".pdf", ignoreCase = true)
    }
}
