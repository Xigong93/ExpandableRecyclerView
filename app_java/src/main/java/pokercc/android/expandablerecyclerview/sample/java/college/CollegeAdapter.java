package pokercc.android.expandablerecyclerview.sample.java.college;

import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pokercc.android.expandablerecyclerview.ExpandableAdapter;
import pokercc.android.expandablerecyclerview.sample.java.databinding.CityItemBinding;
import pokercc.android.expandablerecyclerview.sample.java.databinding.CollegeItemBinding;
import pokercc.android.expandablerecyclerview.sample.java.databinding.FamousCollegeItemBinding;
import pokercc.android.expandablerecyclerview.sample.java.databinding.ProvinceItemBinding;

class ProvinceVH extends RecyclerView.ViewHolder {

    public final ProvinceItemBinding itemBinding;

    ProvinceVH(ProvinceItemBinding itemBinding) {
        super(itemBinding.getRoot());
        this.itemBinding = itemBinding;
    }
}

class CityVH extends RecyclerView.ViewHolder {

    public final CityItemBinding itemBinding;

    CityVH(CityItemBinding itemBinding) {
        super(itemBinding.getRoot());
        this.itemBinding = itemBinding;
    }
}

class CollegeVH extends RecyclerView.ViewHolder {

    public final CollegeItemBinding itemBinding;

    CollegeVH(CollegeItemBinding itemBinding) {
        super(itemBinding.getRoot());
        this.itemBinding = itemBinding;
    }
}

class FamousCollegeVH extends RecyclerView.ViewHolder {

    public final FamousCollegeItemBinding itemBinding;

    FamousCollegeVH(FamousCollegeItemBinding itemBinding) {
        super(itemBinding.getRoot());
        this.itemBinding = itemBinding;
    }
}

class CollegeAdapter extends ExpandableAdapter<RecyclerView.ViewHolder> {
    private static final int CITY_ITEM = 12;
    private static final int COLLEGE_ITEM = -1;
    private static final int PROVINCE_ITEM = 11;
    private static final int FAMOUS_COLLEGE__ITEM = -2;

    private final boolean shortList;
    private final List<CollegeZone> data;

    CollegeAdapter(boolean shortList, List<CollegeZone> zones) {
        this.shortList = shortList;
        this.data = zones;
    }


    @Override
    protected RecyclerView.ViewHolder onCreateGroupViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        if (viewType == PROVINCE_ITEM) {
            ProvinceItemBinding itemBinding = ProvinceItemBinding.inflate(inflater, viewGroup, false);
            return new ProvinceVH(itemBinding);
        } else if (viewType == CITY_ITEM) {
            CityItemBinding itemBinding = CityItemBinding.inflate(inflater, viewGroup, false);
            return new CityVH(itemBinding);
        }
        throw new IllegalArgumentException("unSupport viewType:" + viewType);
    }


    @Override
    protected RecyclerView.ViewHolder onCreateChildViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        if (viewType == FAMOUS_COLLEGE__ITEM) {
            FamousCollegeItemBinding itemBinding = FamousCollegeItemBinding.inflate(inflater, viewGroup, false);
            return new FamousCollegeVH(itemBinding);
        } else if (viewType == COLLEGE_ITEM) {
            CollegeItemBinding itemBinding = CollegeItemBinding.inflate(inflater, viewGroup, false);
            return new CollegeVH(itemBinding);
        }
        throw new IllegalArgumentException("unSupport viewType:" + viewType);
    }


    @Override
    public int getGroupItemViewType(int groupPosition) {
        if (data.get(groupPosition).city) {
            return CITY_ITEM;
        } else {
            return PROVINCE_ITEM;
        }
    }

    @Override
    public int getChildItemViewType(int groupPosition, int childPosition) {
        if (data.get(groupPosition).colleges.get(childPosition).famous) {
            return FAMOUS_COLLEGE__ITEM;
        } else {
            return COLLEGE_ITEM;
        }
    }

    @Override
    public boolean isGroup(int viewType) {
        return viewType > 0;
    }

    @Override
    public int getGroupCount() {
        return data.size();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return data.get(groupPosition).colleges.size();
    }

    @Override
    protected void onBindChildViewHolder(RecyclerView.ViewHolder holder, int groupPosition, int childPosition, List<?> payloads) {
        College college = data.get(groupPosition).colleges.get(childPosition);
        if (payloads.isEmpty()) {
            if (holder instanceof CollegeVH) {
                ((CollegeVH) holder).itemBinding.titleText.setText(college.name);
            }
            if (holder instanceof FamousCollegeVH) {
                ((FamousCollegeVH) holder).itemBinding.titleText.setText(college.name);
            }
        }

    }

    @Override
    protected void onBindGroupViewHolder(RecyclerView.ViewHolder holder, int groupPosition, boolean expand, List<?> payloads) {
        CollegeZone collegeZone = data.get(groupPosition);
        if (payloads.isEmpty()) {
            if (holder instanceof ProvinceVH) {
                ((ProvinceVH) holder).itemBinding.titleText.setText(collegeZone.name);
                ((ProvinceVH) holder).itemBinding.arrowImage.setRotation(expand ? 0 : -90);
            }
            if (holder instanceof CityVH) {
                ((CityVH) holder).itemBinding.titleText.setText(collegeZone.name);
                ((CityVH) holder).itemBinding.arrowImage.setRotation(expand ? 0 : -90);
            }
        }
    }

    @Override
    protected void onGroupViewHolderExpandChange(RecyclerView.ViewHolder holder, int groupPosition, long animDuration, boolean expand) {
        View arrowImage;
        if (holder instanceof ProvinceVH) {
            arrowImage = ((ProvinceVH) holder).itemBinding.arrowImage;
        } else if (holder instanceof CityVH) {
            arrowImage = ((CityVH) holder).itemBinding.arrowImage;
        } else {
            return;
        }
        if (expand) {
            ObjectAnimator.ofFloat(arrowImage, View.ROTATION, 0f)
                    .setDuration(animDuration)
                    .start();
            // 不要使用这种动画，Item离屏之后，动画会取消
//            arrowImage.animate()
//                .setDuration(animDuration)
//                .rotation(0f)
//                .start()
        } else {
            ObjectAnimator.ofFloat(arrowImage, View.ROTATION, -90f)
                    .setDuration(animDuration)
                    .start();
        }
    }
}
