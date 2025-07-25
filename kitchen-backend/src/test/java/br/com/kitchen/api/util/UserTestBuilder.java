package br.com.kitchen.api.util;

import br.com.kitchen.api.model.User;

public class UserTestBuilder {

    public static User buildUserWithId(Long id) {
        User user = new User();
        user.setId(id);
        user.setLogin("user_" + id);
        user.setName("John doe");
        user.setPassword("pass_" + id);
        return user;
    }

    public static User buildWithoutId() {
        User user = new User();
        user.setLogin("user_test");
        user.setName("John doe");
        user.setPassword("password");
        return user;
    }
}
