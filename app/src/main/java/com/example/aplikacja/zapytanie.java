package com.example.aplikacja;
import com.google.firebase.BuildConfig;

import java.time.temporal.ValueRange;

import okhttp3.*;


public class zapytanie {
    private static final String API_KEY = "API_KEY";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public static void getRecipe(String ingredients, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        String prompt = "Podaj prosty przepis, który mogę przygotować mając te składniki: " + ingredients;

        String json = "{\n" +
                "  \"model\": \"gpt-3.5-turbo\",\n" +
                "  \"messages\": [\n" +
                "    {\"role\": \"user\", \"content\": \"" + prompt + "\"}\n" +
                "  ]\n" +
                "}";

        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }
}
