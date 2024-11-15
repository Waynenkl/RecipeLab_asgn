package student.inti.RecipeLab.ui;

import static student.inti.RecipeLab.utility.UserInterfaceHelpers.hideProgressDialog;
import static student.inti.RecipeLab.utility.UserInterfaceHelpers.showFailureFeedback;
import static student.inti.RecipeLab.utility.UserInterfaceHelpers.showNoContentFound;
import static student.inti.RecipeLab.utility.UserInterfaceHelpers.showRecipes;
import static student.inti.RecipeLab.utility.UserInterfaceHelpers.showUnsuccessfulFeedback;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import student.inti.RecipeLab.R;
import student.inti.RecipeLab.RecipeDetailsActivity;
import student.inti.RecipeLab.adapter.RecipeListAdapter;
import student.inti.RecipeLab.client.EdamamClient;
import student.inti.RecipeLab.databinding.FragmentRecipeListBinding;
import student.inti.RecipeLab.interfaces.EdamamApi;
import student.inti.RecipeLab.interfaces.ItemOnClickListener;
import student.inti.RecipeLab.models.RecipeSearchResponse;
import student.inti.RecipeLab.models.Settings;
import student.inti.RecipeLab.utility.Constants;

import org.parceler.Parcels;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeListFragment extends Fragment implements ItemOnClickListener {
  public static final String TAG = RecipeListFragment.class.getSimpleName();
  private FragmentRecipeListBinding binding;
  private String mealType;
  private Settings userSettings;
  private RecipeListAdapter adapter;

  public RecipeListFragment() {
    // Required empty public constructor
  }

  public static RecipeListFragment newInstance(String mealType, Settings userSettings) {
    RecipeListFragment fragment = new RecipeListFragment();
    Bundle args = new Bundle();
    args.putString(Constants.EXTRA_MEAL_TYPE, mealType);
    args.putParcelable(Constants.EXTRA_USER_SETTINGS, Parcels.wrap(userSettings));
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mealType = getArguments().getString(Constants.EXTRA_MEAL_TYPE);
      userSettings = Parcels.unwrap(getArguments().getParcelable(Constants.EXTRA_USER_SETTINGS));
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the fragment layout
    binding = FragmentRecipeListBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // Set action bar title to passed meal type
    ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
    Objects.requireNonNull(actionBar).setTitle(mealType);

    // Create arrays for diet and health preferences for recipes
    String[] diets = new String[userSettings.getDiets().size()];
    String[] preferences = new String[userSettings.getPreferences().size()];

    EdamamApi client = EdamamClient.getClient();
    Call<RecipeSearchResponse> call = client.getRecipesByMealType("public", "", Constants.EDAMAM_API_ID, Constants.EDAMAM_API_KEY, mealType, userSettings.getDiets().toArray(diets), userSettings.getPreferences().toArray(preferences));

    loadRecipes(call);

    setListeners();

  }

  private void setListeners() {
    binding.fabToTop.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        binding.recipeList.scrollToPosition(0);
      }
    });
  }

  private void loadRecipes(Call<RecipeSearchResponse> call){
    call.enqueue(new Callback<RecipeSearchResponse>() {
      @Override
      public void onResponse(@NonNull Call<RecipeSearchResponse> call, @NonNull Response<RecipeSearchResponse> response) {
        hideProgressDialog(binding.progressBar, binding.progressMessage);

        if(response.isSuccessful()){
          assert response.body() != null;
          adapter = new RecipeListAdapter(getContext(), response.body().getHits(), RecipeListFragment.this);

          setLayoutManager();

          binding.recipeList.setAdapter(adapter);

          if(adapter.getItemCount() > 0){
            showRecipes(binding.recipeList);
          } else {
            showNoContentFound(binding.errorText, getString(R.string.no_recipes_found));
          }
        } else {
          showUnsuccessfulFeedback(binding.errorText, requireContext());
        }
      }

      @Override
      public void onFailure(@NonNull Call<RecipeSearchResponse> call, @NonNull Throwable t) {
        hideProgressDialog(binding.progressBar, binding.progressMessage);
        showFailureFeedback(binding.errorText, requireContext());
        Log.e(TAG, "Error: ", t);
      }
    });
  }

  private void setLayoutManager(){
    // Set layout manager based on orientation
    if(binding.getRoot().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
      binding.recipeList.setLayoutManager(new GridLayoutManager(getContext(), 2));
    } else {
      binding.recipeList.setLayoutManager(new LinearLayoutManager(getContext()));
    }
  }


  @Override
  public void onClick(String id, boolean isSaved) {
    Intent intent = new Intent(getContext(), RecipeDetailsActivity.class);
    intent.putExtra(Constants.EXTRA_RECIPE_ID, id);
    intent.putExtra(Constants.EXTRA_SAVED, isSaved);
    Log.d(TAG, "Recipe ID: " + id);
    startActivity(intent);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}