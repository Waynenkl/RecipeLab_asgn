package student.inti.RecipeLab.ui;

import static student.inti.RecipeLab.utility.UserInterfaceHelpers.hideProgressDialog;
import static student.inti.RecipeLab.utility.UserInterfaceHelpers.showNoContentFound;
import static student.inti.RecipeLab.utility.UserInterfaceHelpers.showRecipes;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import student.inti.RecipeLab.R;
import student.inti.RecipeLab.RecipeDetailsActivity;
import student.inti.RecipeLab.adapter.FirebaseRecipeListAdapter;
import student.inti.RecipeLab.databinding.FragmentSavedRecipesBinding;
import student.inti.RecipeLab.interfaces.ItemOnClickListener;
import student.inti.RecipeLab.models.Recipe;
import student.inti.RecipeLab.utility.Constants;
import student.inti.RecipeLab.utility.gestures.AppItemTouchHelper;
import student.inti.RecipeLab.utility.gestures.AppItemTouchHelperCallback;
import student.inti.RecipeLab.utility.gestures.OnTouchScreenDragListener;
import student.inti.RecipeLab.viewholder.FirebaseRecipeViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Objects;

public class SavedRecipesFragment extends Fragment implements OnTouchScreenDragListener, ItemOnClickListener {
  public static final String TAG = SavedRecipesFragment.class.getSimpleName();
  private FragmentSavedRecipesBinding binding;
  private FirebaseRecyclerAdapter<Recipe, FirebaseRecipeViewHolder> firebaseAdapter;
  private ItemTouchHelper itemTouchHelper;

  public SavedRecipesFragment() {
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    binding = FragmentSavedRecipesBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    setUpFirebaseAdapter();
    hideProgressDialog(binding.progressBar, binding.progressMessage);
    showRecipes(binding.starredRecipeList);
  }

  private void setUpFirebaseAdapter(){
    // Set up FirebaseAdapter
    String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    Query dbQuery = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_RECIPE_LOCATION).child(userId).orderByChild(Constants.FIREBASE_QUERY_INDEX);
    FirebaseRecyclerOptions<Recipe> options =
            new FirebaseRecyclerOptions.Builder<Recipe>()
                    .setQuery(dbQuery, Recipe.class)
                    .build();

    setLayoutManager();
    firebaseAdapter = new FirebaseRecipeListAdapter(options, dbQuery.getRef(), this, getContext(), this);

    if (firebaseAdapter.getItemCount() < 1) {
      showNoContentFound(binding.errorText, getString(R.string.no_saved_recipes));
    }
    binding.errorText.setVisibility(View.GONE);
    binding.starredRecipeList.setAdapter(firebaseAdapter);

    // Attach drag listener to viewholder via recyclerview and enable interaction with necessary callbacks
    ItemTouchHelper.Callback callback = new AppItemTouchHelperCallback((AppItemTouchHelper) firebaseAdapter);
    itemTouchHelper = new ItemTouchHelper(callback);
    itemTouchHelper.attachToRecyclerView(binding.starredRecipeList);
  }

  @Override
  public void onStart() {
    super.onStart();
    firebaseAdapter.startListening();
  }

  @Override
  public void onStop() {
    firebaseAdapter.stopListening();
    super.onStop();
  }

  private void setLayoutManager(){
    // Set layout manager based on orientation
    if(binding.getRoot().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
      binding.starredRecipeList.setLayoutManager(new GridLayoutManager(getContext(), 2));
    } else {
      binding.starredRecipeList.setLayoutManager(new LinearLayoutManager(getContext()));
    }
  }

  @Override
  public void onDrag(RecyclerView.ViewHolder viewHolder) {
    itemTouchHelper.startDrag(viewHolder); // Send touch events to AppItemTouchHelperCallback
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    firebaseAdapter.stopListening();
  }

  @Override
  public void onClick(String id, boolean isSaved) {
    Intent intent = new Intent(getContext(), RecipeDetailsActivity.class);
    intent.putExtra(Constants.EXTRA_RECIPE_ID, id);
    intent.putExtra(Constants.EXTRA_SAVED, isSaved);
    Log.d(TAG, "Recipe ID: " + id);
    requireActivity().startActivity(intent);
  }
}
