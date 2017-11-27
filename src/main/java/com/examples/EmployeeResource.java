package com.examples;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.examples.model.Employee;

/**
 * Root resource (exposed at "employee" path)
 */
@Path("employee")
public class EmployeeResource {

	/**
	 * Method handling HTTP GET requests. The returned object will be sent to the
	 * client as "text/xml" media type.
	 *
	 * @return an example employee
	 */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Employee getItXML() {
		Employee employee = new Employee("E1", "An employee", 1000);
		return employee;
	}
}
