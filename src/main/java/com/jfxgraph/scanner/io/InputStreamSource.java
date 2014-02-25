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

import java.io.IOException;
import java.io.InputStream;
/**
 * Simple interface for objects that are sources for an {@link InputStream}.
 *
 * <p>This is the base interface for Spring's more extensive {@link Resource} interface.
 *
 * <p>For single-use streams, {@link InputStreamResource} can be used for any
 * given {@code InputStream}. Spring's {@link ByteArrayResource} or any
 * file-based {@code Resource} implementation can be used as a concrete
 * instance, allowing one to read the underlying content stream multiple times.
 * This makes this interface useful as an abstract content source for mail
 * attachments, for example.
 *
 * @author Juergen Hoeller
 * @author Albert
 * @since  4.0 2013年10月25日 上午11:42:19
 * @since 20.01.2004
 * @see java.io.InputStream
 * @see Resource
 * @see InputStreamResource
 * @see ByteArrayResource
 */
public interface InputStreamSource
{
    /**
     * Return an {@link InputStream}.
     * <p>It is expected that each call creates a <i>fresh</i> stream.
     * <p>This requirement is particularly important when you consider an API such
     * as JavaMail, which needs to be able to read the stream multiple times when
     * creating mail attachments. For such a use case, it is <i>required</i>
     * that each {@code getInputStream()} call returns a fresh stream.
     * @return the input stream for the underlying resource (must not be {@code null})
     * @throws IOException if the stream could not be opened
     * @see org.springframework.mail.javamail.MimeMessageHelper#addAttachment(String, InputStreamSource)
     */
    InputStream getInputStream() throws IOException;
}
