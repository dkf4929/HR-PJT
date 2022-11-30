package project.hrpjt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HrPjtApplication {

	public static void main(String[] args) {
		SpringApplication.run(HrPjtApplication.class, args);
	}

}
