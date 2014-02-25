/**
 * Copyright (c) 2012-2013,Epic-HUST Technology(Wuhan)Co.,Ltd. All Rights Reserved.
 */
package com.jfxgraph.scanner.type.filter;

import java.io.IOException;

import com.jfxgraph.scanner.reader.MetadataReader;
import com.jfxgraph.scanner.reader.MetadataReaderFactory;
import com.jfxgraph.scanner.type.ClassMetadata;

/**
 * AbstractClassTestingTypeFilter.
 * 
 * @author Albert
 * @since 1.0
 */
public abstract class AbstractClassTestingTypeFilter implements TypeFilter
{
    /**
     * Determine a match based on the given ClassMetadata object.
     * 
     * @param metadata
     *            the ClassMetadata object
     * @return whether this filter matches on the specified type
     */
    protected abstract boolean match(ClassMetadata metadata);

    /**
     * @see com.jfxgraph.scanner.type.filter.TypeFilter#match(com.jfxgraph.scanner.reader.MetadataReader,
     *      com.jfxgraph.scanner.reader.MetadataReaderFactory)
     */
    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException
    {
        return match(metadataReader.getClassMetadata());
    }
}
