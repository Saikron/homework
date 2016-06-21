package com.redhat.usecase.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.cxf.jaxrs.impl.ResponseBuilderImpl;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.customer.app.Person;
import com.customer.app.response.ESBResponse;
import com.redhat.usecase.service.DEIMService;

public class DEIMServiceImpl implements DEIMService {

	@Produce(uri = "direct:integrateRoute")
	ProducerTemplate template;

	@Override
	@POST
	@Path("/add")
	@Consumes(MediaType.APPLICATION_XML)
	public Response addPerson(Person person) {
		// TODO fix it
		template.requestBody(person);
		System.out.println("========================= REST SUCCESS ============");
		Response response = new ResponseBuilderImpl().status(Status.OK).build();
		return response;
	}
	

}
