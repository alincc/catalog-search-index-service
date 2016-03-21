package no.nb.microservices.catalogsearchindex.core.services;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import no.nb.microservices.catalogsearchindex.core.index.service.SearchServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import no.nb.microservices.catalogsearchindex.core.model.Item;
import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;
import no.nb.microservices.catalogsearchindex.core.model.SearchCriteria;
import no.nb.microservices.catalogsearchindex.core.index.repository.SearchRepository;

@RunWith(MockitoJUnitRunner.class)
public class SearchServiceImplTest {

	@InjectMocks
	private SearchServiceImpl searchService;
	
	@Mock
	private SearchRepository searchRepository;
	
	@Test
	public void testGrouping() {
		
		Pageable pageRequest1 = new PageRequest(0, 3);
		
		List<Item> content = new ArrayList<>();
		Item item1 = new Item("1");
		item1.setTitle("nonunique");
		item1.setMediaTypes(Arrays.asList("Bøker"));
		item1.setCreators(Arrays.asList("Ola"));
		content.add(item1);
		Item item2 = new Item("2");
		item2.setTitle("unique1");
		item2.setMediaTypes(Arrays.asList("Bøker"));
		item2.setCreators(Arrays.asList("Ola"));
		content.add(item2);

		content.add(item1);
		Page<Item> page = new PageImpl<>(content, pageRequest1, 6);

		List<Item> content2 = new ArrayList<>();
		Item item12 = new Item("3");
		item12.setTitle("unique2");
		item12.setMediaTypes(Arrays.asList("Bøker"));
		item12.setCreators(Arrays.asList("Ola"));
		content2.add(item12);
		Item item22 = new Item("4");
		item22.setTitle("nonunique");
		item22.setMediaTypes(Arrays.asList("Bøker"));
		item22.setCreators(Arrays.asList("Ola"));
		content2.add(item12);
		Pageable pageRequest2 = new PageRequest(1, 2);
		Page<Item> page2 = new PageImpl<>(content2, pageRequest2, 6);

		List<Item> content3 = new ArrayList<>();
		Item item13 = new Item("5");
		item13.setTitle("unique3");
		item13.setMediaTypes(Arrays.asList("Bøker"));
		item13.setCreators(Arrays.asList("Ola"));
		content3.add(item13);
		Item item23 = new Item("5");
		item23.setTitle("unique4");
		item23.setMediaTypes(Arrays.asList("Bøker"));
		item23.setCreators(Arrays.asList("Ola2"));
		content3.add(item13);
		Pageable pageRequest3 = new PageRequest(2, 2);
		Page<Item> page3 = new PageImpl<>(content3, pageRequest3, 6);

		when(searchRepository.search(any())).thenReturn(
				new SearchAggregated(page, null),
				new SearchAggregated(page2, null),
				new SearchAggregated(page3, null));
		
		SearchCriteria searchCriteria = new SearchCriteria("query");
		searchCriteria.setGrouping(true);
		Pageable pageRequest = new PageRequest(0, 3);
		searchCriteria.setPageRequest(pageRequest);
	
		
		SearchAggregated result = searchService.search(searchCriteria);

		assertThat(result.getPage().getNumberOfElements(), is(3));
	}

	@Test
	public void testGroupingNotReachingSize() {
		
		
		List<Item> content = new ArrayList<>();
		Item item1 = new Item("1");
		item1.setTitle("nonunique");
		item1.setMediaTypes(Arrays.asList("Bøker"));
		item1.setCreators(Arrays.asList("Ola"));
		content.add(item1);
		Item item2 = new Item("2");
		item2.setTitle("unique1");
		item2.setMediaTypes(Arrays.asList("Bøker"));
		item2.setCreators(Arrays.asList("Ola"));
		content.add(item2);

		content.add(item1);
		Pageable pageRequest1 = new PageRequest(0, 3);
		Page<Item> page = new PageImpl<>(content, pageRequest1, 2);

		when(searchRepository.search(any())).thenReturn(
				new SearchAggregated(page, null));
		
		SearchCriteria searchCriteria = new SearchCriteria("query");
		searchCriteria.setGrouping(true);
		Pageable pageRequest = new PageRequest(0, 3);
		searchCriteria.setPageRequest(pageRequest);
	
		
		SearchAggregated result = searchService.search(searchCriteria);

		assertThat(result.getPage().getNumberOfElements(), is(2));
	}	
	
}
