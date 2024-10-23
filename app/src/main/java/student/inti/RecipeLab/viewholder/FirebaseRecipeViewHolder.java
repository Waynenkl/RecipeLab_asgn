package student.inti.RecipeLab.viewholder;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import student.inti.RecipeLab.R;
import student.inti.RecipeLab.databinding.ItemSavedRecipeListBinding;
import student.inti.RecipeLab.models.Recipe;
import student.inti.RecipeLab.utility.animations.ViewHolderItemTouchHelper;
import com.bumptech.glide.Glide;

public class FirebaseRecipeViewHolder extends RecyclerView.ViewHolder implements ViewHolderItemTouchHelper {
  public static final String TAG = FirebaseRecipeViewHolder.class.getSimpleName();
  public final ItemSavedRecipeListBinding binding;
  private final Context context;

  public FirebaseRecipeViewHolder(ItemSavedRecipeListBinding binding) {
    super(binding.getRoot());
    this.binding = binding;
    this.context = binding.getRoot().getContext();
  }

  public void bindRecipe(Recipe recipe){
    Glide.with(context).asBitmap().load(recipe.getImages().getThumbnail().getUrl()).placeholder(R.drawable.brunch_dining).into(binding.recipeImageView);
    binding.recipeLabel.setText(recipe.getLabel());
    binding.recipeSource.setText(recipe.getSource());
  }

  @Override
  public void onItemSelected() {
    Log.d(TAG, "Animation on selected item");
    AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(context,
            R.animator.drag_on_selected);
    set.setTarget(binding.getRoot());
    set.start();
  }

  @Override
  public void onItemUnselected() {
    Log.d(TAG, "Animation on unselected item");
    AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(context,
            R.animator.drag_on_unselected);
    set.setTarget(itemView);
    set.start();
  }
}
