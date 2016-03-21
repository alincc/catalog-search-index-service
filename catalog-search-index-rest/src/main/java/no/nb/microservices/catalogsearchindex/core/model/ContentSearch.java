package no.nb.microservices.catalogsearchindex.core.model;

import org.springframework.data.domain.Page;

import java.util.List;

public class ContentSearch {
    private Page<Item> page;
    private List<ContentFragment> fragments;

    public Page<Item> getPage() {
        return page;
    }

    public void setPage(Page<Item> page) {
        this.page = page;
    }

    public List<ContentFragment> getFragments() {
        return fragments;
    }

    public void setFragments(List<ContentFragment> fragments) {
        this.fragments = fragments;
    }

}
