package no.nb.microservices.catalogsearchindex.core.metadata.service;

import no.nb.microservices.catalogmetadata.model.struct.StructMap;

public interface IMetadataService {
    StructMap getStructById(String id);
}
