package project.hrpjt.employee.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import project.hrpjt.employee.repository.EmployeeRepository;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class CustomEmployeeDetailsService implements UserDetailsService {
    private final EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (checkMail(username)) {
            return employeeRepository.findByKakaoMail(username).orElseThrow(
                    () -> {
                        throw new UsernameNotFoundException("카카오 계정이 유효하지 않습니다.`");
                    });
        } else {
            return employeeRepository.findByEmpNo(username).orElseThrow(
                    () -> {
                        throw new UsernameNotFoundException("가입되지 않은 사용자입니다.");
                    });
        }
    }

    private boolean checkMail(String username) {
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(username);
        return m.matches();
    }
}
