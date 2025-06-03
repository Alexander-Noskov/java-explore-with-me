package ru.practicum.ewm.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.UserMapper;
import ru.practicum.ewm.user.UserService;
import ru.practicum.ewm.user.dto.NewUserRequestDto;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Validated
public class AdminUserRestController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @RequestParam(required = false, defaultValue = "0") Integer from,
                                  @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Get users by ids: {}, from: {}, size: {}", ids, from, size);
        return userService.getUsers(ids, from, size).stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @PostMapping("users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid NewUserRequestDto dto) {
        log.info("Creating new user: {}", dto);
        return userMapper.toUserDto(userService.save(userMapper.toUserEntity(dto)));
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable @NotNull Long id) {
        log.info("Deleting user: {}", id);
        userService.deleteById(id);
    }
}
