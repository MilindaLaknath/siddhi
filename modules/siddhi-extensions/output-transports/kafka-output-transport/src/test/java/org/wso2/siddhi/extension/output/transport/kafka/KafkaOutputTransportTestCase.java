/*
 * Copyright (c)  2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.siddhi.extension.output.transport.kafka;

import org.I0Itec.zkclient.exception.ZkTimeoutException;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.wso2.siddhi.core.ExecutionPlanRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.stream.output.StreamCallback;
import org.wso2.siddhi.core.util.EventPrinter;
import org.wso2.siddhi.extension.output.mapper.text.TextOutputMapper;
import org.wso2.siddhi.query.api.ExecutionPlan;
import org.wso2.siddhi.query.api.annotation.Annotation;
import org.wso2.siddhi.query.api.definition.Attribute;
import org.wso2.siddhi.query.api.definition.StreamDefinition;
import org.wso2.siddhi.query.api.execution.query.Query;
import org.wso2.siddhi.query.api.execution.query.input.stream.InputStream;
import org.wso2.siddhi.query.api.execution.query.selection.Selector;
import org.wso2.siddhi.query.api.expression.Variable;

public class KafkaOutputTransportTestCase {
    static final Logger log = Logger.getLogger(KafkaOutputTransportTestCase.class);

    @Test
    public void testPublisherWithKafkaTransport() throws InterruptedException {
        try {
            SiddhiManager siddhiManager = new SiddhiManager();
            siddhiManager.setExtension("outputmapper:text", TextOutputMapper.class);
            ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(
                    "@Plan:name('TestExecutionPlan') " +
                    "define stream FooStream (symbol string, price float, volume long); " +
                    "@info(name = 'query1') " +
                        "@sink(type='kafka', topic='kafka_topic', bootstrap.servers='localhost:9092', partition.no='0', " +
                              "@map(type='text'))" +
                                    "Define stream BarStream (symbol string, price float, volume long);" +
                    "from FooStream select symbol, price, volume insert into BarStream;");

            InputHandler fooStream = executionPlanRuntime.getInputHandler("FooStream");
            executionPlanRuntime.start();
            fooStream.send(new Object[]{"WSO2", 55.6f, 100L});
            fooStream.send(new Object[]{"IBM", 75.6f, 100L});
            fooStream.send(new Object[]{"WSO2", 57.6f, 100L});
            Thread.sleep(10000);
            executionPlanRuntime.shutdown();
        } catch (ZkTimeoutException ex) {
            log.warn("No zookeeper may not be available.", ex);
        }
    }
}
