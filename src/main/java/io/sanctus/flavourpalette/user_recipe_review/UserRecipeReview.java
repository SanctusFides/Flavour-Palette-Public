package io.sanctus.flavourpalette.user_recipe_review;

import jakarta.persistence.*;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "user_ratings")
public class UserRecipeReview {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "recipe_id")
    private String recipeId;

    @Column(name = "rating")
    private int rating;
}
