package no.nb.microservices.catalogsearchindex.core.content.service;

import no.nb.microservices.catalogmetadata.model.struct.Div;
import no.nb.microservices.catalogmetadata.model.struct.Resource;
import no.nb.microservices.catalogmetadata.model.struct.StructMap;
import no.nb.microservices.catalogsearchindex.core.metadata.service.IMetadataService;
import no.nb.microservices.catalogsearchindex.core.model.ContentFragment;
import no.nb.microservices.catalogsearchindex.core.model.ContentSearch;
import no.nb.microservices.catalogsearchindex.core.model.Item;
import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;
import no.nb.oletobias.metadatarepository.freetext.Document;
import no.nb.oletobias.metadatarepository.freetext.Page;
import no.nb.oletobias.metadatarepository.freetext.Rectangle;
import no.nb.oletobias.metadatarepository.freetext.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ContentService implements IContentService {
    private final IMetadataService metadataService;

    @Autowired
    public ContentService(IMetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @Override
    public ContentSearch getContent(SearchAggregated searchAggregated) {
        ContentSearch contentSearch = new ContentSearch();
        contentSearch.setPage(searchAggregated.getPage());
        contentSearch.setFragments(createContentFragments(searchAggregated));

        return contentSearch;
    }

    private List<ContentFragment> createContentFragments(SearchAggregated searchAggregated) {
        List<ContentFragment> fragments = new ArrayList<>();
        try {
            List<String> freetextFragments = getFreetextFragments(searchAggregated);
            List<String> freetextMetadata = getFreetextMetadata(searchAggregated);
            Document metadataDocument = Document.buildPageDocument(String.join(" ",freetextMetadata).trim());
            StructMap struct = metadataService.getStructById(searchAggregated.getPage().getContent().get(0).getId());
            Map<String, String[]> pageNameMap = buildPageNameMapping(struct);
            freetextFragments.stream().filter(fragment -> fragment != null).forEach(fragment -> {
                Word word = Word.parse(fragment ,0);
                for (Rectangle wordRectangle : word.getPositionMetadata()) {
                    String pageName = metadataDocument.getPageNameFromPageID(wordRectangle.getPageId().getValue());
                    Page page = metadataDocument.getPageFromPageID(wordRectangle.getPageId().getValue());
                    String[] pageData = pageNameMap.get(pageName);
                    String pageUrn = pageData[4];

                    float resWidth = Float.parseFloat(pageData[1]);
                    float resHeight = Float.parseFloat(pageData[2]);

                    float factorX = resWidth / (float) page.getRectangle().getWidth().getValue();
                    float factorY = resHeight / (float) page.getRectangle().getHeight().getValue();

                    float x = (float) wordRectangle.getLeft().getValue();
                    float y = (float) wordRectangle.getTop().getValue();
                    float width = (float) wordRectangle.getWidth().getValue() * factorX;
                    float height = (float) wordRectangle.getHeight().getValue() * factorY;

                    int t = Math.round(y * factorY);
                    int l = Math.round(x * factorX);

                    ContentFragment contentFragment = new ContentFragment(l, t, (int) width, (int) height, pageUrn, word.getText(), "", "");
                    fragments.add(contentFragment);

                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return fragments;
    }

    private Map<String, String[]> buildPageNameMapping(StructMap struct) {
        Map<String, String[]> orderMap = new HashMap<String, String[]>();
        for(Div div : struct.getDivs()) {
            Resource resource = div.getResource();
            String orderlabel = "";
            if (div.getOrderLabel() != null && !div.getOrderLabel().isEmpty()) {
                orderlabel = div.getOrderLabel();
            }
            orderMap.put(div.getId(), new String[]{div.getOrder(), ""+resource.getWidth(), ""+resource.getHeight(), orderlabel, resource.getHref()});
        }
        return orderMap;
    }

    private List<String> getFreetextFragments(SearchAggregated searchAggregated) {
        List<Item> items = searchAggregated.getPage().getContent();
        if (items != null && items.size() == 1) {
            return items.get(0).getFreetextHits();
        } else {
            return Collections.emptyList();
        }
    }

    private List<String> getFreetextMetadata(SearchAggregated searchAggregated) {
        List<Item> items = searchAggregated.getPage().getContent();
        if (items != null && items.size() == 1) {
            return items.get(0).getFreetextMetadatas();
        } else {
            return Collections.emptyList();
        }
    }

}
