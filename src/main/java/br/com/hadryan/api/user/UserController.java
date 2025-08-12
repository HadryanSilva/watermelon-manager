package br.com.hadryan.api.user;

import br.com.hadryan.api.user.request.UserPostRequest;
import br.com.hadryan.api.user.request.UserPutRequest;
import br.com.hadryan.api.user.response.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable UUID id) {
        var userFound = userService.findById(id);
        return ResponseEntity.ok(userMapper.userToResponse(userFound));
    }

    @PostMapping("/create")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserPostRequest request) {
        var userToSave = userMapper.postToUser(request);
        var userCreated = userService.create(request.getAccountId(), userToSave);
        return ResponseEntity
                .created(URI.create("/api/v1/users/" + userCreated.getId()))
                .body(userMapper.userToResponse(userCreated));
    }

    @PutMapping("/edit")
    public ResponseEntity<UserResponse> update(@Valid @RequestBody UserPutRequest request) {
        var userToUpdate = userMapper.putToUser(request);
        var userUpdated = userService.update(userToUpdate);
        return ResponseEntity.ok(userMapper.userToResponse(userUpdated));
    }

}
