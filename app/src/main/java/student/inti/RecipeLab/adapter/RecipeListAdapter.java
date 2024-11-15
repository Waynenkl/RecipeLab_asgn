package student.inti.RecipeLab.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import student.inti.RecipeLab.R;
import student.inti.RecipeLab.databinding.ItemRecipeListBinding;
import student.inti.RecipeLab.interfaces.ItemOnClickListener;
import student.inti.RecipeLab.models.Hit;
import student.inti.RecipeLab.models.Recipe;
import com.bumptech.glide.Glide;


import java.util.List;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeListViewHolder> {
  public static final String TAG = RecipeListAdapter.class.getSimpleName();
  private final Context context;
  private final List<Hit> hits;
  private final ItemOnClickListener listener;

  public RecipeListAdapter(Context context, List<Hit> hits, ItemOnClickListener listener) {
    this.context = context;
    this.hits = hits;
    this.listener = listener;
  }


  @NonNull
  @Override
  public RecipeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new RecipeListViewHolder(ItemRecipeListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
  }


  @Override
  public void onBindViewHolder(@NonNull RecipeListViewHolder holder, int position) {
    Recipe recipe = hits.get(position).getRecipe();
    holder.bindRecipe(recipe);
    holder.binding.getRoot().setOnClickListener(view -> {
      String uri = recipe.getUri();
      // Extract recipe ID from recipe's URI
      String recipeId = uri.substring(uri.indexOf("#") + 1);
      listener.onClick(recipeId, recipe.getIsSaved());
      Log.d(TAG, "Saved status: " + recipe.getIsSaved());
    });
  }

  @Override
  public int getItemCount() {
    return hits.size();
  }

  public static class RecipeListViewHolder extends RecyclerView.ViewHolder {
    private final ItemRecipeListBinding binding;
    private final Context context;

    public RecipeListViewHolder(ItemRecipeListBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
      this.context = binding.getRoot().getContext();
    }

    private void bindRecipe(Recipe recipe) {
      Glide.with(context).asBitmap().load(recipe.getImages().getThumbnail().getUrl()).placeholder(R.drawable.brunch_dining).into(binding.recipeImageView);
      binding.recipeLabel.setText(recipe.getLabel());
      binding.recipeSource.setText(recipe.getSource());
    }
  }
}
