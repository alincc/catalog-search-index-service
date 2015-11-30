package no.nb.microservices.catalogsearchindex.rest.controller;

import no.nb.microservices.catalogsearchindex.ItemResource;
import no.nb.microservices.catalogsearchindex.core.model.Item;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

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
        item.setFirstIndexTime("2015-05-05");
        item.setContentClasses(Arrays.asList("restricted", "public"));
        item.setMetadataClasses(Arrays.asList("public"));
        item.setDigital(true);
        item.setTitle("Nice Title");
        item.setMediaTypes(Arrays.asList("Bøker", "Musikk"));
		ItemResource resource = assembler.toResource(item);
		assertEquals("Junit", resource.getItemId());
        assertEquals("2015-05-05", resource.getFirstIndexTime());
        assertEquals("Should have two contentClasses", 2 ,resource.getContentClasses().size());
        assertEquals("Should have one metadataClasses", 1 ,resource.getMetadataClasses().size());
        assertEquals("Should be digital", true, resource.isDigital());
        assertEquals("Should hava a title", item.getTitle(), resource.getTitle());
        assertEquals("Should have two media types", 2 ,resource.getMediaTypes().size());
	}
}
