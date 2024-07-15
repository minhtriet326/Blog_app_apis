package com.blog.services;

import com.blog.entities.User;
import com.blog.payloads.UserDTO;

import java.util.List;
import java.util.Map;

public interface UserService {
    UserDTO addUser(UserDTO userDTO);
    List<UserDTO> getAllUsers();
    UserDTO getUserById(Integer userId);
    UserDTO updateUser(UserDTO userDTO, Integer userId);
    Map<String, String> deleteUser(Integer userId);
}
