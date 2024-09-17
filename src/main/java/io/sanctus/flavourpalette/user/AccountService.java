package io.sanctus.flavourpalette.user;

public interface AccountService {
    User loadUserByUsername(String username);
    void handlePasswordUpdate(String username,String password);
    void handleDeleteUserByUsername(String username);
}
