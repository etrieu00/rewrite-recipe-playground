package com.etrieu00.recipe;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.jspecify.annotations.Nullable;
import org.openrewrite.*;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.TreeVisitingPrinter;
import org.openrewrite.java.tree.J;

import java.util.Optional;

@Value
@EqualsAndHashCode(callSuper = false)
public class RecipeTreePrinter extends Recipe {

    String displayName = "Tree printing recipe";
    String description = "A recipe for printing out a tree.";

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<>() {
            @Override
            public @Nullable J visit(final @Nullable Tree tree, final ExecutionContext context, final Cursor parent) {
                Optional.ofNullable(tree)
                    .map(TreeVisitingPrinter::printTree)
                    .ifPresent(System.out::println);
                return super.visit(tree, context, parent);
            }
        };
    }
}
