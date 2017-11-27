package com.examples.repository;

import java.util.LinkedList;
import java.util.List;

import com.examples.model.Employee;

/**
 * An example repository for employees.
 * 
 * In a real application this should be handled by a database.
 */
public class EmployeeRepository {

	public static final EmployeeRepository instance = new EmployeeRepository();

	private List<Employee> employees = new LinkedList<>();

	private EmployeeRepository() {
		// initialize the "db" with some contents
		employees.add(new Employee("ID1", "First Employee", 1000));
		employees.add(new Employee("ID2", "Second Employee", 2000));
		employees.add(new Employee("ID3", "Third Employee", 3000));
	}

	public List<Employee> findAll() {
		return employees;
	}

	public Employee findOne(String id) {
		return employees.
				stream().
				filter(e -> e.getEmployeeId().equals(id)).
				findFirst().orElse(null);
	}
}
