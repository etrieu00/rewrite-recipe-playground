package com.etrieu00.recipe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.*;
import org.openrewrite.NlsRewrite.Description;
import org.openrewrite.NlsRewrite.DisplayName;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.J.ClassDeclaration;

import java.util.Optional;

@Value
@EqualsAndHashCode(callSuper = false)
public class RecipePractice extends Recipe {

    @Option(displayName = "Fully qualified class name",
        description = "A fully qualified class name indicating which class to add a hello() method to.",
        example = "com.yourorg.FooBar")
    String fullyQualifiedClassName;

    @JsonCreator
    public RecipePractice(@JsonProperty("fullyQualifiedClassName") String fullyQualifiedClassName) {
        this.fullyQualifiedClassName = fullyQualifiedClassName;
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<>() {
            private final JavaTemplate template = JavaTemplate.builder("""
                public String hello() {
                  return "Hello from #{}!";
                }
                """).build();

            @Override
            public ClassDeclaration visitClassDeclaration(ClassDeclaration classDecl, ExecutionContext executionContext) {
                return Optional.of(classDecl)
                    .filter(clazz -> clazz.getType().getFullyQualifiedName().equals(fullyQualifiedClassName))
                    .filter(clazz -> clazz.getBody()
                        .getStatements()
                        .stream()
                        .filter(statement -> statement instanceof J.MethodDeclaration)
                        .map(J.MethodDeclaration.class::cast)
                        .noneMatch(methodDeclaration -> methodDeclaration.getName().getSimpleName().equals("hello")))
                    .map(clazz -> clazz.withBody(template.apply(new Cursor(getCursor(), clazz.getBody()), clazz.getBody().getCoordinates().lastStatement(),
                        fullyQualifiedClassName)))
                    .orElse(classDecl);
            }
        };
    }

    @Override
    public @DisplayName String getDisplayName() {
        return "Practice recipe";
    }

    @Override
    public @Description String getDescription() {
        return "This recipe is a practice recipe.";
    }
}
