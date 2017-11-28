package com.examples;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.examples.model.Employee;
import com.examples.repository.EmployeeRepository;

/**
 * Root resource (exposed at "employee" path)
 */
@Path("employees")
public class EmployeeResource {

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Collection<Employee> getAllEmployees() {
		return EmployeeRepository.instance.findAll();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Employee> getAllEmployeesJSON() {
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

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Employee getOneEmployeeJSON(@PathParam("id") String id) {
		return EmployeeRepository.instance.findOne(id);
	}

	// returns the number of employees
	// Use http://localhost:8080/myapp/employees/count
	// to get the total number of records
	@GET
	@Path("count")
	@Produces(MediaType.TEXT_PLAIN)
	public String getCount() {
		int count = EmployeeRepository.instance.findAll().size();
		return String.valueOf(count);
	}

	/**
	 * Adds a new Employee to the database. For simplicity, we assume that the
	 * operation always succeeds.
	 * 
	 * @param employee
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addEmployee(Employee employee) {
		String result = "Employee saved : " + EmployeeRepository.instance.save(employee);
		return Response.status(201).entity(result).build();
	}

	/**
	 * Updates the Employee with the given id.
	 * 
	 * @param id
	 * @param employee
	 * @return
	 */
	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateEmployee(@PathParam("id") String id, Employee employee) {
		Employee existing = EmployeeRepository.instance.findOne(id);
		if (employee == null) {
			return Response.
					status(Status.BAD_REQUEST).
					entity("Missing values for updating the Employee").
					build();
		}
		if (existing == null) {
			return Response.
					notModified().
					entity("No Employee with id " + id + " found").
					build();
		} else {
			employee.setEmployeeId(id);
			EmployeeRepository.instance.save(employee);
			String result = "previous Employee: " +
				existing.toString() + "\n" +
				"updated with: " +
				employee;
			return Response.ok().entity(result).build();
		}
	}

}
