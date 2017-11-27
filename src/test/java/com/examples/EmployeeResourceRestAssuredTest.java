package com.examples;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import javax.ws.rs.core.MediaType;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EmployeeResourceRestAssuredTest {

	private HttpServer server;

	@Before
	public void setUp() throws Exception {
		server = Main.startServer();
	}

	@After
	public void tearDown() throws Exception {
		server.shutdownNow();
	}

	@Test
	public void testGetItXML() {
		given().
			accept(MediaType.APPLICATION_XML).
		when().
			get(Main.BASE_URI + "employee").
		then().
			statusCode(200).
			assertThat().
				body(
					"employee.id", equalTo("E1"),
					"employee.name", equalTo("An employee"),
					"employee.salary", equalTo("1000")
				);
	}

}
