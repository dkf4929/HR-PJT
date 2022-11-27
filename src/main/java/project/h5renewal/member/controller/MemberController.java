package project.h5renewal.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import project.h5renewal.member.dto.MemberSaveDto;
import project.h5renewal.member.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/add")
    public String save(@RequestBody MemberSaveDto param) {
        memberService.save(param);

        return "저장완료";
    }
}
