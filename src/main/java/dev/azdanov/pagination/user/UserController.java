package dev.azdanov.pagination.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public String listUsers(Pageable pageable, Model model) {
        PagedModel<UserDto> users = userService.getUsers(pageable);

        model.addAttribute("users", users);
        model.addAttribute("pageable", pageable);

        return "users";
    }
}
