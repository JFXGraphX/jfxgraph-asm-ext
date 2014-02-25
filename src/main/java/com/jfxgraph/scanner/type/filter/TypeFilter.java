package com.jfxgraph.scanner.type.filter;

import java.io.IOException;

import com.jfxgraph.scanner.reader.MetadataReader;
import com.jfxgraph.scanner.reader.MetadataReaderFactory;

/**
 * Base interface for type filters using a {@link MetadataReader}
 * 
 * @author Albert
 * @since 1.0
 */
public interface TypeFilter
{
    /**
     * Determine whether this filter matches for the class described by
     * the given metadata.
     * @param metadataReader the metadata reader for the target class
     * @param metadataReaderFactory a factory for obtaining metadata readers
     * for other classes (such as superclasses and interfaces)
     * @return whether this filter matches
     * @throws IOException in case of I/O failure when reading metadata
     */
    boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException;
}
