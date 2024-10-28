package student.inti.RecipeLab.ui;

import static student.inti.RecipeLab.utility.UserInterfaceHelpers.hideProgressDialog;
import static student.inti.RecipeLab.utility.UserInterfaceHelpers.showFailureFeedback;
import static student.inti.RecipeLab.utility.UserInterfaceHelpers.showNoContentFound;
import static student.inti.RecipeLab.utility.UserInterfaceHelpers.showProgressDialog;
import static student.inti.RecipeLab.utility.UserInterfaceHelpers.showRecipes;
import static student.inti.RecipeLab.utility.UserInterfaceHelpers.showUnsuccessfulFeedback;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import student.inti.RecipeLab.R;
import student.inti.RecipeLab.RecipeDetailsActivity;
import student.inti.RecipeLab.adapter.RecipeListAdapter;
import student.inti.RecipeLab.client.EdamamClient;
import student.inti.RecipeLab.databinding.FragmentSearchBinding;
import student.inti.RecipeLab.interfaces.EdamamApi;
import student.inti.RecipeLab.interfaces.ItemOnClickListener;
import student.inti.RecipeLab.models.RecipeSearchResponse;
import student.inti.RecipeLab.models.Settings;
import student.inti.RecipeLab.utility.Constants;

import org.parceler.Parcels;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment implements ItemOnClickListener {
  public static final String TAG = SearchFragment.class.getSimpleName();
  private FragmentSearchBinding binding;
  private Settings userSettings;
  private RecipeListAdapter adapter;
  private EdamamApi client;
  private SharedPreferences sharedPreferences;

  public SearchFragment(){
  }

  public static SearchFragment newInstance(Settings userSettings){
    SearchFragment fragment = new SearchFragment();
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
    binding = FragmentSearchBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // Create arrays for diet and health preferences for recipes
    String[] diets = new String[userSettings.getDiets().size()];
    String[] preferences = new String[userSettings.getPreferences().size()];

    client = EdamamClient.getClient();

    // Set up shared preferences
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    String recentSearch = sharedPreferences.getString(Constants.PREFERENCES_RECIPE_SEARCH_KEY, null);
    Log.d(TAG, "Recently searched recipe: " + recentSearch);

    // Initial display of recipes based on previous search
    loadRecipes(recentSearch, diets, preferences);

    // Load recipes based on search
    setUpSearchView(diets, preferences);

    setListeners();
  }

  private void setListeners() {
    binding.fabToTop.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        binding.recipeResultList.scrollToPosition(0);
      }
    });
  }

  private void setUpSearchView(String[] diets, String[] preferences){
    binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String recipe) {
        saveToSharedPreferences(recipe);
        loadRecipes(recipe, diets, preferences);
        return false;
      }

      @Override
      public boolean onQueryTextChange(String s) {
        return false;
      }
    });
  }

  private void loadRecipes(String recipe, String[] diets, String[] preferences){
    Call<RecipeSearchResponse> call = client.getRecipesByKeyword("public", recipe, Constants.EDAMAM_API_ID, Constants.EDAMAM_API_KEY, userSettings.getDiets().toArray(diets), userSettings.getPreferences().toArray(preferences));
    showProgressDialog(binding.progressBar, binding.progressMessage);

    call.enqueue(new Callback<RecipeSearchResponse>() {
      @Override
      public void onResponse(@NonNull Call<RecipeSearchResponse> call, @NonNull Response<RecipeSearchResponse> response) {
        hideProgressDialog(binding.progressBar, binding.progressMessage);

        if(response.isSuccessful()){
          assert response.body() != null;
          adapter = new RecipeListAdapter(getContext(), response.body().getHits(), SearchFragment.this);
          setLayoutManager();
          binding.recipeResultList.setAdapter(adapter);

          if(adapter.getItemCount() > 0){
            showRecipes(binding.recipeResultList);
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
      binding.recipeResultList.setLayoutManager(new GridLayoutManager(getContext(), 2));
    } else {
      binding.recipeResultList.setLayoutManager(new LinearLayoutManager(getContext()));
    }
  }

  private void saveToSharedPreferences(String recipeSearch) {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(Constants.PREFERENCES_RECIPE_SEARCH_KEY, recipeSearch).apply();
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
