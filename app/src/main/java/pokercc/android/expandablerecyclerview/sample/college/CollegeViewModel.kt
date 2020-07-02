
package pokercc.android.expandablerecyclerview.sample.college

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pokercc.android.expandablerecyclerview.sample.R

class CollegeViewModel(application: Application) : AndroidViewModel(application) {

    val colleges = MutableLiveData<List<CollegeZone>>()
    @Suppress("BlockingMethodInNonBlockingContext")
    fun loadColleges() {
        GlobalScope.launch(Dispatchers.Main) {
            colleges.value = withContext(Dispatchers.IO) {
                val json = getApplication<Application>().resources
                    .openRawResource(R.raw.college)
                    .bufferedReader().readText()
                val collegeWrapper =
                    Moshi.Builder().build().adapter(CollegeWrapper::class.java).fromJson(json)!!
                val zoneMap = HashMap<String, CollegeZone>()
                for (zone in collegeWrapper.zone) {
                    zoneMap[zone.id] = zone
                }
                for (college in collegeWrapper.university) {
                    zoneMap[college.zone]?.colleges?.add(college)
                }
                for (zone in collegeWrapper.zone) {
                    zone.colleges.sortBy { it.order }
                }
                collegeWrapper.zone.sortedBy { it.sort }
            }
        }

    }


}