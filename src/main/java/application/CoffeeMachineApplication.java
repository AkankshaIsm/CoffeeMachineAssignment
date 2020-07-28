package application;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.CoffeeMachine;
import domain.Machine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CoffeeMachineApplication {

    static final Map<String, Integer> INGREDIENT_MAP = new HashMap<>();
    private static final Map<String, Map<String, Integer>> BEVERAGE_MAP = new HashMap<>();
    private static final String FILE_PATH = "src/main/resources/input.json";

    public static void main(String[] args) throws MissingFieldsException, IOException {
        CoffeeMachine coffeeMachine = getCoffeeMachine(FILE_PATH);
        if (isInputComplete(coffeeMachine)) {
            initializeMaps(coffeeMachine);
            int beveragesServed = getCoffeeMachineStatus(coffeeMachine.getOutlets().getCount_n());
            System.out.println("\nBeverages served in one round : " + beveragesServed);
        }
    }

    static CoffeeMachine getCoffeeMachine(String filePath) throws IOException {
        CoffeeMachine coffeeMachine;
        try {
            File inputFile = new File(filePath);
            ObjectMapper objectMapper = new ObjectMapper();
            coffeeMachine = objectMapper.readValue(inputFile, Machine.class).getMachine();
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        } catch (IOException e) {
            throw new IOException(e);
        }
        return coffeeMachine;
    }

    /* This function tests if the fields: outlets, beverages or total_item_quantity are missing from input json file.
       It returns the custom exception - MissingFieldsException */
    static boolean isInputComplete(CoffeeMachine coffeeMachine) throws MissingFieldsException {
        if (coffeeMachine != null && coffeeMachine.getOutlets() != null && coffeeMachine.getBeverages() != null && coffeeMachine.getTotal_items_quantity() != null)
            return true;

        if (coffeeMachine == null) {
            throw new MissingFieldsException("Missing Field - machine");
        }
        if (coffeeMachine.getOutlets() == null) {
            throw new MissingFieldsException("Missing Field - outlets");
        }
        if (coffeeMachine.getBeverages() == null) {
            throw new MissingFieldsException("Missing Field - beverages");
        }
        if (coffeeMachine.getTotal_items_quantity() == null) {
            throw new MissingFieldsException("Missing Field - total_items_quantity");
        }
        return false;
    }

    //Initializing IngredientMap and BeverageMap because we don't want to make updates in the original input file
    static void initializeMaps(CoffeeMachine coffeeMachine) {
        for (Map.Entry<String, Integer> element : coffeeMachine.getTotal_items_quantity().entrySet()) {
            INGREDIENT_MAP.put(element.getKey(), element.getValue());
        }

        for (Map.Entry<String, Map<String, Integer>> element : coffeeMachine.getBeverages().entrySet()) {
            BEVERAGE_MAP.put(element.getKey(), element.getValue());
        }
    }

    /* This is the main function which decides what all beverages are served */
    static int getCoffeeMachineStatus(int outlets) {
        int i_outlets = outlets;
        for (Map.Entry<String, Map<String, Integer>> element : BEVERAGE_MAP.entrySet()) {
            String beverage = element.getKey();
            if (outlets == 0) {
                System.out.println(beverage + " cannot be prepared because all outlets are being utilised");
            }
            else if (allIngredientsAvailable(beverage, element.getValue())) {
                serveBeverage(beverage, element.getValue());
                outlets--; //As beverage is prepared, outlet will be consumed.
            }
        }
        return i_outlets - outlets;
    }

    /* This functions tests if all ingredients for current beverage are available in sufficient quantity*/
    static boolean allIngredientsAvailable(String beverage, Map<String, Integer> ingredientList) {
        for (Map.Entry<String, Integer> ingredient : ingredientList.entrySet()) {
            //If Ingredient is not found in INGREDIENT_MAP, beverage cannot be prepared
            if (!INGREDIENT_MAP.containsKey(ingredient.getKey())) {
                System.out.println(beverage + " cannot be prepared because " + ingredient.getKey() + " is not available");
                return false;
            }
            //If Quantity is insufficient in IngredientMap, beverage cannot be prepared
            if (ingredient.getValue() > INGREDIENT_MAP.get(ingredient.getKey())) {
                System.out.println(beverage + " cannot be prepared because " + ingredient.getKey() + " is not sufficient");
                return false;
            }
        }
        return true;
    }

    /* Preparing the beverage now :p using ingredients. Quantity of ingredients in Coffee Machine will reduce as beverage is prepared. */
    static void serveBeverage(String beverage, Map<String, Integer> ingredientList) {
        for (Map.Entry<String, Integer> ingredient : ingredientList.entrySet()) {
            int itemQuantityInCoffeeMachine = INGREDIENT_MAP.get(ingredient.getKey());
            //If this beverage would need all of the ingredient quantity, there would be none left in coffee machine. Hence, removing from Map
            if (ingredient.getValue() == itemQuantityInCoffeeMachine) {
                INGREDIENT_MAP.remove(ingredient.getKey());
            } else {//Reducing Quantity
                INGREDIENT_MAP.put(ingredient.getKey(), itemQuantityInCoffeeMachine - ingredient.getValue());
            }
        }
        System.out.println(beverage + " is prepared");
    }
}
