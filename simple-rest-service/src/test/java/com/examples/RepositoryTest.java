package com.examples;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.examples.model.Employee;
import com.examples.repository.EmployeeRepository;

/**
 * In spite of {@link EmployeeRepository} being a dummy temporary implementation
 * of a repository, it's better to write tests for that, in order to make sure
 * that if something is not working in the EmployeeResource then it's not the
 * repository's fault.
 */
public class RepositoryTest {

	private EmployeeRepository repository;

	private Map<String, Employee> map;

	@Before
	public void setup() {
		map = new HashMap<>();
		// anonymous subclass to make the protected constructor
		// accessible in the test
		repository = new EmployeeRepository(map) {

		};
		// make sure the repository is always empty
		map.clear();
	}

	@Test
	public void testFindAll() {
		assertRepositorySize(0);
		map.put("ID1", newEmployee("ID1", "Test Employee", 0));
		assertRepositorySize(1);
	}

	@Test
	public void testFindOne() {
		assertNull(repository.findOne("ID1"));
		map.put("ID1", newEmployee("ID1", "Test Employee", 0));
		assertNotNull(repository.findOne("ID1"));
	}

	@Test
	public void testSaveEmployeeWithoutIdCreatesAnIdAutomatically() {
		Employee e = newEmployee(null, "Test Employee", 0);
		Employee saved = repository.save(e);
		assertNotNull(saved.getEmployeeId());
		assertRepositorySize(1);
	}

	@Test
	public void testSaveEmployeeWithId() {
		Employee e = newEmployee("ID1", "Test Employee", 0);
		Employee saved = repository.save(e);
		assertEquals("ID1", saved.getEmployeeId());
		assertRepositorySize(1);
	}

	@Test
	public void testDeleteByIdNonExistant() {
		Employee e = newEmployee("ID1", "Test Employee", 0);
		map.put("ID1", e);
		assertSame(e, repository.deleteById("ID1"));
	}

	@Test
	public void testDeleteById() {
		assertNull(repository.deleteById("foo"));
	}

	private void assertRepositorySize(int expectedSize) {
		assertEquals(expectedSize, repository.findAll().size());
	}

	private Employee newEmployee(String id, String name, int salary) {
		return new Employee(id, name, salary);
	}

}
