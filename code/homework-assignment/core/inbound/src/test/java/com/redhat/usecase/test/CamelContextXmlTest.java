package com.redhat.usecase.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsEndpoint;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.camel.util.KeyValueHolder;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.connection.JmsTransactionManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.customer.app.Person;
import com.customer.app.PersonName;
import com.customer.app.response.ESBResponse;

public class CamelContextXmlTest extends CamelSpringTestSupport {

	// TODO Create test message bodies that work for the route(s) being tested
	// Expected message bodies
	protected Object[] expectedBodies = {
			"<something id='1'>expectedBody1</something>",
			"<something id='2'>expectedBody2</something>" };
	// Templates to send to input endpoints
	@Produce(uri = "direct:integrateRoute")
	protected ProducerTemplate inputEndpoint;
	@EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;
	BrokerService broker = null;

	@Before
	public void initialize() throws Exception {
		broker = new BrokerService();
		TransportConnector connector = new TransportConnector();
		connector.setUri(new URI("tcp://localhost:61617"));
		broker.addConnector(connector);
		broker.start();
		broker.waitUntilStarted();
		KeyValueHolder serviceHolder = new KeyValueHolder(new ActiveMQComponent(), null);
	}

	@Test
	public void testCamelRoute() throws Throwable {
		String expectedBody = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n  <Person xmlns=\"http://www.app.customer.com\"/>\n";
		 
        getMockEndpoint("mock:deim-queue").expectedBodiesReceived(expectedBody);
 
        inputEndpoint.sendBody(new Person());
 
        assertMockEndpointsSatisfied();
	}
	
	@Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
            	// We will extend the route of the error queue to add a mock endpoint
                try {
					context.getRouteDefinition("handleRest").adviceWith(context, new AdviceWithRouteBuilder() {
					    @Override public void configure() throws Exception {
					        weaveById("inbound-queue").replace().to("mock:deim-queue");
					    }
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        };
    }

	@After
	public void destroy() {
		try {
			if (broker != null)
				broker.stop();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected ClassPathXmlApplicationContext createApplicationContext() {
		ClassPathXmlApplicationContext classPathXmlApplicationContext = null;

		classPathXmlApplicationContext = new ClassPathXmlApplicationContext(
				new String[] { "bundleContext.xml", "camelTestContext.xml" });

		return classPathXmlApplicationContext;
	}

	public static String replaceOSGiPropertyLoader(String springXmlLocation)
			throws ParserConfigurationException, IOException, SAXException,
			XPathExpressionException, TransformerException {
		CamelContext context;
		// TODO fix it
		return null;
	}

}
