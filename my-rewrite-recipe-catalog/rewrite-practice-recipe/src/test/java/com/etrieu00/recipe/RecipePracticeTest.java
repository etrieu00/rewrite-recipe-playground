package com.etrieu00.recipe;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

class RecipePracticeTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new RecipePractice("com.etrieu00.demo.FooBar"));
    }

    @Test
    void addsHelloToFooBar() {
        rewriteRun(
            java(
                """
                        package com.etrieu00.demo;
                    
                        class FooBar {
                        }
                    """,
                """
                        package com.etrieu00.demo;
                    
                        class FooBar {
                            public String hello() {
                                return "Hello from com.etrieu00.demo.FooBar!";
                            }
                        }
                    """
            )
        );
    }

    @Test
    void doesNotChangeExistingHello() {
        rewriteRun(
            java(
                """
                        package com.etrieu00.demo;
                    
                        class FooBar {
                            public String hello() { return ""; }
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