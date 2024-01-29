package com.project.sfoc.domain.member.repository;

import com.project.sfoc.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
