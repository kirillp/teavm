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

import static org.teavm.ast.Expr.addInt;
import static org.teavm.ast.Expr.constant;
import static org.teavm.ast.Expr.invokeStatic;
import static org.teavm.ast.Expr.less;
import static org.teavm.ast.Statement.assign;
import static org.teavm.ast.Statement.loopWhile;
import static org.teavm.ast.Statement.sequence;
import static org.teavm.ast.Statement.statementExpr;
import static org.teavm.model.builder.ProgramBuilder.exit;
import static org.teavm.model.builder.ProgramBuilder.ifLessThanZero;
import static org.teavm.model.builder.ProgramBuilder.intNum;
import static org.teavm.model.builder.ProgramBuilder.invokeStaticMethod;
import static org.teavm.model.builder.ProgramBuilder.jump;
import static org.teavm.model.builder.ProgramBuilder.label;
import static org.teavm.model.builder.ProgramBuilder.put;
import static org.teavm.model.builder.ProgramBuilder.set;
import static org.teavm.model.builder.ProgramBuilder.var;
import java.util.Arrays;
import org.junit.Test;
import org.teavm.ast.Expr;

public class LoopDecompilerTest extends BaseDecompilerTest {
    @Test
    public void loop() {
        decompile(() -> {
            set(var("i")).constant(0);
            set(var("n")).constant(10);
            jump(label("head"));

            put(label("head"));
            set(var("cmp")).sub(intNum(), var("i"), var("n"));
            ifLessThanZero(var("cmp"), label("body"), label("exit"));

            put(label("body"));
            invokeStaticMethod(PRINT_NUM, var("i"));
            set(var("step")).constant(1);
            set(var("i")).add(intNum(), var("i"), var("step"));
            jump(label("head"));

            put(label("exit"));
            invokeStaticMethod(PRINT);
            exit();
        });

        expect(sequence(
                assign(Expr.var(0), constant(0)),
                loopWhile(less(Expr.var(0), constant(10)), loop -> Arrays.asList(
                        statementExpr(invokeStatic(PRINT_NUM, Expr.var(0))),
                        assign(Expr.var(0), addInt(Expr.var(0), constant(1)))
                )),
                statementExpr(invokeStatic(PRINT))
        ));
    }
}
