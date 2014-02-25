/**
 * Copyright (c) 2013 著作权由艾普工华武汉科技公司所有。著作权人保留一切权利。
 * 这份授权条款，在使用者符合以下四条件的情形下，授予使用者使用及再散播本 软件包装原始码及二进位可执行形式的权利，无论此包装是否经改作皆然：
 * 
 * 1. 对于本软件源代码的再散播，必须保留上述的版权宣告、此四条件表列，以 及下述的免责声明。 2.
 * 对于本套件二进位可执行形式的再散播，必须连带以文件以及／或者其他附 于散播包装中的媒介方式，重制上述之版权宣告、此四条件表列，以及下述 的免责声明。 3.
 * 所有提及本软件功能或是本软件使用之宣传材料，都必须包还含下列之交 待文字： “本产品内含有由柏克莱加州大学及其软件贡献者所开发的软件。” 4.
 * 未获事前取得书面许可，不得使用柏克莱加州大学或本软件贡献者之名称， 来为本软件之衍生物做任何表示支持、认可或推广、促销之行为。
 * 
 * 免责声明：本软件是由加州大学董事会及本软件之贡献者以现状（"as is"）提供， 本软件包装不负任何明示或默示之担保责任，包括但不限于就适售性以及特定目
 * 的的适用性为默示性担保。艾普工华武汉科技公司及本软件之贡献者，无论任何条件、 无论成因或任何责任主义、无论此责任为因合约关系、无过失责任主义或因非违
 * 约之侵权（包括过失或其他原因等）而起，对于任何因使用本软件包装所产生的 任何直接性、间接性、偶发性、特殊性、惩罚性或任何结果的损害（包括但不限
 * 于替代商品或劳务之购用、使用损失、资料损失、利益损失、业务中断等等）， 不负任何责任，即在该种使用已获事前告知可能会造成此类损害的情形下亦然。
 */
package com.jfxgraph.scanner.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

/**
 * Resource.java
 * Interface for a resource descriptor that abstracts from the actual
 * type of underlying resource, such as a file or class path resource.
 *
 * <p>An InputStream can be opened for every resource if it exists in
 * physical form, but a URL or File handle can just be returned for
 * certain resources. The actual behavior is implementation-specific.
 * 
 * @author Albert
 * @since 4.0 2013年10月25日 上午11:43:53
 */
public interface Resource extends InputStreamSource
{
    /**
     * Return whether this resource actually exists in physical form.
     * <p>
     * This method performs a definitive existence check, whereas the existence
     * of a {@code Resource} handle only guarantees a valid descriptor handle.
     */
    boolean exists();

    /**
     * Return whether the contents of this resource can be read, e.g. via
     * {@link #getInputStream()} or {@link #getFile()}.
     * <p>
     * Will be {@code true} for typical resource descriptors; note that actual
     * content reading may still fail when attempted. However, a value of
     * {@code false} is a definitive indication that the resource content cannot
     * be read.
     * 
     * @see #getInputStream()
     */
    boolean isReadable();

    /**
     * Return whether this resource represents a handle with an open stream. If
     * true, the InputStream cannot be read multiple times, and must be read and
     * closed to avoid resource leaks.
     * <p>
     * Will be {@code false} for typical resource descriptors.
     */
    boolean isOpen();

    /**
     * Return a URL handle for this resource.
     * 
     * @throws IOException
     *             if the resource cannot be resolved as URL, i.e. if the
     *             resource is not available as descriptor
     */
    URL getURL() throws IOException;

    /**
     * Return a URI handle for this resource.
     * 
     * @throws IOException
     *             if the resource cannot be resolved as URI, i.e. if the
     *             resource is not available as descriptor
     */
    URI getURI() throws IOException;

    /**
     * Return a File handle for this resource.
     * 
     * @throws IOException
     *             if the resource cannot be resolved as absolute file path,
     *             i.e. if the resource is not available in a file system
     */
    File getFile() throws IOException;

    /**
     * Determine the content length for this resource.
     * 
     * @throws IOException
     *             if the resource cannot be resolved (in the file system or as
     *             some other known physical resource type)
     */
    long contentLength() throws IOException;

    /**
     * Determine the last-modified timestamp for this resource.
     * 
     * @throws IOException
     *             if the resource cannot be resolved (in the file system or as
     *             some other known physical resource type)
     */
    long lastModified() throws IOException;

    /**
     * Create a resource relative to this resource.
     * 
     * @param relativePath
     *            the relative path (relative to this resource)
     * @return the resource handle for the relative resource
     * @throws IOException
     *             if the relative resource cannot be determined
     */
    Resource createRelative(String relativePath) throws IOException;

    /**
     * Determine a filename for this resource, i.e. typically the last part of
     * the path: for example, "myfile.txt".
     * <p>
     * Returns {@code null} if this type of resource does not have a filename.
     */
    String getFilename();

    /**
     * Return a description for this resource, to be used for error output when
     * working with the resource.
     * <p>
     * Implementations are also encouraged to return this value from their
     * {@code toString} method.
     * 
     * @see Object#toString()
     */
    String getDescription();

}
