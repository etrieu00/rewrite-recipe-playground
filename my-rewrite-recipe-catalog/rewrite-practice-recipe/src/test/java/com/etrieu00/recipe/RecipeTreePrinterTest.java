package com.etrieu00.recipe;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

public class RecipeTreePrinterTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new RecipeTreePrinter());
    }

    @Test
    void addAnnotationToMethod() {
        rewriteRun(
            java(
                """
                        package com.etrieu00.demo.controller;
                    
                        import org.springframework.web.bind.annotation.GetMapping;
                        import org.springframework.web.bind.annotation.RestController;
                    
                        @RestController
                        public class DemoController {
                    
                          public record Person(String name, Integer age) {
                          }
                    
                          @GetMapping("/person")
                          public Person retrievePerson() {
                            return new Person("Eric", 27);
                          }
                    
                          @GetMapping("/person2")
                          public Person retrievePerson2() {
                            return new Person("Eric", 27);
                          }
                        }
                    """
            )
        );
    }
}
