package no.nb.microservices.catalogsearchindex.core.repository;

import no.nb.microservices.catalogsearchindex.core.model.Item;

import org.springframework.data.repository.CrudRepository;

public interface TestDataRepository extends CrudRepository<Item, String>{

}
