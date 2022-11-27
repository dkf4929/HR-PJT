package project.h5renewal.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.h5renewal.member.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    public Optional<Member> findByLoginId(String loginId);
}
