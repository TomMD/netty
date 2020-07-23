/*
 * Copyright 2020 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.compression;

final class ZstdConstants {

    static final int ZSTD_NUMBER = 'Z' << 24 | 'S' << 16 | 'T' << 8  | 'D';

    static final int HEADER_LENGTH = 4 +  // ZSTD number
            1 +  // type
            4 +  // compressed length
            4 +  // decompressed length
            4;   // checksum

    /**
     * Default compress level
     */
    static final int DEFAULT_COMPRESS_LEVEL = 0;
    static final int TOKEN_OFFSET = 4;
    /**
     * Compressed length offset
     */
    static final int COMPRESSED_LENGTH_OFFSET = TOKEN_OFFSET + 1;
    /**
     * Decompressed length offset
     */
    static final int DECOMPRESSED_LENGTH_OFFSET = COMPRESSED_LENGTH_OFFSET + 4;
    static final int DEFAULT_CHECKSUM_OFFSET = DECOMPRESSED_LENGTH_OFFSET + 4;
    static final int COMPRESSION_LEVEL_BASE = 10;
    /**
     * Min block size
     */
    static final int MIN_BLOCK_SIZE = 64;
    /**
     * Max block size
     */
    static final int MAX_BLOCK_SIZE = 1 << COMPRESSION_LEVEL_BASE + 0x0F;   //  32 M
    /**
     * Default block size
     */
    static final int DEFAULT_BLOCK_SIZE = 1 << 16;  // 64 KB
    static final int BLOCK_TYPE_NON_COMPRESSED = 0x10;
    static final int BLOCK_TYPE_COMPRESSED = 0x20;

    private ZstdConstants() { }
}
