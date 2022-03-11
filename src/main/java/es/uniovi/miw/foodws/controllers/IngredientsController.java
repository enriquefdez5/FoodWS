package es.uniovi.miw.foodws.controllers;

import es.uniovi.miw.foodws.models.Ingredient;
import es.uniovi.miw.foodws.repositories.IngredientRepository;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/ingredients")
public class IngredientsController {

    private final IngredientRepository ingredientRepository;

    public IngredientsController(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    /**
     * Get all ingredients if param is null or get ingredients by name if param is not null
     *
     * @param ingredientName It can be null. If null returns every ingredient, if not returns matched
     * @return List of ingredients
     */
    @GetMapping
    @ApiOperation(value = "List ingredients",
            notes = "Returns a list of ingredients")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ingredientName", value = "Name of the searched ingredient",
                    paramType = "path")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 200, message = "Ok",
                    response = Ingredient.class,
                    responseContainer = "List")
    })
    public ResponseEntity<?> getIngredients
    (@RequestParam(value = "ingredientName", required = false) String ingredientName) {
        if (ingredientName != null)
            return ResponseEntity.
                    ok(ingredientRepository.findByIngredientName(ingredientName));
        return ResponseEntity.ok(ingredientRepository.findAll());
    }

    /**
     * Get ingredient by id
     *
     * @param id of the ingredient
     * @return ingredient
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "Get ingredient",
            notes = "Get ingredient by id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id of the ingredient",
                    required = true, paramType = "path")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "Ingredient not found"),
            @ApiResponse(code = 200, message = "Ok",
                    response = Ingredient.class)
    })
    public ResponseEntity<?> getIngredient(@PathVariable long id) {
        Optional<Ingredient> found = ingredientRepository.findById(id);
        if (found.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(found.get());
    }


    /**
     * Add ingredient
     *
     * @param ingredient to add
     * @return ingredient added
     */
    @PostMapping
    @ApiOperation(value = "Create ingredient",
            notes = "Add an ingredient to the database")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ingredient", value = "Ingredient",
                    required = true, paramType = "body")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 201, message = "Ok",
                    response = Ingredient.class)
    })
    public ResponseEntity<?> postIngredient(@Valid @RequestBody Ingredient ingredient) {
        ingredientRepository.saveAndFlush(ingredient);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(ingredient.getId())
                .toUri();
        return ResponseEntity.created(location).body(ingredient);
    }

    /**
     * Updates ingredient found by id
     *
     * @param id         of the ingredient to update
     * @param ingredient to update
     * @return updated ingredient
     */
    @PutMapping("/{id}")
    @ApiOperation(value = "Update ingredient",
            notes = "Update ingredient found by id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id of the ingredient to update",
                    required = true, paramType = "path"),
            @ApiImplicitParam(name = "ingredient", value = "Ingredient",
                    required = false, paramType = "body")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "Ingredient not found"),
            @ApiResponse(code = 200, message = "Ok",
                    response = Ingredient.class)
    })
    public ResponseEntity<?> putIngredient(@PathVariable long id, @Valid
    @RequestBody Ingredient ingredient) {
        Optional<Ingredient> found = ingredientRepository.findById(id);
        if (found.isEmpty())
            return ResponseEntity.notFound().build();
        Ingredient current = found.get();
        current.setIngredientName(ingredient.getIngredientName());
        current.setFatSecretId(ingredient.getFatSecretId());
        current.setGrams(ingredient.getGrams());
        current.setMenuSet(ingredient.getMenuSet());
        ingredientRepository.saveAndFlush(current);
        return ResponseEntity.ok(current);
    }

    /**
     * Deletes an ingredient found by id
     *
     * @param id of the ingredient to delete
     * @return deleted ingredient
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete an ingredient",
            notes = "Delete an ingredient found by id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id of the ingredient to delete",
                    required = true, paramType = "path"),
    })
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "Ingredient not found"),
            @ApiResponse(code = 200, message = "Ok",
                    response = Ingredient.class)
    })
    public ResponseEntity<?> deleteIngredient(@PathVariable long id) {
        Optional<Ingredient> found = ingredientRepository.findById(id);
        if (found.isEmpty())
            return ResponseEntity.notFound().build();
        Ingredient current = found.get();
        ingredientRepository.delete(current);
        return ResponseEntity.ok(current);
    }


}
