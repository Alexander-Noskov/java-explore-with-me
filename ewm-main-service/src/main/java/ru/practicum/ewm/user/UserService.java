package ru.practicum.ewm.user;

import java.util.List;

public interface UserService {
    UserEntity save(UserEntity user);

    UserEntity get(Long id);

    List<UserEntity> getUsers(List<Long> ids, Integer from, Integer size);

    void delete(Long id);
}
