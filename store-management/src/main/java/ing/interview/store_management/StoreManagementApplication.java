package ing.interview.store_management;

import ing.interview.store_management.service.DataInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StoreManagementApplication {

	@Autowired
	private DataInitializer userCreatorForDatabaseService;

	public static void main(String[] args) {
		SpringApplication.run(StoreManagementApplication.class, args);
	}

}
