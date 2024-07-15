package com.blog.controllers;

import com.blog.payloads.CategoryDTO;
import com.blog.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(
            summary = "Add new Category to database", 
            description = "We will add a new Category to database by providing CategoryDTO",
            tags = {"Category", "post"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    content = @Content(schema = @Schema(implementation = CategoryDTO.class),
                                        mediaType = "application/json")
            )
    })
    @PostMapping("/addCategory")
    public ResponseEntity<CategoryDTO> addCategoryHandler(@Valid @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.addCategory(categoryDTO));
    }

    @Operation(
            summary = "Get all Categories from database",
            description = "We will get all Categories from database",
            tags = {"Category", "get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = List.class),
                            mediaType = "application/json")
            )
    })
    @GetMapping("/getAllCategories")
    public ResponseEntity<List<CategoryDTO>> getAllCategoriesHandler() {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getAllCategories());
    }

    @Operation(
            summary = "Get a specific Category from database",
            description = "We will get a Category from database by Id",
            tags = {"Category", "get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = CategoryDTO.class),
                            mediaType = "application/json")
            )
    })
    @GetMapping("/getCategoryById/{categoryId}")
    public ResponseEntity<CategoryDTO> getCategoryByIdHandler(@PathVariable Integer categoryId) {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getCategoryById(categoryId));
    }

    @Operation(
            summary = "Update Category to database",
            description = "We will update a Category to database by providing Category's Id and CategoryDTO",
            tags = {"Category", "put"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = CategoryDTO.class),
                            mediaType = "application/json")
            )
    })
    @PutMapping("/updateCategory/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Integer categoryId,
                                                      @Valid @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.updateCategory(categoryId, categoryDTO));
    }

    @Operation(
            summary = "Delete Category from database",
            description = "We will delete a Category from database",
            tags = {"Category", "delete"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Map.class),
                            mediaType = "application/json")
            )
    })
    @DeleteMapping("/deleteCategory/{categoryId}")
    public ResponseEntity<Map<String, String>> deleteCategoryHandler(@PathVariable Integer categoryId) {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.deleteCategory(categoryId));
    }
}
