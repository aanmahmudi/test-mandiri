package com.mandiri.lending.domain.repository;

import com.mandiri.lending.domain.entity.Loan;
import com.mandiri.lending.domain.entity.LoanStatus;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoanRepository extends JpaRepository<Loan, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from Loan l join fetch l.member where l.id = :id")
    Loan findByIdForUpdate(@Param("id") UUID id);

    List<Loan> findByMember_Id(UUID memberId);

    List<Loan> findByMember_IdAndStatus(UUID memberId, LoanStatus status);

    boolean existsByMember_IdAndBookIdAndStatus(UUID memberId, UUID bookId, LoanStatus status);

    @Query(
            value = """
                    select
                      m.id as member_id,
                      m.name as member_name,
                      coalesce(sum(case when l.status = 'ACTIVE' then 1 else 0 end), 0) as active_count,
                      count(l.id) as total_count
                    from members m
                    left join loans l on l.member_id = m.id
                    group by m.id, m.name
                    having coalesce(sum(case when l.status = 'ACTIVE' then 1 else 0 end), 0) >= :minActive
                    order by active_count desc, total_count desc
                    """,
            nativeQuery = true
    )
    List<MemberLoanSummaryRow> findMemberLoanSummary(@Param("minActive") long minActive);
}
