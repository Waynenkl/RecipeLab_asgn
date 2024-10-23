package student.inti.RecipeLab.interfaces;

import student.inti.RecipeLab.models.Hit;
import student.inti.RecipeLab.models.RecipeSearchResponse;
import student.inti.RecipeLab.utility.Constants;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EdamamApi {
  @GET("v2")
  Call<RecipeSearchResponse> getRecipesByMealType(
          @Query(Constants.SEARCH_TYPE_QUERY_PARAMETER) String recipeSearch,
          @Query(Constants.SEARCH_QUERY_PARAMETER) String searchString,
          @Query(Constants.APP_ID_QUERY_PARAMETER) String appId,
          @Query(Constants.API_KEY_QUERY_PARAMETER) String apikey,
          @Query(Constants.MEAL_TYPE_QUERY_PARAMETER) String mealType,
          @Query(Constants.DIET_LABEL_QUERY_PARAMETER) String[] diets,
          @Query(Constants.HEALTH_LABEL_QUERY_PARAMETER) String[] health);

  @GET("v2")
  Call<RecipeSearchResponse> getRecipesByKeyword(
          @Query(Constants.SEARCH_TYPE_QUERY_PARAMETER) String recipeSearch,
          @Query(Constants.SEARCH_QUERY_PARAMETER) String searchString,
          @Query(Constants.APP_ID_QUERY_PARAMETER) String appId,
          @Query(Constants.API_KEY_QUERY_PARAMETER) String apikey,
          @Query(Constants.DIET_LABEL_QUERY_PARAMETER) String[] diets,
          @Query(Constants.HEALTH_LABEL_QUERY_PARAMETER) String[] health
  );

  @GET("v2/{id}")
  Call<Hit> getRecipeById(
          @Path("id") String recipeId,
          @Query(Constants.SEARCH_TYPE_QUERY_PARAMETER) String recipeSearch,
          @Query(Constants.APP_ID_QUERY_PARAMETER) String appId,
          @Query(Constants.API_KEY_QUERY_PARAMETER) String apiKey
  );
}
