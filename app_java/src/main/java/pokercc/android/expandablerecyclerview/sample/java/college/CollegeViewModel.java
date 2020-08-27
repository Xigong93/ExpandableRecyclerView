package pokercc.android.expandablerecyclerview.sample.java.college;

import android.app.Application;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pokercc.android.expandablerecyclerview.sample.java.R;

public class CollegeViewModel extends AndroidViewModel {
    final MutableLiveData<List<CollegeZone>> colleges = new MutableLiveData<>();

    public CollegeViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadColleges() {
        new Thread() {

            @Override
            public void run() {
                super.run();
                try (InputStream is = getApplication().getResources().openRawResource(R.raw.college)) {
                    InputStreamReader inputStreamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
                    CollegeWrapper collegeWrapper = new Gson().fromJson(inputStreamReader, CollegeWrapper.class);
                    Map<String, CollegeZone> zoneMap = new HashMap<>();
                    for (CollegeZone zone : collegeWrapper.zone) {
                        zoneMap.put(zone.id, zone);
                    }
                    for (College college : collegeWrapper.university) {
                        CollegeZone collegeZone = zoneMap.get(college.zone);
                        if (collegeZone != null) {
                            collegeZone.colleges.add(college);
                        }
                    }
                    for (CollegeZone zone : collegeWrapper.zone) {
                        Collections.sort(zone.colleges);
                    }
                    Collections.sort(collegeWrapper.zone);
                    colleges.postValue(collegeWrapper.zone);
                } catch (Resources.NotFoundException | IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }


}
