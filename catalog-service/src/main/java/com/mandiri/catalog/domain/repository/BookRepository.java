package com.mandiri.catalog.domain.repository;

import com.mandiri.catalog.domain.entity.Book;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, UUID> {
    Optional<Book> findByIsbn(String isbn);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Book b where b.id = :id")
    Optional<Book> findByIdForUpdate(@Param("id") UUID id);

    @Query(
            value = """
                    select *
                    from books
                    where title ilike ('%%' || :q || '%%')
                       or author ilike ('%%' || :q || '%%')
                       or isbn ilike ('%%' || :q || '%%')
                    order by updated_at desc
                    limit :limit
                    """,
            nativeQuery = true
    )
    List<Book> search(@Param("q") String q, @Param("limit") int limit);
}
