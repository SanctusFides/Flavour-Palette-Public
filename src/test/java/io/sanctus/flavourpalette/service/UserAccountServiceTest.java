package io.sanctus.flavourpalette.service;

import io.sanctus.flavourpalette.cloudinary_impl.CloudinaryImpl;
import io.sanctus.flavourpalette.user.AccountServiceImpl;
import io.sanctus.flavourpalette.user.User;
import io.sanctus.flavourpalette.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserAccountServiceTest {
    @MockBean
    CloudinaryImpl cloudinary;
    private final AccountServiceImpl accountService;
    private final UserRepository userRepository;
    private final User user1;

    @Autowired
    UserAccountServiceTest(AccountServiceImpl accountService,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           UserRepository userRepository) {
        this.accountService = accountService;
        this.userRepository = userRepository;
        this.user1 = User.builder().username("Test").password(bCryptPasswordEncoder.encode("test"))
                .authType("FORM").build();
    }
    @BeforeEach
    void init(){
        userRepository.save(user1);
    }
    @AfterEach
    void tearDown(){
        userRepository.deleteAll();
    }

    @Test
    void LoadUserByUserName_ReturnsUser() {
        User result = accountService.loadUserByUsername("Test");
        assertThat(result.getUserId()).isEqualTo(1);
        assertThat(result.getUsername()).isEqualTo("Test");
    }

    @Test
    void HandlePasswordUpdate_UpdatesPassword_VerifyChange() {
        User preChange = accountService.loadUserByUsername("Test");
        accountService.handlePasswordUpdate("Test","newpass");
        User postChange = accountService.loadUserByUsername("Test");
        assertThat(preChange.getPassword()).isNotEqualTo(postChange.getPassword());
    }

    @Test
    void HandleDeleteUserByUserName_FindAll_ReturnsEmpty() {
        List<User> preDelete = userRepository.findAll();
        assertThat(preDelete.getFirst().getUsername()).isEqualTo("Test");
        accountService.handleDeleteUserByUsername("Test");
        List<User> postDelete = userRepository.findAll();
        assertThat(postDelete).isEmpty();
    }
}
