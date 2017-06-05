/**
 * Copyright 2016 Nikita Koksharov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.redisson.client.protocol.decoder;

import java.io.IOException;
import java.util.List;

import org.redisson.api.GeoPosition;
import org.redisson.client.codec.DoubleCodec;
import org.redisson.client.handler.State;

import io.netty.buffer.ByteBuf;

public class GeoPositionDecoder implements MultiDecoder<GeoPosition> {

    @Override
    public Double decode(ByteBuf buf, State state) throws IOException {
        return (Double) DoubleCodec.INSTANCE.getValueDecoder().decode(buf, state);
    }

    @Override
    public boolean isApplicable(int paramNum, State state) {
        return true;
    }

    @Override
    public GeoPosition decode(List<Object> parts, State state) {
        if (parts.isEmpty()) {
            return null;
        }

        Double longitude = Double.valueOf(parts.get(0).toString());
        Double latitude = Double.valueOf(parts.get(1).toString());
        return new GeoPosition(longitude, latitude);
    }

}
