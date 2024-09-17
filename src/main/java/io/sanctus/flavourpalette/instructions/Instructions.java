package io.sanctus.flavourpalette.instructions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.sanctus.flavourpalette.recipe.Recipe;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Data
@Table(name = "instruction")
public class Instructions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "instr_id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipe_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Recipe recipe;

    @Column(name = "instr_text")
    private String stepText;

    @SuppressWarnings("unused")
    public Instructions(String instructions) {
        this.stepText = instructions;
    }
}
