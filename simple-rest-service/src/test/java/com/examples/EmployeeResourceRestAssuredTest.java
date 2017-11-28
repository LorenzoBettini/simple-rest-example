package com.examples;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.examples.repository.EmployeeRepository;

public class EmployeeResourceRestAssuredTest {

	private static final String EMPLOYEES = "employees";
	private HttpServer server;

	@Before
	public void setUp() throws Exception {
		EmployeeRepository.instance.initialize();
		server = Main.startServer();
	}

	@After
	public void tearDown() throws Exception {
		server.shutdownNow();
	}

	@Test
	public void testGetAllEmployees() {
		given().
			accept(MediaType.APPLICATION_XML).
		when().
			get(Main.BASE_URI + EMPLOYEES).
		then().
			statusCode(200).
			assertThat().
			body(
				"employees.employee[0].id", equalTo("ID1"),
				"employees.employee[0].name", equalTo("First Employee"),
				"employees.employee[0].salary", equalTo("1000"),
				"employees.employee[1].id", equalTo("ID2"),
				"employees.employee[1].name", equalTo("Second Employee"),
				"employees.employee[1].salary", equalTo("2000"),
				"employees.employee[2].id", equalTo("ID3"),
				"employees.employee[2].name", equalTo("Third Employee"),
				"employees.employee[2].salary", equalTo("3000")
			);
	}

	@Test
	public void testGetAllEmployeesWithRootPaths() {
		// a variation of the above test showing how to
		// test several XML elements
		given().
			accept(MediaType.APPLICATION_XML).
		when().
			get(Main.BASE_URI + EMPLOYEES).
		then().
			statusCode(200).
			assertThat().
				root("employees.employee[0]").
				body(
					"id", equalTo("ID1"),
					"name", equalTo("First Employee"),
					"salary", equalTo("1000")
				).
				root("employees.employee[1]").
				body(
					"id", equalTo("ID2")
					// similar assertions for the other fields
				);
	}

	@Test
	public void testGetOneEmployee() {
		given().
			accept(MediaType.APPLICATION_XML).
		when().
			get(Main.BASE_URI + EMPLOYEES + "/ID2").
		then().
			statusCode(200).
			assertThat().
			body(
				"employee.id", equalTo("ID2"),
				"employee.name", equalTo("Second Employee"),
				"employee.salary", equalTo("2000")
			);
	}

	@Test
	public void testGetOneEmployeeWithNonExistingId() {
		given().
			accept(MediaType.APPLICATION_XML).
		when().
			get(Main.BASE_URI + EMPLOYEES + "/foo").
		then().
			statusCode(204); // Error code: No Content
	}

	@Test
	public void testGetAllEmployeesJSON() {
		given().
			accept(MediaType.APPLICATION_JSON).
		when().
			get(Main.BASE_URI + EMPLOYEES).
		then().
			statusCode(200).
			assertThat().
			body(
				"id[0]", equalTo("ID1"),
				"name[0]", equalTo("First Employee"),
				"salary[0]", equalTo(1000),
				// NOTE: "salary" retains its integer type in JSON
				// so it must be equal to 1000 NOT "1000"
				"id[1]", equalTo("ID2"),
				"name[1]", equalTo("Second Employee")
				// other checks omitted
			);
	}

	@Test
	public void testGetOneEmployeeJSON() {
		given().
			accept(MediaType.APPLICATION_JSON).
		when().
			get(Main.BASE_URI + EMPLOYEES + "/ID2").
		then().
			statusCode(200).
			assertThat().
			body(
				"id", equalTo("ID2"),
				"name", equalTo("Second Employee"),
				"salary", equalTo(2000)
				// NOTE: "salary" retains its integer type in JSON
				// so it must be equal to 2000 NOT "2000"
			);
	}

	@Test
	public void testGetOneEmployeeWithNonExistingIdJSON() {
		given().
			accept(MediaType.APPLICATION_JSON).
		when().
			get(Main.BASE_URI + EMPLOYEES + "/foo").
		then().
			statusCode(204); // Error code: No Content
	}

	@Test
	public void testCount() {
		given().
		when().
			get(Main.BASE_URI + EMPLOYEES + "/count").
		then().
			statusCode(200).
			assertThat().
			body(
				equalTo(
					""+EmployeeRepository.instance.findAll().size()));
		// let's make this test resilient to future changes in the
		// implementation of the repository (in case we add further
		// initial records)
	}

