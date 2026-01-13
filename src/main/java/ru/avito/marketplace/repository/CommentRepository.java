package ru.avito.marketplace.repository;

import ru.avito.marketplace.entity.Ad;
import ru.avito.marketplace.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findAllByAd(Ad ad);

    Optional<Comment> findByIdAndAd(Integer id, Ad ad);

    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.author WHERE c.ad = :ad ORDER BY c.createdAt DESC")
    List<Comment> findAllByAdWithAuthor(@Param("ad") Ad ad);
}