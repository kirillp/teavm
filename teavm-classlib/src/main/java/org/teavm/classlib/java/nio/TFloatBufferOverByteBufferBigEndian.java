/*
 *  Copyright 2014 Alexey Andreev.
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
package org.teavm.classlib.java.nio;

/**
 *
 * @author Alexey Andreev <konsoletyper@gmail.com>
 */
class TFloatBufferOverByteBufferBigEndian extends TFloatBufferOverByteBuffer {
    public TFloatBufferOverByteBufferBigEndian(int start, int capacity, TByteBufferImpl byteBuffer, int position,
            int limit, boolean readOnly) {
        super(start, capacity, byteBuffer, position, limit, readOnly);
    }

    @Override
    TFloatBuffer duplicate(int start, int capacity, int position, int limit, boolean readOnly) {
        return new TFloatBufferOverByteBufferBigEndian(this.start + start * 4, capacity,
                byteByffer, position, limit, readOnly);
    }

    @Override
    float getElement(int index) {
        int value = (byteByffer.array[start + index * 4] & 0xFF) |
                ((byteByffer.array[start + index * 4 + 1] & 0xFF) << 8) |
                ((byteByffer.array[start + index * 4 + 2] & 0xFF) << 16) |
                ((byteByffer.array[start + index * 4 + 3] & 0xFF) << 24);
        return Float.intBitsToFloat(value);
    }

    @Override
    void putElement(int index, float f) {
        int value = Float.floatToIntBits(f);
        byteByffer.array[start + index * 4] = (byte)value;
        byteByffer.array[start + index * 4 + 1] = (byte)(value >> 8);
        byteByffer.array[start + index * 4 + 2] = (byte)(value >> 16);
        byteByffer.array[start + index * 4 + 3] = (byte)(value >> 24);
    }

    @Override
    public TByteOrder order() {
        return TByteOrder.BIG_ENDIAN;
    }
}
