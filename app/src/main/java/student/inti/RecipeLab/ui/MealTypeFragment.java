package student.inti.RecipeLab.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import student.inti.RecipeLab.R;
import student.inti.RecipeLab.adapter.MealTypeAdapter;
import student.inti.RecipeLab.databinding.FragmentMealTypesBinding;
import student.inti.RecipeLab.models.Settings;
import student.inti.RecipeLab.utility.Constants;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MealTypeFragment extends Fragment {
  public static final String TAG = MealTypeFragment.class.getSimpleName();
  private FragmentMealTypesBinding binding;
  private Settings userSettings;
  private final List<Integer> mealTypeImages = new ArrayList<>(Arrays.asList(R.drawable.breakfast, R.drawable.lunch_dinner ,R.drawable.brunch, R.drawable.snack, R.drawable.teatime));

  public MealTypeFragment() {
  }

  public static MealTypeFragment newInstance(Settings userSettings) {
    MealTypeFragment fragment = new MealTypeFragment();
    Bundle bundle = new Bundle();
    bundle.putParcelable(Constants.EXTRA_USER_SETTINGS, Parcels.wrap(userSettings));
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    assert getArguments() != null;
    userSettings = Parcels.unwrap(getArguments().getParcelable(Constants.EXTRA_USER_SETTINGS));
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    binding = FragmentMealTypesBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    updateTextViewContent();
    initializeRecyclerView();
  }

  private void updateTextViewContent(){
    if(userSettings != null){
      binding.welcomeText.setText(getString(R.string.welcome, userSettings.getName()));
    }
  }

  private void initializeRecyclerView(){
    // Set layout manager
    GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.grid_columns));
    binding.mealTypeGrid.setLayoutManager(gridLayoutManager);
    // Set adapter
    MealTypeAdapter adapter = new MealTypeAdapter(getContext(), mealTypeImages, new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.meal_types))), userSettings);
    binding.mealTypeGrid.setAdapter(adapter);
    Log.d(TAG, "Meal types count: " + adapter.getItemCount());
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
