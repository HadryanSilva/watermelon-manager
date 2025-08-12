package br.com.hadryan.api.user;

import br.com.hadryan.api.user.request.UserInternalRequest;
import br.com.hadryan.api.user.request.UserPutRequest;
import br.com.hadryan.api.user.request.UserRegisterRequest;
import br.com.hadryan.api.user.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User registerToUser(UserRegisterRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User internalToUser(UserInternalRequest request);

    @Mapping(target = "account", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User putToUser(UserPutRequest request);

    UserResponse userToResponse(User user);

}
