package com.blog.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "posts")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postId;

    @NotEmpty(message = "This field can't be empty")
    @Size(min = 1, max = 100, message = "Blog's title must be at least 1 character and maximum 100 characters!")
    private String title;

    @NotEmpty(message = "This field can't be empty")
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Image> images;

    @NotNull(message = "This field can't be null")
    private Date addedDate;

    @NotNull(message = "This field can't be null")
    private Date lastUpdated;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // tất cả các hành động CRUD (Create, Read, Update, Delete) trên entity chủ sẽ được chuyển tiếp tới các entity liên quan
    private Set<Comment> comments = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
