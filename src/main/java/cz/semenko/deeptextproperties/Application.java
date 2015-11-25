package cz.semenko.deeptextproperties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import cz.semenko.deeptextproperties.config.DynamicProperties;

@SpringBootApplication
public class Application {
    
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        DynamicProperties dynamicProperties = ctx.getBean(DynamicProperties.class);
        dynamicProperties.getProperty("first");
        dynamicProperties.getProperty("second");
        dynamicProperties.setProperty("new", "new");
    }

}
