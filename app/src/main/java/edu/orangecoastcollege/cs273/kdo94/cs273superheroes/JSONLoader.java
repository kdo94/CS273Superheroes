package edu.orangecoastcollege.cs273.kdo94.cs273superheroes;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by kevin_000 on 10/10/2016.
 */

public class JSONLoader {
    public static ArrayList<Superheroes> loadJSONFromAsset(Context context) throws IOException{
        ArrayList<Superheroes> allSuperheroes = new ArrayList<>();
        String json = null;
        InputStream is = context.getAssets().open("Superheroes.JSON");
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        json = new String(buffer, "UTF-8");

        try{
            JSONObject jsonRootObject = new JSONObject(json);
            JSONArray allSuperheroesJSON = jsonRootObject.getJSONArray("Superheroes");
            int numberOfSuperheroes = allSuperheroesJSON.length();

            for (int i = 0; i < numberOfSuperheroes; i++){
                JSONObject superheroesJSON = allSuperheroesJSON.getJSONObject(i);
                Superheroes hero = new Superheroes();
                hero.setName(superheroesJSON.getString("Name"));
                hero.setUsername(superheroesJSON.getString("Username"));
                hero.setSuperpower(superheroesJSON.getString("Superpower"));
                hero.setOneThing(superheroesJSON.getString("One Thing"));

                allSuperheroes.add(hero);
            }
        }
        catch(JSONException e){
            Log.e("Superhero", e.getMessage());
        }

        return allSuperheroes;
    }
}
