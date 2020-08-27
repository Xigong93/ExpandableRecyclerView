package pokercc.android.expandablerecyclerview.sample.java.college;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

@Keep
class College implements Comparable<College> {
    public String id;
    public int order;
    public String name;
    public String zone;
    @Nullable
    public String shortName;
    public boolean famous;

    @Override
    public int compareTo(College o) {
        return this.order - o.order;
    }
}

@Keep
class CollegeZone implements Comparable<CollegeZone> {
    public String id;
    public int sort;
    public String name;
    public boolean city;
    public transient final List<College> colleges = new ArrayList<>();

    @Override
    public int compareTo(CollegeZone o) {
        return this.sort - o.sort;
    }
}

class CollegeWrapper {
    public final List<CollegeZone> zone;
    public final List<College> university;

    CollegeWrapper(List<CollegeZone> zone, List<College> university) {
        this.zone = zone;
        this.university = university;
    }
}