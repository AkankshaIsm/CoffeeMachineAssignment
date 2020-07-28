package domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class CoffeeMachine {
    private Outlet outlets;
    private Map<String, Integer> total_items_quantity;
    private Map<String, Map<String, Integer>> beverages;

}
