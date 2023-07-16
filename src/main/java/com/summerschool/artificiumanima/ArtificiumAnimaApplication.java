package com.summerschool.artificiumanima;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAutoConfiguration;

@SpringBootApplication(exclude = {GroovyTemplateAutoConfiguration.class})
public class ArtificiumAnimaApplication {

  public static void main(String[] args) {
    SpringApplication.run(ArtificiumAnimaApplication.class, args);
  }

}
