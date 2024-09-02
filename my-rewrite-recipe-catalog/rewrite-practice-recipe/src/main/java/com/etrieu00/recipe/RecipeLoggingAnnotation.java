package com.etrieu00.recipe;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Preconditions;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.search.FindAnnotations;
import org.openrewrite.java.tree.J.Annotation;
import org.openrewrite.java.tree.J.MethodDeclaration;

import java.util.List;
import java.util.Optional;

import static java.util.Comparator.comparing;

@Value
@EqualsAndHashCode(callSuper = false)
public class RecipeLoggingAnnotation extends Recipe {

    private static final List<String> matches = List.of(
        "@org.springframework.web.bind.annotation.GetMapping",
        "@org.springframework.web.bind.annotation.PutMapping",
        "@org.springframework.web.bind.annotation.DeleteMapping",
        "@org.springframework.web.bind.annotation.PatchMapping",
        "@org.springframework.web.bind.annotation.PostMapping"
    );

    String displayName = "Add custom Endpoint Audit annotation";
    String description = "Programmatically adds a new custom annotation to any endpoint that are exposed to the web.";

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return Preconditions.check(new FindAnnotations("@org.springframework.web.bind.annotation.RestController", true), new JavaIsoVisitor<>() {
            @Override
            public MethodDeclaration visitMethodDeclaration(MethodDeclaration method, ExecutionContext context) {
                maybeAddImport("com.etrieu00.logging.annotation.AuditEndpoint");
                return Optional.of(method)
                    .filter(m -> matches.stream().anyMatch(matcher -> !FindAnnotations.find(m, matcher, false).isEmpty()))
                    .filter(m -> FindAnnotations.find(m, "@com.etrieu00.logging.annotation.AuditEndpoint", true).isEmpty())
                    .map(m -> JavaTemplate
                        .builder("@AuditEndpoint")
                        .javaParser(JavaParser
                            .fromJavaVersion()
                            .classpathFromResources(context, "logging-annotation-core"))
                        .imports("com.etrieu00.logging.annotation.AuditEndpoint")
                        .build()
                        .apply(getCursor(), m.getCoordinates().addAnnotation(comparing(Annotation::getSimpleName))))
                    .map(MethodDeclaration.class::cast)
                    .orElse(method);
            }
        });
    }
}
