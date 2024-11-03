package student.inti.RecipeLab.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import student.inti.RecipeLab.R;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder>{
    private ArrayList<String> ingredients;
    private ArrayList<String> namaMakanan;
    private Context context;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String userId;
    private ArrayList<String> recipeIds;

    public ShoppingListAdapter(ArrayList<String> ingredients, Context context, ArrayList<String> namaMakanan, ArrayList<String> recipeIds){
        this.ingredients = ingredients;
        this.context = context;
        this.namaMakanan = namaMakanan;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();
        this.recipeIds = recipeIds;

    }

    public ShoppingListAdapter(ArrayList<String> ingredientNames) {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shopping_list_item, parent, false);

        return new ViewHolder(view);
    };

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.foodName.setText(namaMakanan.get(position));
        if(position != 0 && namaMakanan.get(position).equals(namaMakanan.get(position -1))){
            holder.foodName.setVisibility(View.GONE);
        }
        holder.ingredients.setText(ingredients.get(position));
        holder.checkBox.setOnCheckedChangeListener((compoundButton, b) ->
                mDatabase.child("Shopping_List").child(userId).child(recipeIds.get(position)).child(ingredients.get(position)).setValue(b));

    }

    @Override
    public int getItemCount() {

        return ingredients.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView foodName, ingredients;
        CheckBox checkBox;


        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            foodName = itemView.findViewById(R.id.textFoodName);
            ingredients = itemView.findViewById(R.id.IngredientToBuy);
            checkBox = itemView.findViewById(R.id.ShoppingListCheckBox);
        }
    }

}
