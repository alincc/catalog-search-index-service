package no.nb.microservices.catalogsearchindex.core.repository;

import no.nb.microservices.catalogsearchindex.core.model.Item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ISearchRepository {
    Page<Item> search(String query, Pageable pageRequest);
}
