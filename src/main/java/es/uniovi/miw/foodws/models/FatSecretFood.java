package es.uniovi.miw.foodws.models;

public class FatSecretFood {

    // calories by 100g
    private double calories;
    // carbohydrates by 100g
    private double carbohydrate;
    // protein by 100g
    private double protein;
    // fat by 100g
    private double fat;

    public FatSecretFood() {
        this.calories = 0;
        this.fat = 0;
        this.protein = 0;
        this.carbohydrate = 0;
    }

    public FatSecretFood(double calories, double carbohydrate, double protein, double fat) {
        this.calories = calories;
        this.carbohydrate = carbohydrate;
        this.protein = protein;
        this.fat = fat;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getCarbohydrate() {
        return carbohydrate;
    }

    public void setCarbohydrate(double carbohydrate) {
        this.carbohydrate = carbohydrate;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

}
