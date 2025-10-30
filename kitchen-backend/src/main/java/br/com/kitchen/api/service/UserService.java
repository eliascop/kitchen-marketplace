package br.com.kitchen.api.service;

import br.com.kitchen.api.dto.UserDTO;
import br.com.kitchen.api.enumerations.Role;
import br.com.kitchen.api.mapper.AddressMapper;
import br.com.kitchen.api.model.Address;
import br.com.kitchen.api.model.Seller;
import br.com.kitchen.api.model.User;
import br.com.kitchen.api.repository.jpa.SellerRepository;
import br.com.kitchen.api.repository.jpa.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService extends GenericService<User, Long> {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(SellerRepository sellerRepository,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        super(userRepository, User.class);
        this.sellerRepository = sellerRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void promoteToAdmin(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found."));

        user.getRoles().add(Role.ROLE_ADMIN);
        userRepository.save(user);
    }

    @Transactional
    public void promoteToSeller(String login, String storeName) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found."));

        user.getRoles().add(Role.ROLE_SELLER);
        userRepository.save(user);

        Seller seller = new Seller();
        seller.setUser(user);
        seller.setStoreName(storeName);
        seller.setBlocked(false);
        sellerRepository.save(seller);
    }

    @Transactional
    public void updateSellerStore(User user_, String storeName, boolean storeStatus){
        Seller seller = sellerRepository.findById(user_.getId())
                .orElseThrow(() -> new RuntimeException("Seller not found."));

        seller.setStoreName(storeName);
        seller.setBlocked(storeStatus);
        sellerRepository.save(seller);
    }

    public User updateUser(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!user.getLogin().equals(userDTO.getLogin())) {
            userRepository.findByLogin(userDTO.getLogin())
                    .ifPresent(u -> { throw new RuntimeException("This login already exists."); });
        }

        user.setLogin(userDTO.getLogin());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setName(userDTO.getName());

        List<Address> updatedAddresses = AddressMapper.toEntityList(userDTO.getAddresses(), user);
        user.getAddresses().clear();
        user.getAddresses().addAll(updatedAddresses);

        return userRepository.save(user);
    }


    public User registerUser(User user) {
        userRepository.findByLogin(user.getLogin())
                .ifPresent(u -> { throw new RuntimeException("user already exists."); });

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(Role.ROLE_USER));
        user.getAddresses().forEach(address -> address.setUser(user));
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<UserDTO> findAllUsers() {
        return super.findAll()
                .stream()
                .map(UserDTO::new)
                .toList();
    }

    public Optional<UserDTO> findUserById(Long id){
        return super.findById(id)
                .stream()
                .map(UserDTO::new)
                .findAny();
    }

    public List<UserDTO> findUserByName(String name){
        return super.findByField("login", name)
                .stream()
                .map(UserDTO::new)
                .toList();
    }
}
