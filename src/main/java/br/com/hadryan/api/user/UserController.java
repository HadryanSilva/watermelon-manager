package br.com.hadryan.api.user;

import br.com.hadryan.api.user.request.*;
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

    /**
     * Public Register - Creates ADMIN user with Account
     * Only used to create an initial user
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        User userToRegister = userMapper.registerToUser(request);
        User userCreated = userService.registerNewAdmin(userToRegister);

        return ResponseEntity
                .created(URI.create("/api/v1/users/" + userCreated.getId()))
                .body(userMapper.userToResponse(userCreated));
    }

    /**
     * Criação interna - Adiciona usuário à conta atual
     * Requer autenticação e permissão ADMIN
     */
    @PostMapping("/create-internal")
    public ResponseEntity<UserResponse> createInternal(@Valid @RequestBody UserInternalRequest request) {
        User userToCreate = userMapper.internalToUser(request);
        User userCreated = userService.createInternalUser(userToCreate, request.getRoles());

        return ResponseEntity
                .created(URI.create("/api/v1/users/" + userCreated.getId()))
                .body(userMapper.userToResponse(userCreated));
    }

    /**
     * Atualizar usuário
     * Usuário pode atualizar próprios dados
     * ADMIN pode atualizar usuários da mesma conta
     */
    @PutMapping("/update")
    public ResponseEntity<UserResponse> update(@Valid @RequestBody UserPutRequest request) {
        User userToUpdate = userMapper.putToUser(request);
        User userUpdated = userService.update(userToUpdate);
        return ResponseEntity.ok(userMapper.userToResponse(userUpdated));
    }

    /**
     * Atualizar roles de um usuário
     * Apenas ADMIN pode executar
     */
    @PutMapping("/{id}/roles")
    public ResponseEntity<UserResponse> updateRoles(
            @PathVariable UUID id,
            @Valid @RequestBody UserRolesUpdateRequest request) {

        User userUpdated = userService.updateUserRoles(id, request.getRoles());
        return ResponseEntity.ok(userMapper.userToResponse(userUpdated));
    }

}
