package com.blog.services;

import com.blog.entities.Category;
import com.blog.exceptions.CustomUniqueConstraintViolationException;
import com.blog.exceptions.ResourceNotFoundException;
import com.blog.payloads.CategoryDTO;
import com.blog.repositories.CategoryRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryDTO addCategory(@Valid CategoryDTO categoryDTO) {
        Category newCategory = dtoToCategory(categoryDTO);

        try {

            Category savedCategory = categoryRepository.save(newCategory);

            return categoryToDTO(savedCategory);

        } catch (DataIntegrityViolationException e) {
            throw new CustomUniqueConstraintViolationException("This title is existed!");
        }
    }

    @Override
    public CategoryDTO getCategoryById(Integer categoryId) {

        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", Integer.toString(categoryId)));

        return categoryToDTO(existingCategory);
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        List<CategoryDTO> categoryDTOS = new ArrayList<>();

//        categories.forEach(category -> {
//            categoryDTOS.add(categoryToDTO(category));
//        });

        categoryDTOS = categories.stream().map(category -> categoryToDTO(category)).collect(Collectors.toList());

        return categoryDTOS;
    }

    @Override
    public CategoryDTO updateCategory(Integer categoryId, @Valid CategoryDTO categoryDTO) {

        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", Integer.toString(categoryId)));

        Category newCategory = Category.builder()
                .categoryId(categoryId)
                .title(categoryDTO.getTitle())
                .description(categoryDTO.getDescription())
                .build();

        Category updatedCategory = categoryRepository.save(newCategory);

        return categoryToDTO(updatedCategory);
    }

    @Override
    public Map<String, String> deleteCategory(Integer categoryId) {

        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", Integer.toString(categoryId)));

        categoryRepository.deleteById(categoryId);

        return Map.of("Message", "Category with id " + categoryId + " has been deleted successfully!");
    }

    private CategoryDTO categoryToDTO(Category category) {
        return modelMapper.map(category, CategoryDTO.class);
    }

    private Category dtoToCategory(CategoryDTO categoryDTO) {
        return modelMapper.map(categoryDTO, Category.class);
    }
}
