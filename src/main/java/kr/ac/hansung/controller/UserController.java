package kr.ac.hansung.controller;

import jakarta.validation.Valid;
import kr.ac.hansung.dto.PasswordChangeDto;
import kr.ac.hansung.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/password")
    public String passwordForm(Model model) {

        model.addAttribute(
                "passwordChangeDto",
                new PasswordChangeDto()
        );

        return "user/password";
    }

    @PostMapping("/password")
    public String changePassword(
            @Valid @ModelAttribute PasswordChangeDto passwordChangeDto,
            BindingResult bindingResult,
            Authentication authentication
    ) {

        if (bindingResult.hasErrors()) {
            return "user/password";
        }

        if (!passwordChangeDto.getNewPassword()
                .equals(passwordChangeDto.getConfirmPassword())) {

            bindingResult.rejectValue(
                    "confirmPassword",
                    "error.confirmPassword",
                    "새 비밀번호가 일치하지 않습니다."
            );

            return "user/password";
        }

        try {

            userService.changePassword(
                    authentication.getName(),
                    passwordChangeDto.getCurrentPassword(),
                    passwordChangeDto.getNewPassword()
            );

        } catch (IllegalArgumentException e) {

            bindingResult.rejectValue(
                    "currentPassword",
                    "error.currentPassword",
                    e.getMessage()
            );

            return "user/password";
        }

        return "redirect:/user/password/success";
    }

    @GetMapping("/password/success")
    public String passwordChangeSuccess() {
        return "user/password-success";
    }
}