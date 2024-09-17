package io.sanctus.flavourpalette.repository;

import io.sanctus.flavourpalette.user.User;
import io.sanctus.flavourpalette.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserRepositoryTest {

    private final UserRepository userRepository;
    private final User user;

    @Autowired
    public UserRepositoryTest(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.user = User.builder().userId(1).username("Test").password("test").build();
    }
    @BeforeEach
    void init(){
        userRepository.save(user);
    }
    @AfterEach
    void tearDown(){
        userRepository.deleteAll();
    }

    @Test
    void Save_FindAll_ReturnsSavedUser() {
        List<User> userList = userRepository.findAll();
        assertThat(userList).isNotEmpty()
                .hasSize(1);
        assertThat(userList.getFirst().getUsername()).isEqualTo("Test");
    }

    @Test
    void FindByUsername_FindBy_ReturnsUserByUserName() {
        Optional<User> userOpt = userRepository.findByUsername(user.getUsername());
        User userActual = null;
        if (userOpt.isPresent()) {
            userActual = userOpt.get();
        }
        assertThat(userActual).isNotNull();
        assertThat(userActual.getUsername()).matches("Test");
        assertThat(userActual.getUserId()).isEqualTo(1);
    }

//  The delete user test has a middle assertion to prove that the user was saved successfully before deleting
    @Test
    void DeleteByUserName_FindBy_ReturnsNull() {
        Optional<User> userOpt = userRepository.findByUsername(user.getUsername());
        User userActual = null;
        if (userOpt.isPresent()) {
            userActual = userOpt.get();
        }
        assertThat(userActual).isNotNull();
        userRepository.deleteByUsername(userActual.getUsername());
        Optional<User> deletedOpt = userRepository.findByUsername(user.getUsername());
        User deletedActual = null;
        if (deletedOpt.isPresent()) {
            deletedActual = deletedOpt.get();
        }
        assertThat(deletedActual).isNull();
    }
}
