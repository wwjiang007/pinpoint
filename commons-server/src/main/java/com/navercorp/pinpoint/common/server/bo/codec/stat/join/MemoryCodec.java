/*
 * Copyright 2017 NAVER Corp.
 *
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
package com.navercorp.pinpoint.common.server.bo.codec.stat.join;

import com.navercorp.pinpoint.common.buffer.Buffer;
import com.navercorp.pinpoint.common.server.bo.codec.stat.AgentStatDataPointCodec;
import com.navercorp.pinpoint.common.server.bo.codec.stat.ApplicationStatCodec;
import com.navercorp.pinpoint.common.server.bo.codec.stat.header.AgentStatHeaderDecoder;
import com.navercorp.pinpoint.common.server.bo.codec.stat.header.AgentStatHeaderEncoder;
import com.navercorp.pinpoint.common.server.bo.codec.stat.header.BitCountingHeaderDecoder;
import com.navercorp.pinpoint.common.server.bo.codec.stat.header.BitCountingHeaderEncoder;
import com.navercorp.pinpoint.common.server.bo.codec.stat.strategy.JoinLongFieldEncodingStrategy;
import com.navercorp.pinpoint.common.server.bo.codec.stat.strategy.JoinLongFieldStrategyAnalyzer;
import com.navercorp.pinpoint.common.server.bo.serializer.stat.ApplicationStatDecodingContext;
import com.navercorp.pinpoint.common.server.bo.stat.join.JoinLongFieldBo;
import com.navercorp.pinpoint.common.server.bo.stat.join.JoinMemoryBo;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author minwoo.jung
 */
@Component
public class MemoryCodec implements ApplicationStatCodec<JoinMemoryBo> {
    private static final byte VERSION = 1;

    private final AgentStatDataPointCodec codec;

    public MemoryCodec(AgentStatDataPointCodec codec) {
        this.codec = Objects.requireNonNull(codec, "codec");
    }

    @Override
    public byte getVersion() {
        return VERSION;
    }

    public void encodeValues(Buffer valueBuffer, List<JoinMemoryBo> joinMemoryBoList) {
        Assert.notEmpty(joinMemoryBoList, "joinMemoryBoList");

        final int numValues = joinMemoryBoList.size();
        valueBuffer.putVInt(numValues);
        List<Long> timestamps = new ArrayList<>(numValues);
        JoinLongFieldStrategyAnalyzer.Builder heapUsedAnalyzerBuilder = new JoinLongFieldStrategyAnalyzer.Builder();
        JoinLongFieldStrategyAnalyzer.Builder nonHeapUsedAnalyzerBuilder = new JoinLongFieldStrategyAnalyzer.Builder();

        for (JoinMemoryBo joinMemoryBo : joinMemoryBoList) {
            timestamps.add(joinMemoryBo.getTimestamp());
            heapUsedAnalyzerBuilder.addValue(joinMemoryBo.getHeapUsedJoinValue());
            nonHeapUsedAnalyzerBuilder.addValue(joinMemoryBo.getNonHeapUsedJoinValue());
        }

        codec.encodeTimestamps(valueBuffer, timestamps);
        encodeDataPoints(valueBuffer, heapUsedAnalyzerBuilder.build(), nonHeapUsedAnalyzerBuilder.build());
    }

    private void encodeDataPoints(Buffer valueBuffer,
                                  JoinLongFieldStrategyAnalyzer heapUsedStrategyAnalyzer,
                                  JoinLongFieldStrategyAnalyzer nonHeapUsedStrategyAnalyzer) {
        // encode header
        AgentStatHeaderEncoder headerEncoder = new BitCountingHeaderEncoder();
        byte[] codes = heapUsedStrategyAnalyzer.getBestStrategy().getCodes();
        for (byte code : codes) {
            headerEncoder.addCode(code);
        }
        codes = nonHeapUsedStrategyAnalyzer.getBestStrategy().getCodes();
        for (byte code : codes) {
            headerEncoder.addCode(code);
        }

        final byte[] header = headerEncoder.getHeader();
        valueBuffer.putPrefixedBytes(header);
        // encode values
        this.codec.encodeValues(valueBuffer, heapUsedStrategyAnalyzer.getBestStrategy(), heapUsedStrategyAnalyzer.getValues());
        this.codec.encodeValues(valueBuffer, nonHeapUsedStrategyAnalyzer.getBestStrategy(), nonHeapUsedStrategyAnalyzer.getValues());
    }

    @Override
    public List<JoinMemoryBo> decodeValues(Buffer valueBuffer, ApplicationStatDecodingContext decodingContext) {
        final String id = decodingContext.getApplicationId();
        final long baseTimestamp = decodingContext.getBaseTimestamp();
        final long timestampDelta = decodingContext.getTimestampDelta();
        final long initialTimestamp = baseTimestamp + timestampDelta;

        int numValues = valueBuffer.readVInt();
        List<Long> timestamps = this.codec.decodeTimestamps(initialTimestamp, valueBuffer, numValues);

        // decode headers
        final byte[] header = valueBuffer.readPrefixedBytes();
        AgentStatHeaderDecoder headerDecoder = new BitCountingHeaderDecoder(header);
        JoinLongFieldEncodingStrategy heapUsedEncodingStrategy = JoinLongFieldEncodingStrategy.getFromCode(headerDecoder.getCode(), headerDecoder.getCode(), headerDecoder.getCode(), headerDecoder.getCode(), headerDecoder.getCode());
        JoinLongFieldEncodingStrategy nonHeapUsedEncodingStrategy = JoinLongFieldEncodingStrategy.getFromCode(headerDecoder.getCode(), headerDecoder.getCode(), headerDecoder.getCode(), headerDecoder.getCode(), headerDecoder.getCode());

        // decode values
        final List<JoinLongFieldBo> heapUsedList = this.codec.decodeValues(valueBuffer, heapUsedEncodingStrategy, numValues);
        final List<JoinLongFieldBo> nonHeapUsedList = this.codec.decodeValues(valueBuffer, nonHeapUsedEncodingStrategy, numValues);

        List<JoinMemoryBo> joinMemoryBoList = new ArrayList<>(numValues);
        for (int i = 0; i < numValues; i++) {
            JoinMemoryBo joinMemoryBo = new JoinMemoryBo();
            joinMemoryBo.setId(id);
            joinMemoryBo.setTimestamp(timestamps.get(i));
            joinMemoryBo.setHeapUsedJoinValue(heapUsedList.get(i));
            joinMemoryBo.setNonHeapUsedJoinValue(nonHeapUsedList.get(i));
            joinMemoryBoList.add(joinMemoryBo);
        }

        return joinMemoryBoList;
    }
}
