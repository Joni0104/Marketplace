package ru.avito.marketplace.repository;

import ru.avito.marketplace.entity.Ad;
import ru.avito.marketplace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdRepository extends JpaRepository<Ad, Integer> {

    List<Ad> findAllByAuthor(User author);

    @Query("SELECT a FROM Ad a LEFT JOIN FETCH a.author WHERE a.id = :id")
    Optional<Ad> findByIdWithAuthor(@Param("id") Integer id);

    @Query("SELECT a FROM Ad a LEFT JOIN FETCH a.author")
    List<Ad> findAllWithAuthor();
}
