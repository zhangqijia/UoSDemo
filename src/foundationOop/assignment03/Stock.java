package foundationOop.assignment03;

import foundationOop.assignment03.drink.Drink;
import foundationOop.assignment03.food.Ingredient;
import foundationOop.assignment03.food.Meal;

import java.util.HashMap;
import java.util.Map;

/**
 * the stock of restaurant ingredients
 */
public class Stock {

    /**
     * every chef of this restaurant uses one stock.
     */
    private static Map<Ingredient, Double> ingredientStock = new HashMap<>();

    public static Map<Ingredient, Double> getIngredientStock() {
        return ingredientStock;
    }

    /**
     * add ingredients into stock
     *
     * @param ingredient ingredient
     * @param amount     the amount
     */
    public static void addIngredient(Ingredient ingredient, double amount) {
        Double balance = ingredientStock.get(ingredient);
        // if we have remaining ingredient in stock, add new weight with old
        if (balance == null) {
            balance = amount;
        } else {
            balance = balance + amount;
        }
        ingredientStock.put(ingredient, balance);
    }

    /**
     * find ingredient's detail by name
     *
     * @param ingredientName name of ingredient
     * @return instance of ingredient
     */
    public static Ingredient searchIngredientDetail(String ingredientName) {
        for (Ingredient ingredient : ingredientStock.keySet()) {
            if (ingredient.getName().equals(ingredientName)) {
                return ingredient;
            }
        }
        return null;
    }

    /**
     * check whether restaurant has enough ingredients for this order
     *
     * @param meal      the meal ordered
     * @param mealCount the count ordered
     * @return true: have enough ingredients  false: don't have enough ingredients
     */
    public static boolean checkMealIngredients(Meal meal, int mealCount) {
        Map<Ingredient, Double> ingredientMap = meal.getIngredientMap();
        for (Map.Entry<Ingredient, Double> entry : ingredientMap.entrySet()) {
            Ingredient ingredient = entry.getKey();
            double needWeight = entry.getValue() * mealCount;
            Double stock = ingredientStock.get(ingredient);
            if (needWeight > stock) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkDrinnkIngredients(Drink drink, int drinkCount) {
        Map<Ingredient, Double> ingredientMap = drink.getIngredientMap();
        for (Map.Entry<Ingredient, Double> entry : ingredientMap.entrySet()) {
            Ingredient ingredient = entry.getKey();
            double needWeight = entry.getValue() * drinkCount;
            Double stock = ingredientStock.get(ingredient);
            if (needWeight > stock) {
                return false;
            }
        }
        return true;
    }

    /**
     * reduce the ingredients amount in stock
     *
     * @param meal       meal
     * @param mealCount  ordered count of meal
     * @param drink      drink
     * @param drinkCount ordered count of drink
     */
    public static void reduceIngredients(Meal meal, int mealCount, Drink drink, int drinkCount) {
        reduceStockAmount(mealCount, meal.getIngredientMap());
        reduceStockAmount(drinkCount, drink.getIngredientMap());
    }

    /**
     * reduce and update the amount of stock
     *
     * @param count         count
     * @param ingredientMap ingredients list
     */
    private static void reduceStockAmount(int count, Map<Ingredient, Double> ingredientMap) {
        for (Map.Entry<Ingredient, Double> entry : ingredientMap.entrySet()) {
            Ingredient key = entry.getKey();
            Double value = entry.getValue();
            Double stockAmount = ingredientStock.get(key);
            stockAmount = stockAmount - value * count;
            ingredientStock.put(key, stockAmount);
        }
    }
}
