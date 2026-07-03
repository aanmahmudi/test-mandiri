package com.mandiri.lending.domain.service;

import com.mandiri.lending.api.dto.CreateMemberRequest;
import com.mandiri.lending.api.dto.MemberResponse;
import com.mandiri.lending.common.BadRequestException;
import com.mandiri.lending.common.NotFoundException;
import com.mandiri.lending.domain.entity.Member;
import com.mandiri.lending.domain.repository.MemberRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public MemberResponse create(CreateMemberRequest request) {
        memberRepository.findByEmail(request.getEmail()).ifPresent(m -> {
            throw new BadRequestException("email already exists");
        });

        Member member = Member.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .email(request.getEmail())
                .build();

        Member saved = memberRepository.save(member);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public MemberResponse get(UUID id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new NotFoundException("member not found"));
        return toResponse(member);
    }

    @Transactional(readOnly = true)
    public Member getEntity(UUID id) {
        return memberRepository.findById(id).orElseThrow(() -> new NotFoundException("member not found"));
    }

    private MemberResponse toResponse(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}
