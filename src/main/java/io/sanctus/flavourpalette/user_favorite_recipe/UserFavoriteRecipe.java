package io.sanctus.flavourpalette.user_favorite_recipe;

import jakarta.persistence.*;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Data
@Table(name = "user_favorites")
public class UserFavoriteRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "recipe_id")
    private String recipeId;
}
