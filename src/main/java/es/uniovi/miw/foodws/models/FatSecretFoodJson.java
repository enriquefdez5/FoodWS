package es.uniovi.miw.foodws.models;

import com.fasterxml.jackson.databind.JsonNode;

public class FatSecretFoodJson {
    JsonNode servings;

    public FatSecretFoodJson(){}
    public FatSecretFoodJson(JsonNode servings) {
        this.servings = servings;
    }


    public void setServing(JsonNode servings) {
        this.servings = servings;
    }

    public JsonNode getServings() {
        return this.servings;
    }

}
