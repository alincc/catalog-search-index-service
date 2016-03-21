package no.nb.microservices.catalogsearchindex.core.metadata.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import no.nb.microservices.catalogmetadata.model.struct.StructMap;
import no.nb.microservices.catalogsearchindex.core.metadata.repository.MetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MetadataService implements IMetadataService {
    private final MetadataRepository metadataRepository;

    @Autowired
    public MetadataService(MetadataRepository metadataRepository) {
        this.metadataRepository = metadataRepository;
    }

    @Override
    @HystrixCommand(fallbackMethod = "getStructByIdFallback")
    public StructMap getStructById(String id) {
        return metadataRepository.getStructById(id);
    }

    private StructMap getStructByIdFallback(String id) {
        return new StructMap();
    }
}
