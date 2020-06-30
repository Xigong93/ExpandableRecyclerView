package pokercc.android.expandablerecyclerview.sample.textbook

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pokercc.android.expandablerecyclerview.sample.R


class TextBookListViewModel(application: Application) : AndroidViewModel(application) {

    val textBookLists = MutableLiveData<List<TestBookList>>()
    fun loadData() {
        GlobalScope.launch(Dispatchers.Main) {
            textBookLists.value = withContext(Dispatchers.IO) {
                val json = getApplication<Application>().resources.openRawResource(
                    R.raw.text_book_list
                ).bufferedReader().readText()
                val type = Types.newParameterizedType(
                    List::class.java,
                    TestBookList::class.java
                )
                val moshi = Moshi.Builder().build()
                val adapter: JsonAdapter<List<TestBookList>> = moshi.adapter(type)
                adapter.fromJson(json)
            }
        }

    }


}