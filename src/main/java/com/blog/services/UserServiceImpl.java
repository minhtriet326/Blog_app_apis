package com.blog.services;

import com.blog.auth.entities.Role;
import com.blog.auth.repositories.RoleRepository;
import com.blog.entities.User;
import com.blog.exceptions.CustomUniqueConstraintViolationException;
import com.blog.exceptions.ResourceNotFoundException;
import com.blog.payloads.UserDTO;
import com.blog.repositories.UserRepository;
import com.blog.utils.AppConstants;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO addUser(UserDTO userDTO) {

        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        User newUser = dtoToUser(userDTO);

        Role role = roleRepository.findById(AppConstants.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "roleId", Integer.toString(AppConstants.ROLE_USER)));

        Set<Role> roles = Set.of(role);

        newUser.setRoles(roles);

        try {

            User savedUser = userRepository.save(newUser);// chỗ này chính là lúc @Column(unique = true) phát huy tác dụng chứ @Valid vô dụng

            UserDTO newUserDTO = userToDTO(savedUser);

            return newUserDTO;

        } catch (DataIntegrityViolationException e) { //Vi phạm ràng buộc dữ liệu

            throw new CustomUniqueConstraintViolationException("This email address is already in use");// Thằng này là ngoại lệ tự tạo

        }
    }

    @Override
    public List<UserDTO> getAllUsers() {

        List<User> users = userRepository.findAll();

        List<UserDTO> userDTOs = new ArrayList<>();

        for (User user : users) {
            UserDTO userDTO = userToDTO(user);
            userDTOs.add(userDTO);
        }

//        List<UserDTO> userDTOs = users.stream().map(user -> userToDTO(user)).collect(Collectors.toList());

        return userDTOs;
    }

    @Override
    public UserDTO getUserById(Integer userId) {

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", Integer.toString(userId)));

        UserDTO userDTO = userToDTO(existingUser);

        return userDTO;
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO, Integer userId) {

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", Integer.toString(userId)));

        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        Role role = roleRepository.findById(AppConstants.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "roleId", Integer.toString(AppConstants.ROLE_USER)));

        Set<Role> roles = Set.of(role);

        User newUser = User.builder()
                .userId(userId)
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .roles(roles)
                .about(userDTO.getAbout())
                .build();

        try {

            User savedUser = userRepository.save(newUser);

            return userToDTO(savedUser);

        } catch (DataIntegrityViolationException e) {

            throw new CustomUniqueConstraintViolationException("Email address is already in use!");

        }
    }

    @Override
    public Map<String, String> deleteUser(Integer userId) {

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", Integer.toString(userId)));

        userRepository.delete(existingUser);

        return Map.of("Message", "User with Id " + userId + " has been deleted successfully!");
    }

    private User dtoToUser(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);// userId mà ko chuyền là null
    }

    private UserDTO userToDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
}
