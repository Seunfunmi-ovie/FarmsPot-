package ng.farmsPot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;



@SpringBootApplication
@EnableScheduling

public class FarmsPotApplication {
    public static void main(String[] args) {
        SpringApplication.run(FarmsPotApplication.class, args);
    }



}
