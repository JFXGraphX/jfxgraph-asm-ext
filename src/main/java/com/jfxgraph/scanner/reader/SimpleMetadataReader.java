package com.jfxgraph.scanner.reader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.jfxgraph.asm.ClassReader;
import com.jfxgraph.scanner.io.Resource;
import com.jfxgraph.scanner.type.AnnotationMetadata;
import com.jfxgraph.scanner.type.ClassMetadata;
import com.jfxgraph.scanner.visitor.AnnotationMetadataReadingVisitor;

/**
 * {@link MetadataReader}简单实现（基于ASM的{@link ClassReader}）.
 * 
 * @author Albert
 * @since 1.0
 */
final class SimpleMetadataReader implements MetadataReader
{
    private final Resource resource;

    private final ClassMetadata classMetadata;

    private final AnnotationMetadata annotationMetadata;

    SimpleMetadataReader(Resource resource, ClassLoader classLoader) throws IOException
    {
        InputStream is = new BufferedInputStream(resource.getInputStream());

        ClassReader classReader;
        try
        {
            classReader = new ClassReader(is);
        } catch (IllegalArgumentException ex)
        {
            throw new IOException("ASM ClassReader 未能解析类文件 - " + "可能是由于新的Java类文件的版本尚不支持: " + resource, ex);
        } finally
        {
            is.close();
        }

        AnnotationMetadataReadingVisitor visitor = new AnnotationMetadataReadingVisitor(classLoader);
        classReader.accept(visitor, ClassReader.SKIP_DEBUG);

        this.annotationMetadata = visitor;
        // (since AnnotationMetadataReader extends ClassMetadataReadingVisitor)
        this.classMetadata = visitor;
        this.resource = resource;
    }

    @Override
    public ClassMetadata getClassMetadata()
    {
        return this.classMetadata;
    }

    @Override
    public Resource getResource()
    {
        return this.resource;
    }

    @Override
    public AnnotationMetadata getAnnotationMetadata()
    {
        return this.annotationMetadata;
    }
}
