package pokercc.android.expandablerecyclerview.sample.textbook

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pokercc.android.expandablerecyclerview.sample.R


class TextBookListViewModel(
    application: Application,
    private val stateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    companion object {
        private const val LOG_TAG = "TextBookListViewModel"
        private const val TEXT_BOOK_LIST = "text_book_list"
    }

    val textBookLists = MutableLiveData<List<TextBookList>>()

    init {
        val textBookList = stateHandle.get<List<TextBookList>>(TEXT_BOOK_LIST)
        textBookList?.let {
            textBookLists.value = it
            Log.d(LOG_TAG, "get textBookList:${it}")
        }
    }

    fun loadData() {
        GlobalScope.launch(Dispatchers.Main) {
            val textBookList = withContext(Dispatchers.IO) {
                val json = getApplication<Application>().resources.openRawResource(
                    R.raw.text_book_list
                ).bufferedReader().readText()
                val type = Types.newParameterizedType(
                    List::class.java,
                    TextBookList::class.java
                )
                val moshi = Moshi.Builder().build()
                val adapter: JsonAdapter<List<TextBookList>> = moshi.adapter(type)
                adapter.fromJson(json)
            }
            textBookLists.value = textBookList
            stateHandle.set(TEXT_BOOK_LIST, ArrayList(textBookList!!))
            Log.d(LOG_TAG, "save textBookList:${textBookList}")
        }
    }


}