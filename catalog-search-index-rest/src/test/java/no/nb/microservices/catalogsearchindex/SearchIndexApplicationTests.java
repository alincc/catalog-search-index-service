package no.nb.microservices.catalogsearchindex;

import no.nb.microservices.catalogsearchindex.Application;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class SearchIndexApplicationTests {

	@Test
	public void contextLoads() {

	}

}
