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

import static java.util.Arrays.asList;
import static org.teavm.ast.Expr.breakStatement;
import static org.teavm.ast.Expr.invokeStatic;
import static org.teavm.ast.Statement.choose;
import static org.teavm.ast.Statement.sequence;
import static org.teavm.ast.Statement.statementExpr;
import static org.teavm.model.builder.ProgramBuilder.exit;
import static org.teavm.model.builder.ProgramBuilder.invokeStaticMethod;
import static org.teavm.model.builder.ProgramBuilder.jump;
import static org.teavm.model.builder.ProgramBuilder.jumpTable;
import static org.teavm.model.builder.ProgramBuilder.jumpTableEntry;
import static org.teavm.model.builder.ProgramBuilder.label;
import static org.teavm.model.builder.ProgramBuilder.put;
import static org.teavm.model.builder.ProgramBuilder.set;
import static org.teavm.model.builder.ProgramBuilder.var;
import org.junit.Test;

public class SwitchDecompilerTest extends BaseDecompilerTest {
    @Test
    public void simpleSwitch() {
        decompile(() -> {
            set(var("a")).invokeStatic(SUPPLY_INT_1);
            jumpTable(var("a"), asList(
                    jumpTableEntry(1, label("first")),
                    jumpTableEntry(2, label("second"))
            ), label("third"));

            put(label("first"));
            invokeStaticMethod(PRINT);
            jump(label("join"));

            put(label("second"));
            invokeStaticMethod(PRINT_2);
            jump(label("join"));

            put(label("third"));
            invokeStaticMethod(PRINT_3);
            jump(label("join"));

            put(label("join"));
            invokeStaticMethod(PRINT_4);
            exit();
        });

        expect(sequence(
                choose(invokeStatic(SUPPLY_INT_1), b -> {
                    b.clause(1, asList(
                            statementExpr(invokeStatic(PRINT)),
                            breakStatement(b.target())
                    ));
                    b.clause(2, asList(
                            statementExpr(invokeStatic(PRINT_2)),
                            breakStatement(b.target())
                    ));
                    b.defaultClause(asList(
                            statementExpr(invokeStatic(PRINT_3))
                    ));
                }),
                statementExpr(invokeStatic(PRINT_4))
        ));
    }
}
