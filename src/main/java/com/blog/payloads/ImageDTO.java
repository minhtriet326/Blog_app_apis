package com.blog.payloads;

import com.blog.entities.Post;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ImageDTO {
    private Integer imageId;

    @NotBlank(message = "This field can't be blank")
    private String imageName;

    @NotEmpty(message = "This field can't be empty")
    private String postTitle;
}
