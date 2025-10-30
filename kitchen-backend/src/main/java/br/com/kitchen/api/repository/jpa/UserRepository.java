package br.com.kitchen.api.repository.jpa;

import br.com.kitchen.api.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends GenericRepository<User, Long> {
    Optional<User> findByLogin(String login);
}
