package io.sanctus.flavourpalette.ingredient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.sanctus.flavourpalette.recipe.Recipe;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "ingredient")
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingr_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipe_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Recipe recipe;

    @Column(name = "ingr_name")
    private String name;

    @Column(name = "dbName")
    private String dbName;

    @Column(name = "ingr_quantity")
    private String quantity;

    @Column(name = "ingr_size")
    private String size;

    @Column(name = "ingr_prep")
    private String prepType;

}