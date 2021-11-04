/*
 *  Copyright 2021 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.ast.decompilation.test;

import static org.teavm.ast.Expr.addInt;
import static org.teavm.ast.Expr.constant;
import static org.teavm.ast.Expr.divInt;
import static org.teavm.ast.Expr.invokeStatic;
import static org.teavm.ast.Expr.var;
import static org.teavm.ast.Statement.assign;
import static org.teavm.ast.Statement.exitFunction;
import static org.teavm.ast.Statement.sequence;
import static org.teavm.ast.Statement.statementExpr;
import static org.teavm.model.builder.ProgramBuilder.exit;
import static org.teavm.model.builder.ProgramBuilder.intNum;
import static org.teavm.model.builder.ProgramBuilder.invokeStaticMethod;
import static org.teavm.model.builder.ProgramBuilder.set;
import static org.teavm.model.builder.ProgramBuilder.var;
import org.junit.Test;

public class ExpressionDecompilerTest extends BaseDecompilerTest {
    @Test
    public void simple() {
        decompile(() -> {
            set(var("a")).constant(23);
            exit(var("a"));
        });
        expect(exitFunction(constant(23)));
    }

    @Test
    public void expression() {
        decompile(() -> {
            set(var("a")).constant(2);
            set(var("b")).constant(3);
            set(var("c")).add(intNum(), var("a"), var("b"));
            exit(var("c"));
        });
        expect(exitFunction(addInt(constant(2), constant(3))));
    }

    @Test
    public void complexExpression() {
        decompile(() -> {
            set(var("a")).constant(2);
            set(var("b")).constant(3);
            set(var("c")).constant(4);
            set(var("d")).add(intNum(), var("a"), var("b"));
            set(var("e")).add(intNum(), var("d"), var("c"));
            exit(var("e"));
        });
        expect(exitFunction(
                addInt(
                    addInt(constant(2), constant(3)),
                    constant(4)
                )
        ));
    }

    @Test
    public void sharedNonConstant() {
        decompile(() -> {
            set(var("a")).constant(2);
            set(var("b")).constant(3);
            set(var("c")).add(intNum(), var("a"), var("b"));
            set(var("d")).add(intNum(), var("c"), var("c"));
            exit(var("d"));
        });
        expect(sequence(
                assign(var(2), addInt(constant(2), constant(3))),
                exitFunction(addInt(var(2), var(2)))
        ));
    }

    @Test
    public void sharedConstant() {
        decompile(() -> {
            set(var("a")).constant(2);
            set(var("b")).add(intNum(), var("a"), var("a"));
            exit(var("b"));
        });
        expect(exitFunction(addInt(constant(2), constant(2))));
    }

    @Test
    public void relocatableOperationWithBarrier() {
        decompile(() -> {
            set(var("a")).constant(2);
            set(var("b")).constant(3);
            set(var("c")).add(intNum(), var("a"), var("b"));
            invokeStaticMethod(PRINT);
            exit(var("c"));
        });
        expect(sequence(
                statementExpr(invokeStatic(PRINT)),
                exitFunction(addInt(constant(2), constant(3)))
        ));
    }

    @Test
    public void nonRelocatableOperationWithBarrier() {
        decompile(() -> {
            set(var("a")).constant(2);
            set(var("b")).constant(3);
            set(var("c")).div(intNum(), var("a"), var("b"));
            invokeStaticMethod(PRINT);
            exit(var("c"));
        });
        expect(sequence(
                assign(var(2), divInt(constant(2), constant(3))),
                statementExpr(invokeStatic(PRINT)),
                exitFunction(var(2))
        ));
    }

    @Test
    public void properOrderOfArguments() {
        decompile(() -> {
            set(var("a")).invokeStatic(SUPPLY_INT_1);
            set(var("b")).invokeStatic(SUPPLY_INT_2);
            set(var("c")).add(intNum(), var("a"), var("b"));
            exit(var("c"));
        });
        expect(exitFunction(addInt(invokeStatic(SUPPLY_INT_1), invokeStatic(SUPPLY_INT_2))));
    }

    @Test
    public void wrongOrderOfArguments() {
        decompile(() -> {
            set(var("a")).invokeStatic(SUPPLY_INT_1);
            set(var("b")).invokeStatic(SUPPLY_INT_2);
            set(var("c")).add(intNum(), var("b"), var("a"));
            exit(var("c"));
        });
        expect(sequence(
                assign(var(0), invokeStatic(SUPPLY_INT_1)),
                exitFunction(addInt(invokeStatic(SUPPLY_INT_2), var(0)))
        ));
    }
}
