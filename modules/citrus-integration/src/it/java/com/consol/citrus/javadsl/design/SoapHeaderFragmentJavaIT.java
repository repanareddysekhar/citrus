/*
 * Copyright 2006-2013 the original author or authors.
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

package com.consol.citrus.javadsl.design;

import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import com.consol.citrus.annotations.CitrusTest;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 */
@Test
public class SoapHeaderFragmentJavaIT extends TestNGCitrusTestDesigner {
    
    @CitrusTest
    public void soapHeaderFragment() {
        variable("correlationId", "citrus:randomNumber(10)");      
        variable("messageId", "citrus:randomNumber(10)");
        variable("user", "Christoph");
        
        soap().client("webServiceClient")
            .send()
            .payload("<ns0:HelloRequest xmlns:ns0=\"http://www.consol.de/schemas/samples/sayHello.xsd\">" +
                          "<ns0:MessageId>${messageId}</ns0:MessageId>" +
                          "<ns0:CorrelationId>${correlationId}</ns0:CorrelationId>" +
                          "<ns0:User>${user}</ns0:User>" +
                          "<ns0:Text>Hello WebServer</ns0:Text>" +
                      "</ns0:HelloRequest>")
            .header("{http://citrusframework.org/test}Operation", "sayHello")
            .header("citrus_http_operation", "sayHello")
            .soapAction("sayHello")
            .header("<ns0:HelloHeader xmlns:ns0=\"http://www.consol.de/schemas/samples/sayHello.xsd\">" +
                        "<ns0:MessageId>${messageId}</ns0:MessageId>" +
                        "<ns0:CorrelationId>${correlationId}</ns0:CorrelationId>" +
                        "<ns0:User>${user}</ns0:User>" +
                        "<ns0:Text>Hello WebServer</ns0:Text>" +
                    "</ns0:HelloHeader>")
            .fork(true);
        
        soap().server("webServiceRequestReceiver")
            .receive()
            .payload("<ns0:HelloRequest xmlns:ns0=\"http://www.consol.de/schemas/samples/sayHello.xsd\">" +
                          "<ns0:MessageId>${messageId}</ns0:MessageId>" +
                          "<ns0:CorrelationId>${correlationId}</ns0:CorrelationId>" +
                          "<ns0:User>${user}</ns0:User>" +
                          "<ns0:Text>Hello WebServer</ns0:Text>" +
                      "</ns0:HelloRequest>")
            .header("Operation", "sayHello")
            .header("operation", "sayHello")
            .soapAction("sayHello")
            .header("<SOAP-ENV:Header xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                          "<Operation xmlns=\"http://citrusframework.org/test\">sayHello</Operation>" +
                          "<ns0:HelloHeader xmlns:ns0=\"http://www.consol.de/schemas/samples/sayHello.xsd\">" +
                              "<ns0:MessageId>${messageId}</ns0:MessageId>" +
                              "<ns0:CorrelationId>${correlationId}</ns0:CorrelationId>" +
                              "<ns0:User>${user}</ns0:User>" +
                              "<ns0:Text>Hello WebServer</ns0:Text>" +
                          "</ns0:HelloHeader>" +
                      "</SOAP-ENV:Header>")
            .schemaValidation(false)
            .extractFromHeader("citrus_jms_messageId", "internal_correlation_id");
            
        soap().server("webServiceResponseSender")
            .send()
            .payload("<ns0:HelloResponse xmlns:ns0=\"http://www.consol.de/schemas/samples/sayHello.xsd\">" +
                            "<ns0:MessageId>${messageId}</ns0:MessageId>" +
                            "<ns0:CorrelationId>${correlationId}</ns0:CorrelationId>" +
                            "<ns0:User>WebServer</ns0:User>" +
                            "<ns0:Text>Hello ${user}</ns0:Text>" +
                        "</ns0:HelloResponse>")
            .header("<ns0:HelloHeader xmlns:ns0=\"http://www.consol.de/schemas/samples/sayHello.xsd\">" +
                              "<ns0:MessageId>${messageId}</ns0:MessageId>" +
                              "<ns0:CorrelationId>${correlationId}</ns0:CorrelationId>" +
                              "<ns0:User>${user}</ns0:User>" +
                              "<ns0:Text>Hello WebServer</ns0:Text>" +
                          "</ns0:HelloHeader>")
            .header("{http://citrusframework.org/test}Operation", "answerHello")
            .header("citrus_http_operation", "answerHello")
            .header("citrus_jms_correlationId", "${internal_correlation_id}");
        
        soap().client("webServiceClient")
            .receive()
            .payload("<ns0:HelloResponse xmlns:ns0=\"http://www.consol.de/schemas/samples/sayHello.xsd\">" +
                            "<ns0:MessageId>${messageId}</ns0:MessageId>" +
                            "<ns0:CorrelationId>${correlationId}</ns0:CorrelationId>" +
                            "<ns0:User>WebServer</ns0:User>" +
                            "<ns0:Text>Hello ${user}</ns0:Text>" +
                        "</ns0:HelloResponse>")
            .header("<SOAP-ENV:Header xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                              "<Operation xmlns=\"http://citrusframework.org/test\">@ignore@</Operation>" +
                              "<ns0:HelloHeader xmlns:ns0=\"http://www.consol.de/schemas/samples/sayHello.xsd\">" +
                                  "<ns0:MessageId>${messageId}</ns0:MessageId>" +
                                  "<ns0:CorrelationId>${correlationId}</ns0:CorrelationId>" +
                                  "<ns0:User>${user}</ns0:User>" +
                                  "<ns0:Text>Hello WebServer</ns0:Text>" +
                              "</ns0:HelloHeader>" +
                          "</SOAP-ENV:Header>")
            .header("Operation", "answerHello")
            .header("operation", "answerHello")
            .schemaValidation(false)
            .timeout(5000L);
    }
}