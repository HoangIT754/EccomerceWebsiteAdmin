package com.example.Ecommerce_BE.service;

import com.example.Ecommerce_BE.dto.request.user.UserCreationRequest;
import com.example.Ecommerce_BE.dto.request.user.UserUpdateRequest;
import com.example.Ecommerce_BE.entity.Users;
import com.example.Ecommerce_BE.repository.ProductsRepository;
import com.example.Ecommerce_BE.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ProductsRepository productsRepository;

    @Autowired
    public UsersService(UsersRepository usersRepository, ProductsRepository productsRepository) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.productsRepository = productsRepository;
    }

    public Users createUser(UserCreationRequest request) {
        if (usersRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        Users user = new Users();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setDistrict(request.getDistrict());
        user.setCity(request.getCity());
        user.setUsername(request.getUsername());

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(hashedPassword);

        user.setBirthdate(request.getBirthdate());
        user.setActive(1);

        return usersRepository.save(user);
    }

    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    public Users getUserById(UUID id) {
        return usersRepository.findById(id).orElseThrow(() -> new RuntimeException("User Not Found"));
    }

    public Users updateUserById(UUID userId, UserUpdateRequest request) {
        Users user = usersRepository.findById(userId).orElseThrow(() -> new RuntimeException("User Not Found"));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setDistrict(request.getDistrict());
        user.setCity(request.getCity());

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(hashedPassword);

        user.setBirthdate(request.getBirthdate());
        user.setActive(request.getActive());

        return usersRepository.save(user);
    }

    public void deleteUserById(UUID userId) {
        usersRepository.deleteById(userId);
    }

    public boolean updateProfileByUsername(String username, String firstName, String lastName, String email) {
        Optional<Users> userOptional = usersRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);

            usersRepository.save(user);
            return true;
        }

        return false;
    }
}