package back.ahwhew;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AhwhewApplication {

	public static void main(String[] args) {
		SpringApplication.run(AhwhewApplication.class, args);
	}

}
