package no.nb.microservices.catalogsearchindex.rest.controller;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import no.nb.microservices.catalogsearchindex.ItemResource;
import no.nb.microservices.catalogsearchindex.core.model.Item;

public class ItemResourceAssemblerTest {

	@Before
	public void init() {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/search?q=Junit");
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);

        RequestContextHolder.setRequestAttributes(attributes);
	}
	
	@After
    public void cleanUp() {
        RequestContextHolder.resetRequestAttributes();
    }
	
	@Test
	public void testToResource() {
		ItemResourceAssembler assembler = new ItemResourceAssembler();
		Item item = new Item();
		item.setId("Junit");
		ItemResource resource = assembler.toResource(item);
		assertEquals("Junit", resource.getItemId());
	}
}
