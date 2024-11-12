package org.readium.r2.testapp.bookshelf
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.io.File
import org.readium.r2.testapp.R
import org.readium.r2.testapp.data.model.Book
import org.readium.r2.testapp.databinding.ItemRecycleBookBinding
import org.readium.r2.testapp.utils.singleClick

class BookshelfAdapter(
    private val onBookClick: (Book) -> Unit,
    private val onBookLongClick: (Book) -> Unit
) : RecyclerView.Adapter<BookshelfAdapter.ViewHolder>() {

    private var showAllBooks = false
    private var originalList: List<Book> = emptyList()
    private var displayList: List<Book> = emptyList()

    fun submitList(list: List<Book>?) {
        originalList = list ?: emptyList()
        displayList = if (showAllBooks) originalList else originalList.take(2)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemRecycleBookBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val book = displayList[position]

        viewHolder.bind(book)
    }

    inner class ViewHolder(private val binding: ItemRecycleBookBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book) {
            binding.bookshelfTitleText.text = book.title
            Picasso.get()
                .load(File(book.cover))
                .placeholder(R.drawable.cover)
                .into(binding.bookshelfCoverImage)
            binding.root.singleClick {
                onBookClick(book)
            }
            binding.root.setOnLongClickListener {
                onBookLongClick(book)
                true
            }

        }

    }

    override fun getItemCount(): Int {
        return displayList.size
    }

    fun toggleShowAllBooks(showAll: Boolean) {
        showAllBooks = showAll
        submitList(originalList)
    }

}