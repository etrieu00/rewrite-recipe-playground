package com.etrieu00.recipe;

import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

class RecipeLoggingAnnotationTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new RecipeLoggingAnnotation());
    }

    @Test
    void addAnnotationToMethod() {
        final @Language("java") var before = """
            package com.etrieu00.demo.controller;
            
            import org.springframework.web.bind.annotation.GetMapping;
            import org.springframework.web.bind.annotation.PostMapping;
            import org.springframework.web.bind.annotation.RestController;
            
            @RestController
            public class DemoController {
            
                public record Person(String name, Integer age) {
                }
            
                @GetMapping("/person")
                public Person retrievePerson() {
                    return new Person("Eric", 27);
                }
            
                @PostMapping("/person2")
                public Person retrievePerson2() {
                    return new Person("Eric", 27);
                }
            
                public Person retrievePerson3() {
                    return new Person("Eric", 27);
                }
            }
            """;
        final @Language("java") var after = """
            package com.etrieu00.demo.controller;
            
            import com.etrieu00.logging.annotation.AuditEndpoint;
            import org.springframework.web.bind.annotation.GetMapping;
            import org.springframework.web.bind.annotation.PostMapping;
            import org.springframework.web.bind.annotation.RestController;
            
            @RestController
            public class DemoController {
            
                public record Person(String name, Integer age) {
                }
            
                @AuditEndpoint
                @GetMapping("/person")
                public Person retrievePerson() {
                    return new Person("Eric", 27);
                }
            
                @AuditEndpoint
                @PostMapping("/person2")
                public Person retrievePerson2() {
                    return new Person("Eric", 27);
                }
            
                public Person retrievePerson3() {
                    return new Person("Eric", 27);
                }
            }
            """;
        rewriteRun(java(before, after));
    }

    @Test
    void doNotAddAnnotationToMethod() {
        rewriteRun(
            java(
                """
                        package com.etrieu00.demo.controller;
                    
                        import com.etrieu00.logging.annotation.AuditEndpoint;
                    
                        import org.springframework.web.bind.annotation.GetMapping;
                        import org.springframework.web.bind.annotation.PostMapping;
                        import org.springframework.web.bind.annotation.RestController;
                    
                        public class DemoController {
                    
                          public record Person(String name, Integer age) {
                          }
                    
                          @AuditEndpoint
                          @GetMapping("/person")
                          public Person retrievePerson() {
                            return new Person("Eric", 27);
                          }
                    
                          @PostMapping("/person2")
                          public Person retrievePerson2() {
                            return new Person("Eric", 27);
                          }
                        }
                    """
            )
        );
    }

    @Test
    void doesNotAddAnnotationToMethod() {
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
                    
                          public void doNothing() {
                            System.out.println("Nothing happens here!");
                          }
                        }
                    """
            )
        );
    }

    @Test
    void doesNotChangeOtherClasses() {
        rewriteRun(
            java(
                """
                        package com.etrieu00.demo;
                    
                        class Bash {
                        }
                    """
            )
        );
    }
}
