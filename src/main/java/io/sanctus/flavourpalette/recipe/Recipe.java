package io.sanctus.flavourpalette.recipe;

import lombok.*;
import jakarta.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "recipe")
public class Recipe {
    @Id
    @Column(name = "recipe_id")
    private String recipeId;
    
    @Column(name = "author_id")
    private String authorId;

    @Column(name = "recipe_name")
    private String recipeName;

    @Column(name = "db_name")
    private String dbName;

    @Column(name = "prep_time")
    private int prepTime;

    @Column(name = "cook_time")
    private int cookTime;

    @Column(name = "total_time")
    private int totalTime;

    @Column(name = "description")
    private String description;

    @Column(name = "image_ID")
    private String imageId;
}