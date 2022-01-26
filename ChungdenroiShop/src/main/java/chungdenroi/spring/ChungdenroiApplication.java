package chungdenroi.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

@SpringBootApplication
public class ChungdenroiApplication {

    public static void main(String[] args) throws ParseException {
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        System.out.println(LocalDate.now().format(formatter));


        SpringApplication.run(ChungdenroiApplication.class, args);
    }

}
