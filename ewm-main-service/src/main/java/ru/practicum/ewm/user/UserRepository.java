package ru.practicum.ewm.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("select u from UserEntity u where (:ids is null or u.id in :ids) order by u.id limit :size offset :from")
    List<UserEntity> getUsersByParam(
            @Param("ids") List<Long> ids,
            @Param("from") Integer from,
            @Param("size") Integer size
    );
}