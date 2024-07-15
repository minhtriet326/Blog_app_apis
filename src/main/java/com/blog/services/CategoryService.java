package com.blog.services;

import com.blog.payloads.CategoryDTO;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    //ADD
    CategoryDTO addCategory(CategoryDTO categoryDTO);
    //GET
    CategoryDTO getCategoryById(Integer categoryId);
    //GET ALL
    List<CategoryDTO> getAllCategories();
    //UPDATE
    CategoryDTO updateCategory(Integer categoryId ,CategoryDTO categoryDTO);
    //DELETE
    Map<String, String> deleteCategory(Integer categoryId);
}
