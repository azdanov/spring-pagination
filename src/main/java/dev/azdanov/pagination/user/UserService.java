package dev.azdanov.pagination.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public PagedModel<UserDto> getUsers(Pageable pageable) {
        Page<UserDto> users = userRepository.findAll(pageable)
            .map(user -> new UserDto(user.getId(), user.getName(), user.getEmail()));

        return new PagedModel<>(users);
    }

}
