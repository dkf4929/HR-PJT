package project.hrpjt.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.hrpjt.member.dto.MemberSaveDto;
import project.hrpjt.member.service.MemberService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    @ResponseBody
    @PostMapping("/add")
    public String save(MemberSaveDto param) {
        memberService.save(param);

        return "저장완료";
    }

    @GetMapping("/add")
    public String saveForm() {
        return "members/add";
    }
}
