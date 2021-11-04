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

import static org.junit.Assert.assertEquals;
import static org.teavm.model.builder.ProgramBuilder.build;
import org.teavm.ast.Statement;
import org.teavm.ast.decompilation.NewDecompiler;
import org.teavm.ast.util.AstPrinter;
import org.teavm.model.MethodReference;
import org.teavm.model.Program;
import org.teavm.model.text.ListingBuilder;

public class BaseDecompilerTest {
    protected static final MethodReference PRINT = new MethodReference(BaseDecompilerTest.class,
            "print", void.class);
    protected static final MethodReference PRINT_2 = new MethodReference(BaseDecompilerTest.class,
            "print2", void.class);
    protected static final MethodReference PRINT_3 = new MethodReference(BaseDecompilerTest.class,
            "print3", void.class);
    protected static final MethodReference PRINT_4 = new MethodReference(BaseDecompilerTest.class,
            "print4", void.class);
    protected static final MethodReference PRINT_NUM = new MethodReference(BaseDecompilerTest.class,
            "print", int.class, void.class);
    protected static final MethodReference SUPPLY_INT_1 = new MethodReference(BaseDecompilerTest.class,
            "supplyInt1", int.class);
    protected static final MethodReference SUPPLY_INT_2 = new MethodReference(ExpressionDecompilerTest.class,
            "supplyInt2", int.class);
    protected AstPrinter astPrinter = new AstPrinter();
    protected ListingBuilder listingBuilder = new ListingBuilder();
    protected NewDecompiler decompiler = new NewDecompiler();
    protected Program program;
    protected Statement statement;

    protected void decompile(Runnable r) {
        program = build(r);
        statement = decompiler.decompile(program);
    }

    protected void expect(Statement statement) {
        assertEquals(
                "Wrong result for program:\n" + listingBuilder.buildListing(program, "  "),
                astPrinter.print(statement),
                astPrinter.print(this.statement)
        );
    }
}
