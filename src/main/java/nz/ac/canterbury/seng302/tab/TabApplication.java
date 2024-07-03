package nz.ac.canterbury.seng302.tab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

/**
 * TAB (not that TAB) entry-point
 * Note @link{SpringBootApplication} annotation
 */
@SpringBootApplication( exclude = { SecurityAutoConfiguration.class })
public class TabApplication {

	/**
	 * Main entry point, runs the Spring application
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(TabApplication.class, args);
	}

}
