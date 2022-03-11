package es.uniovi.miw.foodws.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.mysql.cj.xdevapi.JsonArray;
import es.uniovi.miw.foodws.models.FatSecretFoodJson;
import es.uniovi.miw.foodws.models.Ingredient;
import es.uniovi.miw.foodws.models.Menu;
import es.uniovi.miw.foodws.repositories.MenuRepository;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/menus")
public class MenusController {

    private static final ObjectMapper mapper = new ObjectMapper();


    private final String FATSECRET_API_URL = "https://platform.fatsecret.com/rest/server.api";
    private final String FATSECRET_API_FOOD_METHOD = "method=food.get.v2";
    private final String FATSECRET_API_FOOD_METHOD_FORMAT = "format=json";


    private final MenuRepository menuRepository;

    public MenusController(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    /**
     * Get all menus if param is null or get menus by name if param is not null
     *
     * @param menuName It can be null. If null returns every menu, if not returns matched
     * @return List of menus
     */
    @GetMapping
    @ApiOperation(value = "List menus",
            notes = "Returns a list of menus")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuName", value = "Name of the searched menu",
                    required = false, paramType = "path")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 200, message = "Ok",
                    response = Menu.class,
                    responseContainer = "List")
    })
    public ResponseEntity<?> getMenus
    (@RequestParam(value = "menuName", required = false) String menuName) {
        if (menuName != null)
            return ResponseEntity.
                    ok(menuRepository.findByMenuName(menuName));
        return ResponseEntity.ok(menuRepository.findAll());
    }

    /**
     * Get menu by id
     *
     * @param id of the menu
     * @return menu
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "Get menu",
            notes = "Get menu by id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id of the menu",
                    required = true, paramType = "path")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "Menu not found"),
            @ApiResponse(code = 200, message = "Ok",
                    response = Menu.class)
    })
    public ResponseEntity<?> getMenu(@PathVariable long id) {
        Optional<Menu> found = menuRepository.findById(id);
        if (found.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(found.get());
    }


    /**
     * Get menu ingredients
     *
     * @param id of the menu
     * @return menu ingredients
     */
    @GetMapping("/{id}/ingredients")
    public ResponseEntity<?> getMenuIngredients(@PathVariable long id) {
        Optional<Menu> found = menuRepository.findById(id);
        if (found.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(found.get().getIngredientSet());
    }

    //TODO
    @GetMapping("/{id}/ingredients/nutritional")
    public ResponseEntity<?> getMenuIngredientsNutritional(@PathVariable long id) {
        Optional<Menu> found = menuRepository.findById(id);
        if (found.isEmpty())
            return ResponseEntity.notFound().build();

        // Get token
        String access_token = TokenManager.getAccessToken();

        // Get ing info
        for (Ingredient ing : found.get().getIngredientSet()) {
            System.out.println("Access token: " + access_token);
            Response postIngResponse = ClientBuilder.newClient().
                    target(FATSECRET_API_URL).
                    request(MediaType.APPLICATION_JSON).
                    header(HttpHeaders.AUTHORIZATION, "Bearer " + access_token).
                    post(Entity.entity(FATSECRET_API_FOOD_METHOD + "&food_id=" + ing.getFatSecretId() +
                            "&" + FATSECRET_API_FOOD_METHOD_FORMAT, MediaType.APPLICATION_FORM_URLENCODED));
            String ingresponse = postIngResponse.readEntity(String.class);
            try {
                JsonNode jsonNode = mapper.readTree(ingresponse);
                JsonNode servings = jsonNode.get("servings").get("serving");
                if (servings.isArray()) {
                    ArrayNode arrayNode = (ArrayNode) jsonNode;
                    Iterator<JsonNode> itr = arrayNode.elements();
                    System.out.println("Is array")
                    while (itr.hasNext()) {
                        node = itr.next();
                        if (node.get("serving_description").equals("100g")) {

                        }
                    }
                }


                System.out.println(jsonNode);
            }
            catch (Exception e) {
                System.out.println("Nooooo");
            }
        }

        return ResponseEntity.ok("ey!");


//        for (Ingredient ing : found.get().getIngredientSet()) {
//            double grams = ing.getGrams();
//            fsf.setCalories(fsf.getCalories() + computePortion(grams, obj.calories));
//            fsf.setCarbohydrate(fsf.getCarbohydrate() + computePortion(grams * obj.carbohydrate));
//            fsf.setCarbohydratePercentage(fsf.getCarbohydratePercentage() + computePortion(grams, obj.carbohydratePercentage));
//            fsf.setFat(fsf.getFat() + computePortion(grams, obj.fat);
//            fsf.setFatPercentage(fsf.getFatPercentage() + computePortion(grams, obj.fatPercentage));
//            fsf.setProtein(fsf.getProtein() + computePortion(grams, obj.protein));
//            fsf.setProteinPercentage(fsf.getProteinPercentage() + computePortion(grams, obj.proteinPercentage));
//        }

    }



    private void addKeys(String currentPath, JsonNode jsonNode, Map<String, String> map, List<Integer> suffix) {
        if (jsonNode.isObject()) {
            ObjectNode objectNode = (ObjectNode) jsonNode;
            Iterator<Map.Entry<String, JsonNode>> iter = objectNode.fields();
            String pathPrefix = currentPath.isEmpty() ? "" : currentPath + "-";

            while (iter.hasNext()) {
                Map.Entry<String, JsonNode> entry = iter.next();
                addKeys(pathPrefix + entry.getKey(), entry.getValue(), map, suffix);
            }
        } else if (jsonNode.isArray()) {
            ArrayNode arrayNode = (ArrayNode) jsonNode;

            for (int i = 0; i < arrayNode.size(); i++) {
                suffix.add(i + 1);
                addKeys(currentPath, arrayNode.get(i), map, suffix);

                if (i + 1 <arrayNode.size()){
                    suffix.remove(arrayNode.size() - 1);
                }
            }
        }
        else if (jsonNode.isValueNode()) {
            if (currentPath.contains("-")) {
                for (int i = 0; i < suffix.size(); i++) {
                    currentPath += "-" + suffix.get(i);
                }

                suffix = new ArrayList<>();
            }

            ValueNode valueNode = (ValueNode) jsonNode;
            map.put(currentPath, valueNode.asText());
        }
    }
    private double computePortion(double grams, double portionValue) {
        int total = 100;
        return grams * portionValue / total;
    }

    /**
     * Add menu
     *
     * @param menu to add
     * @return menu added
     */
    @PostMapping
    public ResponseEntity<?> postMenu(@Valid @RequestBody Menu menu) {
        menuRepository.saveAndFlush(menu);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(menu.getId())
                .toUri();
        return ResponseEntity.created(location).body(menu);
    }

    /**
     * Updates menu found by id
     *
     * @param id   of the menu to update
     * @param menu to update
     * @return updated menu
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> putMenu(@PathVariable long id, @Valid
    @RequestBody Menu menu) {
        Optional<Menu> found = menuRepository.findById(id);
        if (found.isEmpty())
            return ResponseEntity.notFound().build();
        Menu current = found.get();
        current.setMenuName(menu.getMenuName());
        current.setMenuDescription(menu.getMenuDescription());
        current.setMenuPrice(menu.getMenuPrice());
        current.setIngredientSet(menu.getIngredientSet());
        current.setRestaurant(menu.getRestaurant());
        menuRepository.saveAndFlush(current);
        return ResponseEntity.ok(current);
    }

    /**
     * Deletes an menu found by id
     *
     * @param id of the menu to delete
     * @return deleted menu
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMenu(@PathVariable long id) {
        Optional<Menu> found = menuRepository.findById(id);
        if (found.isEmpty())
            return ResponseEntity.notFound().build();
        Menu current = found.get();
        menuRepository.delete(current);
        return ResponseEntity.ok(current);
    }


}
