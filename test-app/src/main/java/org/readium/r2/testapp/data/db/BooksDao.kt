
package org.readium.r2.testapp.data.db

import androidx.annotation.ColorInt
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.readium.r2.testapp.data.model.Book
import org.readium.r2.testapp.data.model.Bookmark
import org.readium.r2.testapp.data.model.Highlight

@Dao
interface BooksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book): Long

    @Query("DELETE FROM " + Book.TABLE_NAME + " WHERE " + Book.ID + " = :bookId")
    suspend fun deleteBook(bookId: Long)


    @Query("SELECT * FROM " + Book.TABLE_NAME + " WHERE " + Book.ID + " = :id")
    suspend fun get(id: Long): Book?


    @Query("SELECT * FROM " + Book.TABLE_NAME + " ORDER BY " + Book.CREATION_DATE + " desc")
    fun getAllBooks(): Flow<List<Book>>


    @Query("SELECT * FROM " + Book.TABLE_NAME + " ORDER BY " + Book.CREATION_DATE + " desc")
    suspend fun getAllBooksWithoutFlow(): List<Book>


    @Query("SELECT * FROM " + Bookmark.TABLE_NAME + " WHERE " + Bookmark.BOOK_ID + " = :bookId")
    fun getBookmarksForBook(bookId: Long): Flow<List<Bookmark>>

    @Query(
        "SELECT * FROM ${Highlight.TABLE_NAME} WHERE ${Highlight.BOOK_ID} = :bookId ORDER BY ${Highlight.TOTAL_PROGRESSION} ASC"
    )
    fun getHighlightsForBook(bookId: Long): Flow<List<Highlight>>

    @Query("SELECT * FROM ${Highlight.TABLE_NAME} WHERE ${Highlight.ID} = :highlightId")
    suspend fun getHighlightById(highlightId: Long): Highlight?

    /**
     * Inserts a bookmark
     * @param bookmark The bookmark to insert
     * @return The ID of the bookmark that was added (primary key)
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBookmark(bookmark: Bookmark): Long

    /**
     * Inserts a highlight
     * @param highlight The highlight to insert
     * @return The ID of the highlight that was added (primary key)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHighlight(highlight: Highlight): Long

    /**
     * Updates a highlight's annotation.
     */
    @Query(
        "UPDATE ${Highlight.TABLE_NAME} SET ${Highlight.ANNOTATION} = :annotation WHERE ${Highlight.ID} = :id"
    )
    suspend fun updateHighlightAnnotation(id: Long, annotation: String)

    /**
     * Updates a highlight's tint and style.
     */
    @Query(
        "UPDATE ${Highlight.TABLE_NAME} SET ${Highlight.TINT} = :tint, ${Highlight.STYLE} = :style WHERE ${Highlight.ID} = :id"
    )
    suspend fun updateHighlightStyle(id: Long, style: Highlight.Style, @ColorInt tint: Int)

    /**
     * Deletes a bookmark
     */
    @Query("DELETE FROM " + Bookmark.TABLE_NAME + " WHERE " + Bookmark.ID + " = :id")
    suspend fun deleteBookmark(id: Long)

    /**
     * Deletes the highlight with given id.
     */
    @Query("DELETE FROM ${Highlight.TABLE_NAME} WHERE ${Highlight.ID} = :id")
    suspend fun deleteHighlight(id: Long)

    /**
     * Saves book progression
     * @param locator Location of the book
     * @param id The book to update
     */
    @Query(
        "UPDATE " + Book.TABLE_NAME + " SET " + Book.PROGRESSION + " = :locator WHERE " + Book.ID + "= :id"
    )
    suspend fun saveProgression(locator: String, id: Long)
}
