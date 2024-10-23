package student.inti.RecipeLab;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import student.inti.RecipeLab.databinding.ActivityRecipeDetailsBinding;
import student.inti.RecipeLab.ui.RecipeDetailsFragment;
import student.inti.RecipeLab.utility.Constants;

public class RecipeDetailsActivity extends AppCompatActivity {
    public static final String TAG = RecipeDetailsActivity.class.getSimpleName();
    private ActivityRecipeDetailsBinding binding;
    private String recipeId;
    private boolean recipeStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecipeDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Retrieve intent extras
        recipeId = getIntent().getStringExtra(Constants.EXTRA_RECIPE_ID);
        recipeStatus = getIntent().getBooleanExtra(Constants.EXTRA_SAVED, false);

        inflateFragment();
    }

    // Inflate RecipeDetailsFragment with extras
    private void inflateFragment(){
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container, RecipeDetailsFragment.newInstance(recipeId, recipeStatus))
                .commit();
    }

}
