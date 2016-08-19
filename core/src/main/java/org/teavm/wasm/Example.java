/*
 *  Copyright 2016 Alexey Andreev.
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
package org.teavm.wasm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Example {
    private Example() {
    }

    public static void main(String[] args) {
        int a = 0;
        int b = 1;
        for (int i = 0; i < 30; ++i) {
            int c = a + b;
            a = b;
            b = c;
            WasmRuntime.print(a);
        }
        WasmRuntime.print(new A(2).getValue() + new A(3).getValue());

        for (int i = 0; i < 4; ++i) {
            WasmRuntime.print(instance(i).foo());
        }

        Base[] array = { new Derived1(), new Derived2() };
        WasmRuntime.print(array.length);
        for (Base elem : array) {
            WasmRuntime.print(elem.foo());
        }

        WasmRuntime.print(new Derived2() instanceof Base ? 1 : 0);
        WasmRuntime.print(new Derived3() instanceof Base ? 1 : 0);
        WasmRuntime.print((Object) new Derived2() instanceof Derived1 ? 1 : 0);
        WasmRuntime.print((Object) new Derived2() instanceof A ? 1 : 0);
        WasmRuntime.print(new A(23) instanceof Base ? 1 : 0);

        byte[] bytes = { 5, 6, 10, 15 };
        for (byte bt : bytes) {
            WasmRuntime.print(bt);
        }

        String str = "foobar";
        WasmRuntime.print(str.length());
        for (int i = 0; i < str.length(); ++i) {
            WasmRuntime.print(str.charAt(i));
        }

        Initialized.foo();
        Initialized.foo();
        Initialized.foo();

        Object o = new Object();
        WasmRuntime.print(o.hashCode());
        WasmRuntime.print(o.hashCode());
        WasmRuntime.print(new Object().hashCode());
        WasmRuntime.print(new Object().hashCode());

        /*List<Integer> list = new ArrayList<>(Arrays.asList(333, 444, 555));
        list.add(1234);
        list.remove(444);

        for (int item : list) {
            WasmRuntime.print(item);
        }*/
    }

    private static Base instance(int index) {
        switch (index) {
            case 0:
                return new Derived1();
            case 1:
                return new Derived2();
            case 2:
                return new Derived3();
            default:
                return new Derived4();
        }
    }

    private static class A {
        private int value;

        public A(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    interface Base {
        int foo();
    }

    static class Derived1 implements Base {
        @Override
        public int foo() {
            return 234;
        }
    }

    static class Derived2 implements Base {
        @Override
        public int foo() {
            return 345;
        }
    }

    static class Derived3 extends Derived2 {
    }

    static class Derived4 extends Derived1 {
        @Override
        public int foo() {
            return 123;
        }
    }

    static class Initialized {
        static {
            WasmRuntime.print(9999);
        }

        public static void foo() {
            WasmRuntime.print(8888);
        }
    }
}