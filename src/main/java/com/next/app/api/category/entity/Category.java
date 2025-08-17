package com.next.app.api.category.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 카테고리 Entity
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories", catalog = "purchase")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
}
