package no.nb.microservices.catalogsearchindex.core.metadata.repository;

import no.nb.microservices.catalogmetadata.model.struct.StructMap;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("catalog-metadata-service")
public interface MetadataRepository {

    @RequestMapping(method = RequestMethod.GET, value = "/catalog/v1/metadata/{id}/struct", produces = MediaType.APPLICATION_XML_VALUE)
    StructMap getStructById(@PathVariable("id") String id);

}
