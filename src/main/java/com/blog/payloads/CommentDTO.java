package com.blog.payloads;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CommentDTO {
    private Integer commentId;

    @NotEmpty(message = "This field can't be empty")
    private String content;

    private Date addedDate;

    private Date lastUpdated;

    private String user;

    private String postTitle;
}
