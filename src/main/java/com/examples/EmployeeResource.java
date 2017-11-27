package com.examples;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.examples.model.Employee;
import com.examples.repository.EmployeeRepository;

/**
 * Root resource (exposed at "employee" path)
 */
@Path("employees")
public class EmployeeResource {

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public List<Employee> getAllEmployees() {
		return EmployeeRepository.instance.findAll();
	}

	@GET
	// Defines that the next path parameter after "employees is
	// treated as a parameter and passed to the EmployeeResource
	// Allows to type http://localhost:8080/myapp/employees/ID1
	// ID1 will be treated as parameter "id" and passed to this method
	@Path("{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Employee getOneEmployee(@PathParam("id") String id) {
		return EmployeeRepository.instance.findOne(id);
	}
}
