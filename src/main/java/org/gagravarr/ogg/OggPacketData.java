/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gagravarr.ogg;

/**
 * The data part of an {@link OggPacket}.
 * RFC3533 suggests that these should usually be
 *  around 50-200 bytes long.
 * Normally wrapped as a full {@link OggPacket},
 *  but may be used internally when a
 *  Packet is split across more than one
 *  {@link OggPage}.
 */
public class OggPacketData {
    private byte[] data;

    protected OggPacketData(byte[] data) {
        this.data = data;
    }

    /**
     * Returns the data that makes up the packet.
     */
    public byte[] getData() {
        return data;
    }
}
