package foundationOop.assignment03;

import foundationOop.assignment03.drink.Drink;
import foundationOop.assignment03.food.Meal;
import foundationOop.assignment03.restaurant.Menu;
import foundationOop.assignment03.restaurant.Stock;
import foundationOop.assignment03.sheffield.EasyReader;
import foundationOop.assignment03.tool.PrintTools;

/**
 * this is the foundationOop.assignment03.Entrance of the foundationOop.assignment03.restaurant.
 * In the main() method,
 * it will create a foundationOop.assignment03.restaurant.Menu instance and invoke load() and display() method.
 * <p>
 * load() method is to read today's meal list, foundationOop.assignment03.drink list, ingredient list from files in foundationOop.assignment03.recipe folder and
 * assign these contents to foundationOop.assignment03.restaurant.Menu instance fields and Stock class fields.
 * These files' content can be modified according to the real demands of the foundationOop.assignment03.restaurant.
 * For example, the type and quantity of ingredients this foundationOop.assignment03.restaurant purchased might be varied with the season.
 * <p>
 * display() method prints whole menu out in the console by a readable format.
 * <p>
 * after that, I simulate an order process.
 * Firstly, Users can order one kind of  meal in this menu and provide how many meals they want.
 * If the ingredients of this meal are enough, the order process will move to next stage.
 * <p>
 * Based on the meal ordered previously, console will print a recommended foundationOop.assignment03.drink got by invoking drinksRecommendation() in Meal.
 * <p>
 * Then, users can choose one kind of foundationOop.assignment03.drink and type in the number they want.
 * Similarly, we should check Stock by Stock class method.
 * <p>
 * After order is finished, the console will give the total price of the order.
 * This price is calculate by the cost of ingredients in every meal and foundationOop.assignment03.drink.
 * Additionally, The price of one meal add extra 20% of the cost of ingredients to cover staff costs etc..
 * <p>
 * If users agree to pay what they have ordered, the Stock will reduce the ingredients used by this order.
 * <p>
 * When you exit the program, it will print today's remaining Stock information.
 */
public class Entrance {
    public static void main(String[] args) {

        EasyReader easyReader = new EasyReader();

        Menu menu = new Menu();

        //0. load today's ingredients and recipes
        menu.load();
        // print today's ingredients' list of Stock
        Stock.printf();
        //1. print whole menu
        menu.display();

        // the process of ordering starts
        while (true) {
            //2. prompt what meal do you want to order
            Meal meal = null;
            int mealCount = 0;
            boolean ordering = true;
            while (ordering) {
                String mealName = easyReader.readString("What would you like to order? please type the name of one meal.\n");
                meal = menu.getMeal(mealName);
                if (meal == null) {
                    System.out.println("Sorry, we don't have this meal. Please check the menu.");
                } else {
                    // print the detail of the meal
                    System.out.println("This is the meal's detail:   ");
                    System.out.println(meal);
                    PrintTools.loadingAnimation(".", 200L, 3);
                    // 2.1 the count of meal
                    System.out.println("Got it! Then how many meals do you want?");
                    mealCount = easyReader.readInt();
                    // 2.3 check whether we have enough stock
                    boolean mealAvailable = Stock.checkMealIngredients(meal, mealCount);
                    if (!mealAvailable) {
                        System.out.println("sorry this meal does not have enough stock, please choose other meal");
                    } else {
                        ordering = false;
                    }
                }
            }

            //3. recommend some foundationOop.assignment03.drink
            System.out.println("Would you like some drinks?");
            Drink drinkRecommendation = meal.drinksRecommendation();
            if (drinkRecommendation != null) {
                PrintTools.loadingAnimation(".", 150L, 6);
                System.out.println("We highly recommend \"" + drinkRecommendation.getName() + "\" to you!~~");
            }
            PrintTools.loadingAnimation(".", 150L, 6);
            System.out.println("What do you think? ");
            //3.1 order a foundationOop.assignment03.drink
            Drink drink = null;
            int drinkCount = 0;
            boolean orderingDrink = true;
            while (orderingDrink) {
                String drinkName = easyReader.readString("Please type in the name of foundationOop.assignment03.drink you want? \n");
                drink = menu.getDrink(drinkName);
                if (drink == null) {
                    System.out.println("Sorry, we don't have this foundationOop.assignment03.drink. Please check the menu.");
                } else {
                    //3.2 ensure the count of foundationOop.assignment03.drink
                    drinkCount = easyReader.readInt("Got it! Then how many cups of foundationOop.assignment03.drink do you want? \n");
                    //3.3 check whether we have enough stock
                    boolean available = Stock.checkDrinkIngredients(drink, drinkCount);
                    if (!available) {
                        System.out.println("sorry this foundationOop.assignment03.drink does not have enough stock, please choose other foundationOop.assignment03.drink");
                    } else {
                        orderingDrink = false;
                    }
                }
            }

            //5. show the price
            System.out.println("You ordered successfully!!! Please pay and get you MealBox!!!");
            // print the receipt of this order
            PrintTools.printReceipt(meal, mealCount, drink, drinkCount);
            PrintTools.loadingAnimation(".", 200L, 3);
            //6. pay or not?
            String pay = easyReader.readString("Would you like to pay it? (yes/no) \n");
            if ("yes".equals(pay)) {
                System.out.println("Pay successfully, your meal is preparing!");
                // reduce the ingredients stock after customers pay successfully
                Stock.reduceIngredients(meal, mealCount, drink, drinkCount);
            } else {
                System.out.println("Thanks for your visiting!");
            }
            //7. thanks for you order, continue shopping or exit.
            String fin = easyReader.readString("Continuing shopping or exit? (any key/exit) \n");
            if ("exit".equals(fin)) {
                System.out.print("Restaurant is closing");
                PrintTools.loadingAnimation(".", 180L, 6);
                System.out.println("Make an inventory of the Stock...");
                // print today's Stock detail and exit
                Stock.printf();
                System.exit(0);
            }
        }
    }
}