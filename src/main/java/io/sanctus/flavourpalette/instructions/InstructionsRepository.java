package io.sanctus.flavourpalette.instructions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstructionsRepository extends JpaRepository<Instructions, Integer> {
    Optional<List<Instructions>> findAllByRecipeRecipeId(String authorId);
}
