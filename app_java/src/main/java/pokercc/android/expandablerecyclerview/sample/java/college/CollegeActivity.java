package pokercc.android.expandablerecyclerview.sample.java.college;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import pokercc.android.expandablerecyclerview.sample.java.databinding.CollegeActivityBinding;

public class CollegeActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, CollegeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CollegeActivityBinding binding = CollegeActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewModelProvider.AndroidViewModelFactory modelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
        CollegeViewModel collegeViewModel = new ViewModelProvider(this, modelFactory).get(CollegeViewModel.class);
        collegeViewModel.loadColleges();
        collegeViewModel.colleges.observe(this, collegeZones -> {
            binding.recyclerView.setAdapter(new CollegeAdapter(false, collegeZones));
        });
    }
}
