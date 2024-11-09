package student.inti.RecipeLab.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.function.Consumer;

import student.inti.RecipeLab.R;
import student.inti.RecipeLab.RecipeDetailsActivity;
import student.inti.RecipeLab.adapter.RecipeListAdapter;
import student.inti.RecipeLab.adapter.ShoppingListAdapter;
import student.inti.RecipeLab.databinding.FragmentShoppingListBinding;
import student.inti.RecipeLab.interfaces.ItemOnClickListener;
import student.inti.RecipeLab.models.Settings;
import student.inti.RecipeLab.utility.Constants;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShoppingListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShoppingListFragment extends Fragment implements ItemOnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = ShoppingListFragment.class.getSimpleName();

    private FragmentShoppingListBinding binding;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShoppingListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShoppingListFragment newInstance(String param1, String param2) {
        ShoppingListFragment fragment = new ShoppingListFragment();
        Bundle args = new Bundle();

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


    private Settings userSettings;
    private String mealType;
    private RecipeListAdapter adapter;
    private DatabaseReference recipeReference;
    private FirebaseAuth mAuth;
    private ShoppingListAdapter shopListAdapter;
    private String userId;
    ArrayList<String> ingredientNames;
    ArrayList<String> foodNames ;
    ArrayList<String> recipeIds ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentShoppingListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        ingredientNames = new ArrayList<>();
        foodNames = new ArrayList<>();
        recipeIds = new ArrayList<>();

        resetPage();

        setListeners();
        return view;
    }

    private void setListeners() {
        binding.fabRefreshPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPage();
            }
        });
    }

    private void resetPage() {
        recipeReference = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_RECIPE_LOCATION).child(userId);
        recipeReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    DataSnapshot document = task.getResult();
                    if (document != null && document.exists()){
                         ingredientNames = new ArrayList<>();
                         foodNames = new ArrayList<>();
                         recipeIds = new ArrayList<>();


                        // Get Ingredients through individual recipe ID instead of everything
                        document.getChildren().forEach(new Consumer<DataSnapshot>() {
                            @Override
                            public void accept(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    String foodName;
                                    String recipeId;

                                    recipeId = dataSnapshot.child("id").getValue(String.class);
                                    foodName = dataSnapshot.child("label").getValue(String.class);



                                    dataSnapshot.child("ingredientLines").getChildren().forEach(new Consumer<DataSnapshot>() {
                                        @Override
                                        public void accept(DataSnapshot dataSnapshot) {
                                            String data = dataSnapshot.getValue(String.class);
                                            ingredientNames.add(data);

                                            foodNames.add(foodName);
                                            recipeIds.add(recipeId);
                                        }
                                    });
                                }
                                shopListAdapter = new ShoppingListAdapter(ingredientNames, getContext(), foodNames, recipeIds);
                                binding.ShopList.setAdapter(shopListAdapter);
                                binding.ShopListProgressBar.setVisibility(View.GONE);
                                binding.LoadingShopList.setVisibility(View.GONE);
                                binding.EmptyShopList.setVisibility(View.GONE);
                                binding.ShopList.setVisibility(View.VISIBLE);

                                shopListAdapter.notifyDataSetChanged();

                            }
                        });
                    }
                    else{
                        ingredientNames.clear();
                        foodNames.clear();
                        recipeIds.clear();
                        binding.ShopListProgressBar.setVisibility(View.GONE);
                        binding.LoadingShopList.setVisibility(View.GONE);
                        binding.EmptyShopList.setVisibility(View.VISIBLE);
                        if (shopListAdapter != null) {
                            shopListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }


    private void setLayoutManager(){
        // Set layout manager based on orientation
        if(binding.getRoot().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            binding.ShopList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        } else {
            binding.ShopList.setLayoutManager(new LinearLayoutManager(getContext()));
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


}