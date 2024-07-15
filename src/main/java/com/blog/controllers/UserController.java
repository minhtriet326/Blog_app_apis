package com.blog.controllers;

import com.blog.payloads.UserDTO;
import com.blog.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //POST
    @Operation(
            summary = "Add new User to database",
            description = "We will add new User to database by providing UserDTO",
            tags = {"User", "post"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    content = @Content(schema = @Schema(implementation = UserDTO.class),
                                        mediaType = "application/json")
            )
    })
    @PostMapping("/addUser")
    public ResponseEntity<UserDTO> addUserHandler(@Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(userDTO));
    }

    //GET
    @Operation(
            summary = "Get all User from database",
            description = "We will get all User from database",
            tags = {"User", "get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = List.class),
                            mediaType = "application/json")
            )
    })
    @GetMapping("/getAllUser")
    public ResponseEntity<List<UserDTO>> getAllUserHandler() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
    }

    @Operation(
            summary = "Get a specific User from database",
            description = "We will get a User from database by Id",
            tags = {"User", "get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = UserDTO.class),
                            mediaType = "application/json")
            )
    })
    @GetMapping("/getUserById/{userId}")
    public ResponseEntity<UserDTO> getUserByIdHandler(@PathVariable Integer userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(userId));
    }

    //PUT
    @Operation(
            summary = "Update User to database",
            description = "We will update a User to database by providing User's Id and UserDTO",
            tags = {"User", "put"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = UserDTO.class),
                            mediaType = "application/json")
            )
    })
    @PutMapping("/updateUser/{userId}")
    public ResponseEntity<UserDTO> updateUserHandler(@PathVariable Integer userId,
                                                     @Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(userDTO, userId));
    }

    //DELETE
    @Operation(
            summary = "Delete User from database",
            description = "We will delete a User from database by providing userId",
            tags = {"User", "delete"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Map.class),
                            mediaType = "application/json")
            )
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<Map<String,String>> deleteUserHandler(@PathVariable Integer userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.deleteUser(userId));
    }
}
