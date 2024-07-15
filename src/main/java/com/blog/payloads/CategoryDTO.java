package com.blog.payloads;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {
    private Integer categoryId;

    @NotEmpty(message = "This field can't be empty")
    @Column(unique = true, length = 30)
    private String title;

    @NotEmpty(message = "This field can't be empty")
    private String description;
}
