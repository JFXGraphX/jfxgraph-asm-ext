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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.jfxgraph.scanner.util.Assert;
import com.jfxgraph.scanner.util.ClassUtils;
import com.jfxgraph.scanner.util.ObjectUtils;
import com.jfxgraph.scanner.util.StringUtils;

/**
 * {@link Resource} implementation for class path resources. Uses either a given
 * ClassLoader or a given Class for loading resources.
 * 
 * <p>
 * Supports resolution as {@code java.io.File} if the class path resource
 * resides in the file system, but not for resources in a JAR. Always supports
 * resolution as URL.
 * 
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 28.12.2003
 * @see ClassLoader#getResourceAsStream(String)
 * @see Class#getResourceAsStream(String)
 */
public class ClassPathResource extends AbstractFileResolvingResource {

    private final String path;

    private ClassLoader classLoader;

    private Class<?> clazz;

    /**
     * Create a new ClassPathResource for ClassLoader usage. A leading slash
     * will be removed, as the ClassLoader resource access methods will not
     * accept it.
     * <p>
     * The thread context class loader will be used for loading the resource.
     * 
     * @param path
     *            the absolute path within the class path
     * @see java.lang.ClassLoader#getResourceAsStream(String)
     * @see org.springframework.util.ClassUtils#getDefaultClassLoader()
     */
    public ClassPathResource(String path) {
        this(path, (ClassLoader) null);
    }

    /**
     * Create a new ClassPathResource for ClassLoader usage. A leading slash
     * will be removed, as the ClassLoader resource access methods will not
     * accept it.
     * 
     * @param path
     *            the absolute path within the classpath
     * @param classLoader
     *            the class loader to load the resource with, or {@code null}
     *            for the thread context class loader
     * @see ClassLoader#getResourceAsStream(String)
     */
    public ClassPathResource(String path, ClassLoader classLoader) {
        Assert.notNull(path, "Path must not be null");
        String pathToUse = StringUtils.cleanPath(path);
        if (pathToUse.startsWith("/")) {
            pathToUse = pathToUse.substring(1);
        }
        this.path = pathToUse;
        this.classLoader = (classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader());
    }

    /**
     * Create a new ClassPathResource for Class usage. The path can be relative
     * to the given class, or absolute within the classpath via a leading slash.
     * 
     * @param path
     *            relative or absolute path within the class path
     * @param clazz
     *            the class to load resources with
     * @see java.lang.Class#getResourceAsStream
     */
    public ClassPathResource(String path, Class<?> clazz) {
        Assert.notNull(path, "Path must not be null");
        this.path = StringUtils.cleanPath(path);
        this.clazz = clazz;
    }

    /**
     * Create a new ClassPathResource with optional ClassLoader and Class. Only
     * for internal usage.
     * 
     * @param path
     *            relative or absolute path within the classpath
     * @param classLoader
     *            the class loader to load the resource with, if any
     * @param clazz
     *            the class to load resources with, if any
     */
    protected ClassPathResource(String path, ClassLoader classLoader, Class<?> clazz) {
        this.path = StringUtils.cleanPath(path);
        this.classLoader = classLoader;
        this.clazz = clazz;
    }

    /**
     * Return the path for this resource (as resource path within the class
     * path).
     */
    public final String getPath() {
        return this.path;
    }

    /**
     * Return the ClassLoader that this resource will be obtained from.
     */
    public final ClassLoader getClassLoader() {
        return (this.classLoader != null ? this.classLoader : this.clazz.getClassLoader());
    }

    /**
     * This implementation checks for the resolution of a resource URL.
     * 
     * @see java.lang.ClassLoader#getResource(String)
     * @see java.lang.Class#getResource(String)
     */
    @Override
    public boolean exists() {
        URL url;
        if (this.clazz != null) {
            url = this.clazz.getResource(this.path);
        } else {
            url = this.classLoader.getResource(this.path);
        }
        return (url != null);
    }

    /**
     * This implementation opens an InputStream for the given class path
     * resource.
     * 
     * @see java.lang.ClassLoader#getResourceAsStream(String)
     * @see java.lang.Class#getResourceAsStream(String)
     */
    public InputStream getInputStream() throws IOException {
        InputStream is;
        if (this.clazz != null) {
            is = this.clazz.getResourceAsStream(this.path);
        } else {
            is = this.classLoader.getResourceAsStream(this.path);
        }
        if (is == null) {
            throw new FileNotFoundException(getDescription() + " cannot be opened because it does not exist");
        }
        return is;
    }

    /**
     * This implementation returns a URL for the underlying class path resource.
     * 
     * @see java.lang.ClassLoader#getResource(String)
     * @see java.lang.Class#getResource(String)
     */
    @Override
    public URL getURL() throws IOException {
        URL url;
        if (this.clazz != null) {
            url = this.clazz.getResource(this.path);
        } else {
            url = this.classLoader.getResource(this.path);
        }
        if (url == null) {
            throw new FileNotFoundException(getDescription() + " cannot be resolved to URL because it does not exist");
        }
        return url;
    }

    /**
     * This implementation creates a ClassPathResource, applying the given path
     * relative to the path of the underlying resource of this descriptor.
     * 
     * @see org.springframework.util.StringUtils#applyRelativePath(String,
     *      String)
     */
    @Override
    public Resource createRelative(String relativePath) {
        String pathToUse = StringUtils.applyRelativePath(this.path, relativePath);
        return new ClassPathResource(pathToUse, this.classLoader, this.clazz);
    }

    /**
     * This implementation returns the name of the file that this class path
     * resource refers to.
     * 
     * @see org.springframework.util.StringUtils#getFilename(String)
     */
    @Override
    public String getFilename() {
        return StringUtils.getFilename(this.path);
    }

    /**
     * This implementation returns a description that includes the class path
     * location.
     */
    public String getDescription() {
        StringBuilder builder = new StringBuilder("class path resource [");
        String pathToUse = path;
        if (this.clazz != null && !pathToUse.startsWith("/")) {
            builder.append(ClassUtils.classPackageAsResourcePath(this.clazz));
            builder.append('/');
        }
        if (pathToUse.startsWith("/")) {
            pathToUse = pathToUse.substring(1);
        }
        builder.append(pathToUse);
        builder.append(']');
        return builder.toString();
    }

    /**
     * This implementation compares the underlying class path locations.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ClassPathResource) {
            ClassPathResource otherRes = (ClassPathResource) obj;
            return (this.path.equals(otherRes.path)
                    && ObjectUtils.nullSafeEquals(this.classLoader, otherRes.classLoader) && ObjectUtils
                        .nullSafeEquals(this.clazz, otherRes.clazz));
        }
        return false;
    }

    /**
     * This implementation returns the hash code of the underlying class path
     * location.
     */
    @Override
    public int hashCode() {
        return this.path.hashCode();
    }

}
