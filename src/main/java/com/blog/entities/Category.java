package com.blog.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryId;

    @NotEmpty(message = "This field can't be empty")
    @Column(unique = true, length = 30)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //cascade = CascadeType.ALL -> các thao tác trên các đt liên quan cx sẽ đc thực hiện khi các thao tác đó thực hiện trên đt hiện tại
    //fetch = FetchType.LAZY -> data của posts chỉ tải khi nó(thuộc tính) thực sự được truy cập(vì quá nhiều post), còn .EAGER thì tải ngay khi đt đc truy cập
    private List<Post> posts = new ArrayList<>();
}
