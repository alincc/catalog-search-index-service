package no.nb.microservices.catalogsearchindex;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
@WebIntegrationTest("server.port: 0")
public class SearchControllerIntegrationTest {

	@Autowired
	private WebApplicationContext context;
	
	private MockMvc mockMvc;
	
	@Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }
	
	@Test
	public void searchWithSearchStringOnly() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/search?q=*"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(jsonPath("$._embedded").exists())
			.andReturn();
	}
	
	@Test
    public void searchWithSearchStringAndAggregation() throws Exception {
	    mockMvc.perform(MockMvcRequestBuilders.get("/search?q=*&aggs=ddc1"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(jsonPath("$._embedded.aggregations[0].name").value("ddc1"))
            .andReturn();
    }
}
