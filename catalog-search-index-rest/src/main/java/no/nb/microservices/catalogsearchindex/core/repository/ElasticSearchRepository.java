package no.nb.microservices.catalogsearchindex.core.repository;

import static org.elasticsearch.index.query.QueryBuilders.queryString;
import no.nb.microservices.catalogsearchindex.core.model.Item;

import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Repository;

@Repository
public class ElasticSearchRepository implements ISearchRepository {

    private final ElasticsearchOperations template;

    @Autowired
    public ElasticSearchRepository(final ElasticsearchOperations template) {
        this.template = template;
    }

    @Override
    public Page<Item> search(String query, Pageable pageRequest) {
        QueryBuilder queryBuilder = queryString(query);

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder).withPageable(pageRequest).build();

        return template.queryForPage(searchQuery, Item.class);
    }

}
