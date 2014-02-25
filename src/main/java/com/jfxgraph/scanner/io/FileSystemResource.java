/**
 * Copyright (c) 2013 著作权由艾普工华武汉科技公司所有。著作权人保留一切权利。 这份授权条款，在使用者符合以下四条件的情形下，授予使用者使用及再散播本
 * 软件包装原始码及二进位可执行形式的权利，无论此包装是否经改作皆然：
 * 
 * 1. 对于本软件源代码的再散播，必须保留上述的版权宣告、此四条件表列，以 及下述的免责声明。 2. 对于本套件二进位可执行形式的再散播，必须连带以文件以及／或者其他附
 * 于散播包装中的媒介方式，重制上述之版权宣告、此四条件表列，以及下述 的免责声明。 3. 所有提及本软件功能或是本软件使用之宣传材料，都必须包还含下列之交 待文字：
 * “本产品内含有由柏克莱加州大学及其软件贡献者所开发的软件。” 4. 未获事前取得书面许可，不得使用柏克莱加州大学或本软件贡献者之名称， 来为本软件之衍生物做任何表示支持、认可或推广、促销之行为。
 * 
 * 免责声明：本软件是由加州大学董事会及本软件之贡献者以现状（"as is"）提供， 本软件包装不负任何明示或默示之担保责任，包括但不限于就适售性以及特定目
 * 的的适用性为默示性担保。艾普工华武汉科技公司及本软件之贡献者，无论任何条件、 无论成因或任何责任主义、无论此责任为因合约关系、无过失责任主义或因非违
 * 约之侵权（包括过失或其他原因等）而起，对于任何因使用本软件包装所产生的 任何直接性、间接性、偶发性、特殊性、惩罚性或任何结果的损害（包括但不限
 * 于替代商品或劳务之购用、使用损失、资料损失、利益损失、业务中断等等）， 不负任何责任，即在该种使用已获事前告知可能会造成此类损害的情形下亦然。
 */
package com.jfxgraph.scanner.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

import com.jfxgraph.scanner.util.Assert;
import com.jfxgraph.scanner.util.StringUtils;

/**
 * {@link Resource} implementation for {@code java.io.File} handles. Obviously supports resolution
 * as File, and also as URL. Implements the extended {@link WritableResource} interface.
 * 
 * @author Juergen Hoeller
 * @since 28.12.2003
 * @see java.io.File
 */
public class FileSystemResource extends AbstractResource implements WritableResource
{

    private final File file;

    private final String path;

    /**
     * Create a new FileSystemResource from a File handle.
     * <p>
     * Note: When building relative resources via {@link #createRelative}, the relative path will
     * apply <i>at the same directory level</i>: e.g. new File("C:/dir1"), relative path "dir2" ->
     * "C:/dir2"! If you prefer to have relative paths built underneath the given root directory,
     * use the {@link #FileSystemResource(String) constructor with a file path} to append a trailing
     * slash to the root path: "C:/dir1/", which indicates this directory as root for all relative
     * paths.
     * 
     * @param file
     *            a File handle
     */
    public FileSystemResource(File file)
    {
        Assert.notNull(file, "File must not be null");
        this.file = file;
        this.path = StringUtils.cleanPath(file.getPath());
    }

    /**
     * Create a new FileSystemResource from a file path.
     * <p>
     * Note: When building relative resources via {@link #createRelative}, it makes a difference
     * whether the specified resource base path here ends with a slash or not. In the case of
     * "C:/dir1/", relative paths will be built underneath that root: e.g. relative path "dir2" ->
     * "C:/dir1/dir2". In the case of "C:/dir1", relative paths will apply at the same directory
     * level: relative path "dir2" -> "C:/dir2".
     * 
     * @param path
     *            a file path
     */
    public FileSystemResource(String path)
    {
        Assert.notNull(path, "Path must not be null");
        this.file = new File(path);
        this.path = StringUtils.cleanPath(path);
    }

    /**
     * Return the file path for this resource.
     */
    public final String getPath()
    {
        return this.path;
    }

    /**
     * This implementation returns whether the underlying file exists.
     * 
     * @see java.io.File#exists()
     */
    @Override
    public boolean exists()
    {
        return this.file.exists();
    }

    /**
     * This implementation checks whether the underlying file is marked as readable (and corresponds
     * to an actual file with content, not to a directory).
     * 
     * @see java.io.File#canRead()
     * @see java.io.File#isDirectory()
     */
    @Override
    public boolean isReadable()
    {
        return (this.file.canRead() && !this.file.isDirectory());
    }

    /**
     * This implementation opens a FileInputStream for the underlying file.
     * 
     * @see java.io.FileInputStream
     */
    public InputStream getInputStream() throws IOException
    {
        return new FileInputStream(this.file);
    }

    /**
     * This implementation returns a URL for the underlying file.
     * 
     * @see java.io.File#toURI()
     */
    @Override
    public URL getURL() throws IOException
    {
        return this.file.toURI().toURL();
    }

    /**
     * This implementation returns a URI for the underlying file.
     * 
     * @see java.io.File#toURI()
     */
    @Override
    public URI getURI() throws IOException
    {
        return this.file.toURI();
    }

    /**
     * This implementation returns the underlying File reference.
     */
    @Override
    public File getFile()
    {
        return this.file;
    }

    /**
     * This implementation returns the underlying File's length.
     */
    @Override
    public long contentLength() throws IOException
    {
        return this.file.length();
    }

    /**
     * This implementation creates a FileSystemResource, applying the given path relative to the
     * path of the underlying file of this resource descriptor.
     * 
     * @see org.springframework.util.StringUtils#applyRelativePath(String, String)
     */
    @Override
    public Resource createRelative(String relativePath)
    {
        String pathToUse = StringUtils.applyRelativePath(this.path, relativePath);
        return new FileSystemResource(pathToUse);
    }

    /**
     * This implementation returns the name of the file.
     * 
     * @see java.io.File#getName()
     */
    @Override
    public String getFilename()
    {
        return this.file.getName();
    }

    /**
     * This implementation returns a description that includes the absolute path of the file.
     * 
     * @see java.io.File#getAbsolutePath()
     */
    public String getDescription()
    {
        return "file [" + this.file.getAbsolutePath() + "]";
    }

    // implementation of WritableResource

    /**
     * This implementation checks whether the underlying file is marked as writable (and corresponds
     * to an actual file with content, not to a directory).
     * 
     * @see java.io.File#canWrite()
     * @see java.io.File#isDirectory()
     */
    public boolean isWritable()
    {
        return (this.file.canWrite() && !this.file.isDirectory());
    }

    /**
     * This implementation opens a FileOutputStream for the underlying file.
     * 
     * @see java.io.FileOutputStream
     */
    public OutputStream getOutputStream() throws IOException
    {
        return new FileOutputStream(this.file);
    }

    /**
     * This implementation compares the underlying File references.
     */
    @Override
    public boolean equals(Object obj)
    {
        return (obj == this || (obj instanceof FileSystemResource && this.path.equals(((FileSystemResource) obj).path)));
    }

    /**
     * This implementation returns the hash code of the underlying File reference.
     */
    @Override
    public int hashCode()
    {
        return this.path.hashCode();
    }

}
