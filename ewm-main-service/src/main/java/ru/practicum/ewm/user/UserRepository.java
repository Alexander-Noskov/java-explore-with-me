package ru.practicum.ewm.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("SELECT u " +
           "FROM UserEntity u " +
           "WHERE (:ids IS NULL OR u.id IN :ids) " +
           "ORDER BY u.id " +
           "LIMIT :size " +
           "OFFSET :from")
    List<UserEntity> getUsersByParam(
            @Param("ids") List<Long> ids,
            @Param("from") Integer from,
            @Param("size") Integer size
    );
}