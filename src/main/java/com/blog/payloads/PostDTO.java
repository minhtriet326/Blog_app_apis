package com.blog.payloads;

import com.blog.entities.Category;
import com.blog.entities.Comment;
import com.blog.entities.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PostDTO {
    private Integer postId;

    @NotEmpty(message = "This field can't be empty")
    @Size(min = 1, max = 100, message = "Blog's title must be at least 1 character and maximum 100 characters!")
    private String title;

    @NotEmpty(message = "This field can't be empty")
    private String content;

    private String imageName;

    private Date addedDate;

    private Date lastUpdated;

    private String user;

    private List<CommentDTO> commentDTOList;

    private String category;

    private String imageUrl;
}
