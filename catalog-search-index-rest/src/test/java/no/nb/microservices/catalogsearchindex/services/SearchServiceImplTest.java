package no.nb.microservices.catalogsearchindex.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import no.nb.microservices.catalogsearchindex.core.model.Item;
import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;
import no.nb.microservices.catalogsearchindex.core.repository.ISearchRepository;
import no.nb.microservices.catalogsearchindex.core.services.SearchServiceImpl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RunWith(MockitoJUnitRunner.class)
public class SearchServiceImplTest {

	@Mock
	ISearchRepository searchRepositoryMock;
	
	@Test
	public void testSearch() {
		String query = "Bil";
		Pageable pageRequest = new PageRequest(0, 10);
		
		Page<Item> page = new PageImpl<>(Arrays.asList(new Item(), new Item(), new Item()));
		
		when(searchRepositoryMock.search(query, pageRequest)).thenReturn(page);
		SearchServiceImpl searchService = new SearchServiceImpl(searchRepositoryMock);
		SearchAggregated result = searchService.search(query, pageRequest);
		assertNotNull("The result should not be null", result);
		assertEquals("The result size should be 3", 3, result.getPage().getContent().size());
		
		Mockito.verify(searchRepositoryMock).search(query, pageRequest);
	}
}
