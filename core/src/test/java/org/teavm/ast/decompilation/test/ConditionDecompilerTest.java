/*
 *  Copyright 2022 Alexey Andreev.
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

import static org.teavm.ast.Expr.and;
import static org.teavm.ast.Expr.constant;
import static org.teavm.ast.Expr.invokeStatic;
import static org.teavm.ast.Expr.less;
import static org.teavm.ast.Expr.or;
import static org.teavm.ast.Statement.block;
import static org.teavm.ast.Statement.cond;
import static org.teavm.ast.Statement.exitBlock;
import static org.teavm.ast.Statement.sequence;
import static org.teavm.ast.Statement.statementExpr;
import static org.teavm.model.builder.ProgramBuilder.exit;
import static org.teavm.model.builder.ProgramBuilder.ifLessThanZero;
import static org.teavm.model.builder.ProgramBuilder.invokeStaticMethod;
import static org.teavm.model.builder.ProgramBuilder.jump;
import static org.teavm.model.builder.ProgramBuilder.label;
import static org.teavm.model.builder.ProgramBuilder.put;
import static org.teavm.model.builder.ProgramBuilder.set;
import static org.teavm.model.builder.ProgramBuilder.var;
import java.util.Arrays;
import org.junit.Test;

public class ConditionDecompilerTest extends BaseDecompilerTest {

    @Test
    public void simpleCondition() {
        decompile(() -> {
            set(var("a")).constant(2);
            ifLessThanZero(var("a"), label("less"), label("greater"));

            put(label("less"));
            invokeStaticMethod(PRINT);
            jump(label("join"));

            put(label("greater"));
            invokeStaticMethod(PRINT_2);
            jump(label("join"));

            put(label("join"));
            exit();
        });

        expect(cond(
                less(constant(2), constant(0)),
                Arrays.asList(
                        statementExpr(invokeStatic(PRINT))
                ),
                Arrays.asList(
                        statementExpr(invokeStatic(PRINT_2))
                )
        ));
    }

    @Test
    public void simpleConditionWithOneBranch() {
        decompile(() -> {
            set(var("a")).constant(2);
            ifLessThanZero(var("a"), label("less"), label("join"));

            put(label("less"));
            invokeStaticMethod(PRINT);
            jump(label("join"));

            put(label("join"));
            exit();
        });

        expect(cond(
                less(constant(2), constant(0)),
                Arrays.asList(
                        statementExpr(invokeStatic(PRINT))
                )
        ));
    }

    @Test
    public void simpleConditionWithEachBranchReturning() {
        decompile(() -> {
            set(var("a")).constant(2);
            ifLessThanZero(var("a"), label("less"), label("greater"));

            put(label("less"));
            invokeStaticMethod(PRINT);
            exit();

            put(label("greater"));
            invokeStaticMethod(PRINT_2);
            exit();
        });

        expect(cond(
                less(constant(2), constant(0)),
                Arrays.asList(
                        statementExpr(invokeStatic(PRINT))
                ),
                Arrays.asList(
                        statementExpr(invokeStatic(PRINT_2))
                )
        ));
    }

    @Test
    public void shortCircuit() {
        decompile(() -> {
            set(var("a")).constant(2);
            ifLessThanZero(var("a"), label("next"), label("false"));

            put(label("next"));
            set(var("b")).constant(3);
            ifLessThanZero(var("b"), label("true"), label("false"));

            put(label("true"));
            invokeStaticMethod(PRINT);
            jump(label("joint"));

            put(label("false"));
            invokeStaticMethod(PRINT_2);
            jump(label("joint"));

            put(label("joint"));
            invokeStaticMethod(PRINT_3);
            exit();
        });

        expect(sequence(
                cond(
                        and(
                                less(constant(2), constant(0)),
                                less(constant(3), constant(0))
                        ),
                        Arrays.asList(
                                statementExpr(invokeStatic(PRINT))
                        ),
                        Arrays.asList(
                                statementExpr(invokeStatic(PRINT_2))
                        )
                ),
                statementExpr(invokeStatic(PRINT_3))
        ));
    }

    @Test
    public void shortCircuitFailure() {
        decompile(() -> {
            set(var("a")).constant(2);
            ifLessThanZero(var("a"), label("next"), label("false"));

            put(label("next"));
            invokeStaticMethod(PRINT_4);
            set(var("b")).constant(3);
            ifLessThanZero(var("b"), label("true"), label("false"));

            put(label("true"));
            invokeStaticMethod(PRINT);
            jump(label("joint"));

            put(label("false"));
            invokeStaticMethod(PRINT_2);
            jump(label("joint"));

            put(label("joint"));
            invokeStaticMethod(PRINT_3);
            exit();
        });

        expect(sequence(
                block(label -> Arrays.asList(
                        cond(
                                less(constant(2), constant(0)),
                                Arrays.asList(
                                        statementExpr(invokeStatic(PRINT_4)),
                                        cond(
                                                less(constant(3), constant(0)),
                                                Arrays.asList(
                                                        statementExpr(invokeStatic(PRINT)),
                                                        exitBlock(label)
                                                )
                                        )
                                )
                        ),
                        statementExpr(invokeStatic(PRINT_2))
                )),
                statementExpr(invokeStatic(PRINT_3))
        ));
    }

    @Test
    public void complexShortCircuit() {
        decompile(() -> {
            set(var("a")).constant(2);
            ifLessThanZero(var("a"), label("test_b"), label("test_c"));

            put(label("test_b"));
            set(var("b")).constant(3);
            ifLessThanZero(var("b"), label("true"), label("test_c"));

            put(label("test_c"));
            set(var("c")).constant(4);
            ifLessThanZero(var("c"), label("true"), label("false"));

            put(label("true"));
            invokeStaticMethod(PRINT);
            jump(label("joint"));

            put(label("false"));
            invokeStaticMethod(PRINT_2);
            jump(label("joint"));

            put(label("joint"));
            invokeStaticMethod(PRINT_3);
            exit();
        });

        expect(sequence(
                cond(
                        or(
                                and(
                                        less(constant(2), constant(0)),
                                        less(constant(3), constant(0))
                                ),
                                less(constant(4), constant(0))
                        ),
                        Arrays.asList(
                                statementExpr(invokeStatic(PRINT))
                        ),
                        Arrays.asList(
                                statementExpr(invokeStatic(PRINT_2))
                        )
                ),
                statementExpr(invokeStatic(PRINT_3))
        ));
    }
}
