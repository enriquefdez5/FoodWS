package es.uniovi.miw.foodws.models;

public class FatSecretFood {

    private Long id;

    // calories by 100g
    private double calories;
    // carbohydrates by 100g
    private double carbohydrate;
    // % carbohydrates
    private double carbohydratePercentage;
    // protein by 100g
    private double protein;
    // % protein
    private double proteinPercentage;
    // fat by 100g
    private double fat;
    // % fatPercentage
    private double fatPercentage;

    public FatSecretFood() {
    }

    public FatSecretFood(Long id, double calories, double carbohydrate, double carbohydratePercentage, double protein,
                         double proteinPercentage, double fat, double fatPercentage) {
        this.id = id;
        this.calories = calories;
        this.carbohydrate = carbohydrate;
        this.carbohydratePercentage = carbohydratePercentage;
        this.protein = protein;
        this.proteinPercentage = proteinPercentage;
        this.fat = fat;
        this.fatPercentage = fatPercentage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public double getCarbohydratePercentage() {
        return carbohydratePercentage;
    }

    public void setCarbohydratePercentage(double carbohydratePercentage) {
        this.carbohydratePercentage = carbohydratePercentage;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getProteinPercentage() {
        return proteinPercentage;
    }

    public void setProteinPercentage(double proteinPercentage) {
        this.proteinPercentage = proteinPercentage;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getFatPercentage() {
        return fatPercentage;
    }

    public void setFatPercentage(double fatPercentage) {
        this.fatPercentage = fatPercentage;
    }
}
