/**
 * Copyright (c) 2012-2013,Epic-HUST Technology(Wuhan)Co.,Ltd. All Rights Reserved.
 */
package com.jfxgraph.scanner.type.filter;

import java.util.regex.Pattern;

import com.jfxgraph.scanner.type.ClassMetadata;
import com.jfxgraph.scanner.util.Assert;

/**
 * 正则表达式过滤.
 * 
 * @author Albert
 * @version $Id: RegexPatternTypeFilter.java,v0.5 2013年10月28日 下午2:23:55 Albert Exp .
 * @since 3.1
 */
public class RegexPatternTypeFilter extends AbstractClassTestingTypeFilter
{
    private final Pattern pattern;

    public RegexPatternTypeFilter(Pattern pattern)
    {
        Assert.notNull(pattern, "Pattern must not be null");
        this.pattern = pattern;
    }

    /**
     * @see com.jfxgraph.scanner.type.filter.AbstractClassTestingTypeFilter#match(com.jfxgraph.scanner.type.ClassMetadata)
     */
    @Override
    protected boolean match(ClassMetadata metadata)
    {
        return this.pattern.matcher(metadata.getClassName()).matches();
    }
}
