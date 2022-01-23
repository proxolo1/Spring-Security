package com.example.demo.controllers;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("user")
public class UserController {
public final String defaultRole="ROLE_USER";
@Autowired
 private BCryptPasswordEncoder encoder;

@Autowired
UserRepository userRepository;
@PostMapping("addUser")
    public String addUser(@RequestBody User user){
    if(user.getRoles()==null)
    user.setRoles(defaultRole);
    String encodedpwd=encryptPassword(user.getPassword());
    user.setPassword(encodedpwd);
    userRepository.save(user);
    return user.getUserName()+" is successfully added";
}
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@GetMapping("getUsers")
    public List<User> getUsers(){
    return userRepository.findAll();
}
@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
@PutMapping("updateuser/{id}")
    public String updateUser(@PathVariable int id,@RequestBody User updateUser){
    Optional<User>user=userRepository.findById(id);
    user.get().setUserName(updateUser.getUserName());
    user.get().setPassword(encryptPassword(updateUser.getPassword()));
    user.get().setActive(updateUser.isActive());
    user.get().setRoles(updateUser.getRoles());

    userRepository.save(user.get());
    return user.get().getUserName()+" successfully updated";
}

public String encryptPassword(String password){
    return encoder.encode(password);
}
}
