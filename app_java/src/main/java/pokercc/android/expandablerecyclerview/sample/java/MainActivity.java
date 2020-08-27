package pokercc.android.expandablerecyclerview.sample.java;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import pokercc.android.expandablerecyclerview.sample.java.college.CollegeActivity;
import pokercc.android.expandablerecyclerview.sample.java.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.collegeLongListButton.setOnClickListener(v -> {
            CollegeActivity.start(v.getContext());
        });
    }
}
