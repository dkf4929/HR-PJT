package project.hrpjt.dayoff.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.hrpjt.dayoff.entity.DayOff;
import project.hrpjt.dayoff.repository.DayOffRepository;
import project.hrpjt.mail.MailSender;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class DayOffService {
    private final DayOffRepository dayOffRepository;

    public void sendMail() {
        List<DayOff> useTarget = dayOffRepository.findUseTarget();  // 연차사용 대상자를 찾아온다.
        List<String> contents = new ArrayList<>();
        List<String> mailAddress = new ArrayList<>();

        useTarget.forEach(target -> {
            if (target.getEmployee().getExternalMail() != null) {
                contents.add(String.join(
                        System.getProperty("line.separator"),
                        "연차사용 촉진 안내",
                        "이름 : " + target.getEmployee().getEmpNm(),
                        "남은 연차 : " + target.getAnnualDayOff()
                ));
                mailAddress.add(target.getEmployee().getExternalMail());
            }
        });

        MailSender.sendMail("연차사용 촉진 안내", contents, mailAddress);
    }
}
