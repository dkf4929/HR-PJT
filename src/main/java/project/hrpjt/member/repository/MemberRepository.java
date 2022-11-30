package project.hrpjt.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.hrpjt.member.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    public Optional<Member> findByLoginId(String loginId);

    public Optional<Member> findByKakaoId(String id);

    public Optional<Member> findByKakaoMail(String mail);
}
