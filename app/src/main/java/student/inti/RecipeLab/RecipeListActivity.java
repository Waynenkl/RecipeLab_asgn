package student.inti.RecipeLab;

import static student.inti.RecipeLab.utility.UserInterfaceHelpers.hideProgressDialog;
import static student.inti.RecipeLab.utility.UserInterfaceHelpers.showFailureFeedback;
import static student.inti.RecipeLab.utility.UserInterfaceHelpers.showNoContentFound;
import static student.inti.RecipeLab.utility.UserInterfaceHelpers.showRecipes;
import static student.inti.RecipeLab.utility.UserInterfaceHelpers.showUnsuccessfulFeedback;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import student.inti.RecipeLab.adapter.RecipeListAdapter;
import student.inti.RecipeLab.client.EdamamClient;
import student.inti.RecipeLab.databinding.ActivityRecipeListBinding;
import student.inti.RecipeLab.interfaces.EdamamApi;
import student.inti.RecipeLab.interfaces.ItemOnClickListener;
import student.inti.RecipeLab.models.RecipeSearchResponse;
import student.inti.RecipeLab.models.Settings;
import student.inti.RecipeLab.ui.RecipeDetailsFragment;
import student.inti.RecipeLab.ui.RecipeListFragment;
import student.inti.RecipeLab.utility.Constants;

import org.parceler.Parcels;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeListActivity extends AppCompatActivity {
  public static final String TAG = RecipeListActivity.class.getSimpleName();
  private ActivityRecipeListBinding binding;
  private RecipeListAdapter adapter;
  private String mealType;
  private Settings userSettings;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityRecipeListBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    mealType = getIntent().getStringExtra(Constants.EXTRA_MEAL_TYPE);
    userSettings = Parcels.unwrap(getIntent().getParcelableExtra(Constants.EXTRA_USER_SETTINGS));

    inflateFragment();
  }

  // Inflate RecipeDetailsFragment with extras
  private void inflateFragment(){
    getSupportFragmentManager().beginTransaction()
            .setReorderingAllowed(true)
            .add(R.id.fragment_container, RecipeListFragment.newInstance(mealType, userSettings))
            .commit();
  }

}