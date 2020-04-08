package scott.spring.webapisandbox;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import scott.spring.webapisandbox.webapi.controllers.EmployeeController;

@SpringBootTest
public class ApplicationTests {

	@Autowired
	private EmployeeController _employeeCcontroller;

	@Test
	public void contextLoads() {
		assertThat(_employeeCcontroller).isNotNull();
	}
}