	@Test
	public void testAddNewEmployee() {
		performPostAndAssertEmployeeInserted("A new Employee", "ID4");
		performPostAndAssertEmployeeInserted("Another new Employee", "ID5");
	}

	private void performPostAndAssertEmployeeInserted(String employeeName, String expectedId) {
		JsonObject newObject = createJsonObjectParam(employeeName, 1000);

		int employeeNumber = EmployeeRepository.instance.findAll().size();
		String expectedEmployeeString = expectedEmployeeString(expectedId, employeeName);

		given().
			contentType(MediaType.APPLICATION_JSON).
			body(newObject.toString()).
		when().
			post(Main.BASE_URI + EMPLOYEES).
		then().
			statusCode(201).
			assertThat().
			body(
				equalTo(
					"Employee saved : "
					+ expectedEmployeeString)
			);

		// also check that the new employee is effectively added
		assertEquals(employeeNumber+1,
				EmployeeRepository.instance.findAll().size());
		assertEquals(
				expectedEmployeeString,
				EmployeeRepository.instance.findOne(expectedId).toString());
	}

	private String expectedEmployeeString(String expectedId, String employeeName) {
		return "Employee [employeeId="
			+ expectedId
			+ ", name="
			+ employeeName
			+ ", salary=1000]";
	}

	@Test
	public void testUpdateEmployeeWithMissingJSON() {
		given().
			contentType(MediaType.APPLICATION_JSON).
		when().
			put(Main.BASE_URI + EMPLOYEES + "/ID1").
		then().
			statusCode(400);
	}

	@Test
	public void testUpdateNonExistantEmployee() {
		given().
			contentType(MediaType.APPLICATION_JSON).
			body(createJsonObjectParam("An employee", 1000).toString()).
		when().
			put(Main.BASE_URI + EMPLOYEES + "/IDfoo").
		then().
			statusCode(304);
	}

	@Test
	public void testUpdateExistantEmployee() {
		final String previousEmployee = "Employee [employeeId=ID1, name=First Employee, salary=1000]";
		final String updatedEmployee = "Employee [employeeId=ID1, name=Updated, salary=5000]";

		performPutAndAssertEmployee(
			previousEmployee,
			updatedEmployee);
		// verify also idempotency:
		// we update the already updated employee
		// and nothing changes
		performPutAndAssertEmployee(
			updatedEmployee,
			updatedEmployee);
	}

	@Test
	public void testDeleteNonExistingEmployee() {
		given().
		when().
			delete(Main.BASE_URI + EMPLOYEES + "/IDfoo").
		then().
			statusCode(304);
	}

	@Test
	public void testDeleteExistingEmployee() {
		given().
		when().
			delete(Main.BASE_URI + EMPLOYEES + "/ID1").
		then().
			statusCode(200).
			assertThat().
			body(
			equalTo(
				"Employee removed: Employee [employeeId=ID1, name=First Employee, salary=1000]")
			);
	}

	private JsonObject createJsonObjectParam(String employeeName, int salary) {
		return Json.createObjectBuilder()
				.add("name", employeeName)
				.add("salary", salary)
				.build();
	}

	private void performPutAndAssertEmployee(final String expectedPreviousEmployeeString, final String expectedUpdatedEmployee) {
		final String employeeName = "Updated";
		JsonObject newObject = createJsonObjectParam(employeeName, 5000);

		int employeeNumber = EmployeeRepository.instance.findAll().size();

		given().
			contentType(MediaType.APPLICATION_JSON).
			body(newObject.toString()).
		when().
			put(Main.BASE_URI + EMPLOYEES + "/ID1").
		then().
			statusCode(200).
			assertThat().
			body(
			equalTo(
			"previous Employee: " 
			+ expectedPreviousEmployeeString
			+ "\n"
			+ "updated with: "
			+ expectedUpdatedEmployee)
			);

		// also check that the new employee is effectively updated
		assertEquals(employeeNumber,
				EmployeeRepository.instance.findAll().size());
		assertEquals(
				expectedUpdatedEmployee,
				EmployeeRepository.instance.findOne("ID1").toString());
	}

}
