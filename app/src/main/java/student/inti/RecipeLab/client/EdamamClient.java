package student.inti.RecipeLab.client;

import student.inti.RecipeLab.interfaces.EdamamApi;
import student.inti.RecipeLab.utility.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EdamamClient {
  private static Retrofit retrofit = null;

  public static EdamamApi getClient() {
    if(retrofit == null){
      retrofit = new Retrofit.Builder()
              .baseUrl(Constants.EDAMAM_API_BASE_URL)
              .addConverterFactory(GsonConverterFactory.create())
              .build();
    }
    return retrofit.create(EdamamApi.class);
  }
}
