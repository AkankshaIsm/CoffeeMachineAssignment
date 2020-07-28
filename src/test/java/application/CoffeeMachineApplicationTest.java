package application;

import domain.CoffeeMachine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class CoffeeMachineApplicationTest {

    CoffeeMachine coffeeMachine = new CoffeeMachine();

    @BeforeEach
    void setUp() throws IOException {
        coffeeMachine = CoffeeMachineApplication.getCoffeeMachine("src/test/resources/correct-input.json");
        CoffeeMachineApplication.initializeMaps(coffeeMachine);
    }

    @Test
    void getCoffeeMachine() {
        Assertions.assertNotNull(coffeeMachine);
        Assertions.assertEquals(1, coffeeMachine.getOutlets().getCount_n());
        Assertions.assertEquals(1, coffeeMachine.getBeverages().size());
        Assertions.assertEquals(250, coffeeMachine.getTotal_items_quantity().get("hot_water"));
    }

    @Test
    void invalidInput() {
       Assertions.assertThrows(IOException.class, () -> CoffeeMachineApplication.getCoffeeMachine("src/test/resources/invalid-input-format.json"));
    }

    @Test
    void missingFields() throws IOException {
        CoffeeMachine missingFieldsCoffeeMachine = CoffeeMachineApplication.getCoffeeMachine("src/test/resources/missing-field-input.json");
        Assertions.assertThrows(MissingFieldsException.class, () -> CoffeeMachineApplication.isInputComplete(missingFieldsCoffeeMachine));

        Assertions.assertDoesNotThrow(() -> CoffeeMachineApplication.isInputComplete(coffeeMachine));
    }

    @Test
    void invalidFilePath() {
        Assertions.assertThrows(FileNotFoundException.class, () ->  CoffeeMachineApplication.getCoffeeMachine("src/test/resources/input.json"));
    }

    @Test
    void initializeMaps() {
        Assertions.assertEquals(5 , CoffeeMachineApplication.INGREDIENT_MAP.size());
    }

    @Test
    void getCoffeeMachineStatus() {
        int beveragesWithZeroOutlets = CoffeeMachineApplication.getCoffeeMachineStatus(0);
        Assertions.assertEquals(0, beveragesWithZeroOutlets);

        int beveragesWithThreeOutlets = CoffeeMachineApplication.getCoffeeMachineStatus(30);
        Assertions.assertEquals(1, beveragesWithThreeOutlets);
    }

    @Test
    void allIngredientsAvailable() {
        String coffee = "coffee";
        String tea = "hot_tea";
        String gingerTea = "ginger_tea";
        CoffeeMachineApplication.initializeMaps(coffeeMachine);

        Assertions.assertFalse(CoffeeMachineApplication.allIngredientsAvailable(coffee, getCoffeeIngredientMap()));
        Assertions.assertFalse(CoffeeMachineApplication.allIngredientsAvailable(gingerTea, getGingerTeaIngredientMap()));
        Assertions.assertTrue(CoffeeMachineApplication.allIngredientsAvailable(tea, getTeaIngredientMap()));
    }

    @Test
    void serveBeverage() {
        String tea = "hot_tea";
        CoffeeMachineApplication.initializeMaps(coffeeMachine);
        CoffeeMachineApplication.serveBeverage(tea, getTeaIngredientMap());

        Assertions.assertEquals(20, CoffeeMachineApplication.INGREDIENT_MAP.get("sugar_syrup"));
        Assertions.assertNull(CoffeeMachineApplication.INGREDIENT_MAP.get("tea_leaves_syrup"));
    }

    private Map<String, Integer> getCoffeeIngredientMap() {
        Map<String, Integer> coffeeIngredientMap = new HashMap<>();
        coffeeIngredientMap.put("hot_water", 50);
        coffeeIngredientMap.put("hot_milk", 50);
        coffeeIngredientMap.put("coffee_syrup", 50);
        return coffeeIngredientMap;
    }


    private Map<String, Integer> getTeaIngredientMap() {
        Map<String, Integer> teaIngredientMap = new HashMap<>();
        teaIngredientMap.put("hot_water", 50);
        teaIngredientMap.put("hot_milk", 50);
        teaIngredientMap.put("ginger_syrup", 50);
        teaIngredientMap.put("sugar_syrup", 50);
        teaIngredientMap.put("tea_leaves_syrup", 50);
        return teaIngredientMap;
    }

    private Map<String, Integer> getGingerTeaIngredientMap() {
        Map<String, Integer> gingerTeaIngredientMap = new HashMap<>();
        gingerTeaIngredientMap.put("hot_water", 50);
        gingerTeaIngredientMap.put("hot_milk", 50);
        gingerTeaIngredientMap.put("ginger_syrup", 250);
        gingerTeaIngredientMap.put("sugar_syrup", 50);
        gingerTeaIngredientMap.put("tea_leaves_syrup", 50);
        return gingerTeaIngredientMap;
    }

}