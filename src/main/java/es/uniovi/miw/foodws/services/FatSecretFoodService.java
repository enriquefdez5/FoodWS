package es.uniovi.miw.foodws.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import es.uniovi.miw.foodws.models.FatSecretFood;
import es.uniovi.miw.foodws.models.Ingredient;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Iterator;
import java.util.Set;

@Service
public class FatSecretFoodService {

    private final static String FATSECRET_API_FOOD_METHOD_FORMAT = "format=json";
    private final static String FATSECRET_API_FOOD_METHOD = "method=food.get.v2";
    private final static String FATSECRET_API_URL = "https://platform.fatsecret.com/rest/server.api";

    public FatSecretFood getFoodInfo(Set<Ingredient> ingredientSet) {
        // Get token
        String access_token = TokenManager.getAccessToken();

        // Get ing info
        double calories = 0;
        double carbo = 0;
        double fat = 0;
        double protein = 0;

        for (Ingredient ing : ingredientSet) {
            double grams = ing.getGrams();
            Response postIngResponse = ClientBuilder.newClient().
                    target(FATSECRET_API_URL).
                    request(MediaType.APPLICATION_JSON).
                    header(HttpHeaders.AUTHORIZATION, "Bearer " + access_token).
                    post(Entity.entity(FATSECRET_API_FOOD_METHOD + "&food_id=" + ing.getFatSecretId() +
                            "&" + FATSECRET_API_FOOD_METHOD_FORMAT, MediaType.APPLICATION_FORM_URLENCODED));
            String ingresponse = postIngResponse.readEntity(String.class);

            JsonNode jsonNode;
            try {
                jsonNode = new ObjectMapper().readTree(ingresponse);
            } catch (JsonProcessingException e) {
                return null;
            }
            JsonNode serving = jsonNode.get("food").get("servings").get("serving");
            if (serving.isArray()) {
                ArrayNode arrayNode = (ArrayNode) serving;
                Iterator<JsonNode> itr = arrayNode.elements();
                while (itr.hasNext()) {
                    JsonNode node = itr.next();
                    if (node.get("serving_description").toString().equals("\"100 g\"")) {
                        calories += computePortion(grams, Double.parseDouble(String.valueOf(node.get("calories")).
                                replace("\"", "")));
                        carbo += computePortion(grams, Double.parseDouble(String.valueOf(node.get("carbohydrate")).
                                replace("\"", "")));
                        fat += computePortion(grams, Double.parseDouble(String.valueOf(node.get("fat")).
                                replace("\"", "")));
                        protein += computePortion(grams, Double.parseDouble(String.valueOf(node.get("protein")).
                                replace("\"", "")));
                    }
                }
            } else {
                calories += computePortion(grams, Double.parseDouble(String.valueOf(serving.get("calories")).
                        replace("\"", "")));
                carbo += computePortion(grams, Double.parseDouble(String.valueOf(serving.get("carbohydrate")).
                        replace("\"", "")));
                fat += computePortion(grams, Double.parseDouble(String.valueOf(serving.get("fat")).
                        replace("\"", "")));
                protein += computePortion(grams, Double.parseDouble(String.valueOf(serving.get("protein")).
                        replace("\"", "")));
            }
        }
        return new FatSecretFood(calories, carbo, protein, fat);
    }

    private double computePortion(double grams, double portionValue) {
        int total = 100;
        return grams * portionValue / total;
    }
}
