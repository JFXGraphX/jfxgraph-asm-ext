/**
 * Copyright (c) 2012-2013,Epic-HUST Technology(Wuhan)Co.,Ltd. All Rights Reserved.
 */
package com.jfxgraph.scanner.util;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Ant-style 路径匹配模式实现.
 * <p>
 * 匹配规则如下:<br>
 * <ul>
 * <li>? 匹配任何单字符</li>
 * <li>* 匹配0或者任意数量的字符</li>
 * <li>** 匹配0或者更多的目录</li>
 * </ul>
 * <p>
 * 样例参考:<br>
 * <ul>
 * <li>{@code com/t?st.jsp} - matches {@code com/test.jsp} but also {@code com/tast.jsp} or {@code com/txst.jsp}</li>
 * <li>{@code com/*.jsp} - matches all {@code .jsp} files in the {@code com} directory</li>
 * <li>{@code com/&#42;&#42;/test.jsp} - matches all {@code test.jsp} files underneath the {@code com} path</li>
 * <li>{@code org/springframework/&#42;&#42;/*.jsp} - matches all {@code .jsp} files underneath the
 * {@code org/springframework} path</li>
 * <li>{@code org/&#42;&#42;/servlet/bla.jsp} - matches {@code org/springframework/servlet/bla.jsp} but also
 * {@code org/springframework/testing/servlet/bla.jsp} and {@code org/servlet/bla.jsp}</li>
 * </ul>
 * 注：参考自SpringFramework.
 * 
 * @author Albert
 * @version $Id: AntPathMatcher.java,v0.5 2013年10月27日 下午7:52:47 Albert Exp .
 * @since 1.0
 */
public class AntPathMatcher implements PathMatcher
{
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{[^/]+?\\}");

    /** Default path separator: "/" */
    public static final String DEFAULT_PATH_SEPARATOR = "/";

    private String pathSeparator = DEFAULT_PATH_SEPARATOR;

    private final Map<String, AntPathStringMatcher> stringMatcherCache = new ConcurrentHashMap<String, AntPathStringMatcher>(256);

    private boolean trimTokens = true;

    /** Set the path separator to use for pattern parsing. Default is "/", as in Ant. */
    public void setPathSeparator(String pathSeparator)
    {
        this.pathSeparator = (pathSeparator != null ? pathSeparator : DEFAULT_PATH_SEPARATOR);
    }

    /** Whether to trim tokenized paths and patterns. */
    public void setTrimTokens(boolean trimTokens)
    {
        this.trimTokens = trimTokens;
    }

    /**
     * @see com.jfxgraph.scanner.util.PathMatcher#isPattern(java.lang.String)
     */
    @Override
    public boolean isPattern(String path)
    {
        return (path.indexOf('*') != -1 || path.indexOf('?') != -1);
    }

    /**
     * @see com.jfxgraph.scanner.util.PathMatcher#match(java.lang.String, java.lang.String)
     */
    @Override
    public boolean match(String pattern, String path)
    {
        return doMatch(pattern, path, true, null);
    }

    /**
     * @see com.jfxgraph.scanner.util.PathMatcher#matchStart(java.lang.String, java.lang.String)
     */
    @Override
    public boolean matchStart(String pattern, String path)
    {
        return doMatch(pattern, path, false, null);
    }

    /**
     * Given a pattern and a full path, determine the pattern-mapped part.
     * <p>
     * For example:
     * <ul>
     * <li>'{@code /docs/cvs/commit.html}' and '{@code /docs/cvs/commit.html} -> ''</li>
     * <li>'{@code /docs/*}' and '{@code /docs/cvs/commit} -> '{@code cvs/commit}'</li>
     * <li>'{@code /docs/cvs/*.html}' and '{@code /docs/cvs/commit.html} -> '{@code commit.html}'</li>
     * <li>'{@code /docs/**}' and '{@code /docs/cvs/commit} -> '{@code cvs/commit}'</li>
     * <li>'{@code /docs/**\/*.html}' and '{@code /docs/cvs/commit.html} -> '{@code cvs/commit.html}'</li>
     * <li>'{@code /*.html}' and '{@code /docs/cvs/commit.html} -> '{@code docs/cvs/commit.html}'</li>
     * <li>'{@code *.html}' and '{@code /docs/cvs/commit.html} -> '{@code /docs/cvs/commit.html}'</li>
     * <li>'{@code *}' and '{@code /docs/cvs/commit.html} -> '{@code /docs/cvs/commit.html}'</li>
     * </ul>
     * <p>
     * Assumes that {@link #match} returns {@code true} for '{@code pattern}' and '{@code path}', but does
     * <strong>not</strong> enforce this.
     * 
     * @see com.jfxgraph.scanner.util.PathMatcher#extractPathWithinPattern(java.lang.String, java.lang.String)
     */
    @Override
    public String extractPathWithinPattern(String pattern, String path)
    {
        String[] patternParts = StringUtils.tokenizeToStringArray(pattern, this.pathSeparator, this.trimTokens, true);
        String[] pathParts = StringUtils.tokenizeToStringArray(path, this.pathSeparator, this.trimTokens, true);

        StringBuilder builder = new StringBuilder();

        // Add any path parts that have a wildcarded pattern part.
        int puts = 0;
        for (int i = 0; i < patternParts.length; i++)
        {
            String patternPart = patternParts[i];
            if ((patternPart.indexOf('*') > -1 || patternPart.indexOf('?') > -1) && pathParts.length >= i + 1)
            {
                if (puts > 0 || (i == 0 && !pattern.startsWith(this.pathSeparator)))
                {
                    builder.append(this.pathSeparator);
                }
                builder.append(pathParts[i]);
                puts++;
            }
        }

        // Append any trailing path parts.
        for (int i = patternParts.length; i < pathParts.length; i++)
        {
            if (puts > 0 || i > 0)
            {
                builder.append(this.pathSeparator);
            }
            builder.append(pathParts[i]);
        }

        return builder.toString();
    }

    /**
     * Actually match the given {@code path} against the given {@code pattern}.
     * 
     * @param pattern
     *            the pattern to match against
     * @param path
     *            the path String to test
     * @param fullMatch
     *            whether a full pattern match is required (else a pattern match as far as the given base path goes is
     *            sufficient)
     * @return {@code true} if the supplied {@code path} matched, {@code false} if it didn't
     */
    protected boolean doMatch(String pattern, String path, boolean fullMatch, Map<String, String> uriTemplateVariables)
    {

        if (path.startsWith(this.pathSeparator) != pattern.startsWith(this.pathSeparator))
        {
            return false;
        }

        String[] pattDirs = StringUtils.tokenizeToStringArray(pattern, this.pathSeparator, this.trimTokens, true);
        String[] pathDirs = StringUtils.tokenizeToStringArray(path, this.pathSeparator, this.trimTokens, true);

        int pattIdxStart = 0;
        int pattIdxEnd = pattDirs.length - 1;
        int pathIdxStart = 0;
        int pathIdxEnd = pathDirs.length - 1;

        // Match all elements up to the first **
        while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd)
        {
            String patDir = pattDirs[pattIdxStart];
            if ("**".equals(patDir))
            {
                break;
            }
            if (!matchStrings(patDir, pathDirs[pathIdxStart], uriTemplateVariables))
            {
                return false;
            }
            pattIdxStart++;
            pathIdxStart++;
        }

        if (pathIdxStart > pathIdxEnd)
        {
            // Path is exhausted, only match if rest of pattern is * or **'s
            if (pattIdxStart > pattIdxEnd)
            {
                return (pattern.endsWith(this.pathSeparator) ? path.endsWith(this.pathSeparator) : !path.endsWith(this.pathSeparator));
            }
            if (!fullMatch)
            {
                return true;
            }
            if (pattIdxStart == pattIdxEnd && pattDirs[pattIdxStart].equals("*") && path.endsWith(this.pathSeparator))
            {
                return true;
            }
            for (int i = pattIdxStart; i <= pattIdxEnd; i++)
            {
                if (!pattDirs[i].equals("**"))
                {
                    return false;
                }
            }
            return true;
        } else if (pattIdxStart > pattIdxEnd)
        {
            // String not exhausted, but pattern is. Failure.
            return false;
        } else if (!fullMatch && "**".equals(pattDirs[pattIdxStart]))
        {
            // Path start definitely matches due to "**" part in pattern.
            return true;
        }

        // up to last '**'
        while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd)
        {
            String patDir = pattDirs[pattIdxEnd];
            if (patDir.equals("**"))
            {
                break;
            }
            if (!matchStrings(patDir, pathDirs[pathIdxEnd], uriTemplateVariables))
            {
                return false;
            }
            pattIdxEnd--;
            pathIdxEnd--;
        }
        if (pathIdxStart > pathIdxEnd)
        {
            // String is exhausted
            for (int i = pattIdxStart; i <= pattIdxEnd; i++)
            {
                if (!pattDirs[i].equals("**"))
                {
                    return false;
                }
            }
            return true;
        }

        while (pattIdxStart != pattIdxEnd && pathIdxStart <= pathIdxEnd)
        {
            int patIdxTmp = -1;
            for (int i = pattIdxStart + 1; i <= pattIdxEnd; i++)
            {
                if (pattDirs[i].equals("**"))
                {
                    patIdxTmp = i;
                    break;
                }
            }
            if (patIdxTmp == pattIdxStart + 1)
            {
                // '**/**' situation, so skip one
                pattIdxStart++;
                continue;
            }
            // Find the pattern between padIdxStart & padIdxTmp in str between
            // strIdxStart & strIdxEnd
            int patLength = (patIdxTmp - pattIdxStart - 1);
            int strLength = (pathIdxEnd - pathIdxStart + 1);
            int foundIdx = -1;

            strLoop: for (int i = 0; i <= strLength - patLength; i++)
            {
                for (int j = 0; j < patLength; j++)
                {
                    String subPat = pattDirs[pattIdxStart + j + 1];
                    String subStr = pathDirs[pathIdxStart + i + j];
                    if (!matchStrings(subPat, subStr, uriTemplateVariables))
                    {
                        continue strLoop;
                    }
                }
                foundIdx = pathIdxStart + i;
                break;
            }

            if (foundIdx == -1)
            {
                return false;
            }

            pattIdxStart = patIdxTmp;
            pathIdxStart = foundIdx + patLength;
        }

        for (int i = pattIdxStart; i <= pattIdxEnd; i++)
        {
            if (!pattDirs[i].equals("**"))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Tests whether or not a string matches against a pattern. The pattern may contain two special characters: <br>
     * '*' means zero or more characters <br>
     * '?' means one and only one character
     * 
     * @param pattern
     *            pattern to match against. Must not be {@code null}.
     * @param str
     *            string which must be matched against the pattern. Must not be {@code null}.
     * @return {@code true} if the string matches against the pattern, or {@code false} otherwise.
     */
    private boolean matchStrings(String pattern, String str, Map<String, String> uriTemplateVariables)
    {
        AntPathStringMatcher matcher = this.stringMatcherCache.get(pattern);
        if (matcher == null)
        {
            matcher = new AntPathStringMatcher(pattern);
            this.stringMatcherCache.put(pattern, matcher);
        }
        return matcher.matchStrings(str, uriTemplateVariables);
    }

    /**
     * Given a full path, returns a {@link Comparator} suitable for sorting patterns in order of explicitness.
     * <p>
     * The returned {@code Comparator} will
     * {@linkplain java.util.Collections#sort(java.util.List, java.util.Comparator) sort} a list so that more specific
     * patterns (without uri templates or wild cards) come before generic patterns. So given a list with the following
     * patterns:
     * <ol>
     * <li>{@code /hotels/new}</li>
     * <li>{@code /hotels/ hotel}}</li>
     * <li>{@code /hotels/*}</li>
     * </ol>
     * the returned comparator will sort this list so that the order will be as indicated.
     * <p>
     * The full path given as parameter is used to test for exact matches. So when the given path is {@code /hotels/2},
     * the pattern {@code /hotels/2} will be sorted before {@code /hotels/1}.
     * 
     * @param path
     *            the full path to use for comparison
     * @return a comparator capable of sorting patterns in order of explicitness
     */
    public Comparator<String> getPatternComparator(String path)
    {
        return new AntPatternComparator(path);
    }

    private static class AntPatternComparator implements Comparator<String>
    {

        private final String path;

        private AntPatternComparator(String path)
        {
            this.path = path;
        }

        public int compare(String pattern1, String pattern2)
        {
            if (pattern1 == null && pattern2 == null)
            {
                return 0;
            } else if (pattern1 == null)
            {
                return 1;
            } else if (pattern2 == null)
            {
                return -1;
            }
            boolean pattern1EqualsPath = pattern1.equals(path);
            boolean pattern2EqualsPath = pattern2.equals(path);
            if (pattern1EqualsPath && pattern2EqualsPath)
            {
                return 0;
            } else if (pattern1EqualsPath)
            {
                return -1;
            } else if (pattern2EqualsPath)
            {
                return 1;
            }
            int wildCardCount1 = getWildCardCount(pattern1);
            int wildCardCount2 = getWildCardCount(pattern2);

            int bracketCount1 = StringUtils.countOccurrencesOf(pattern1, "{");
            int bracketCount2 = StringUtils.countOccurrencesOf(pattern2, "{");

            int totalCount1 = wildCardCount1 + bracketCount1;
            int totalCount2 = wildCardCount2 + bracketCount2;

            if (totalCount1 != totalCount2)
            {
                return totalCount1 - totalCount2;
            }

            int pattern1Length = getPatternLength(pattern1);
            int pattern2Length = getPatternLength(pattern2);

            if (pattern1Length != pattern2Length)
            {
                return pattern2Length - pattern1Length;
            }

            if (wildCardCount1 < wildCardCount2)
            {
                return -1;
            } else if (wildCardCount2 < wildCardCount1)
            {
                return 1;
            }

            if (bracketCount1 < bracketCount2)
            {
                return -1;
            } else if (bracketCount2 < bracketCount1)
            {
                return 1;
            }

            return 0;
        }

        private int getWildCardCount(String pattern)
        {
            if (pattern.endsWith(".*"))
            {
                pattern = pattern.substring(0, pattern.length() - 2);
            }
            return StringUtils.countOccurrencesOf(pattern, "*");
        }

        /**
         * Returns the length of the given pattern, where template variables are considered to be 1 long.
         */
        private int getPatternLength(String pattern)
        {
            Matcher m = VARIABLE_PATTERN.matcher(pattern);
            return m.replaceAll("#").length();
        }
    }

    /**
     * Tests whether or not a string matches against a pattern via a {@link Pattern}.
     * <p>
     * The pattern may contain special characters: '*' means zero or more characters; '?' means one and only one
     * character; '{' and '}' indicate a URI template pattern. For example <tt>/users/{user}</tt>.
     */
    private static class AntPathStringMatcher
    {

        private static final Pattern GLOB_PATTERN = Pattern.compile("\\?|\\*|\\{((?:\\{[^/]+?\\}|[^/{}]|\\\\[{}])+?)\\}");

        private static final String DEFAULT_VARIABLE_PATTERN = "(.*)";

        private final Pattern pattern;

        private final List<String> variableNames = new LinkedList<String>();

        public AntPathStringMatcher(String pattern)
        {
            StringBuilder patternBuilder = new StringBuilder();
            Matcher m = GLOB_PATTERN.matcher(pattern);
            int end = 0;
            while (m.find())
            {
                patternBuilder.append(quote(pattern, end, m.start()));
                String match = m.group();
                if ("?".equals(match))
                {
                    patternBuilder.append('.');
                } else if ("*".equals(match))
                {
                    patternBuilder.append(".*");
                } else if (match.startsWith("{") && match.endsWith("}"))
                {
                    int colonIdx = match.indexOf(':');
                    if (colonIdx == -1)
                    {
                        patternBuilder.append(DEFAULT_VARIABLE_PATTERN);
                        this.variableNames.add(m.group(1));
                    } else
                    {
                        String variablePattern = match.substring(colonIdx + 1, match.length() - 1);
                        patternBuilder.append('(');
                        patternBuilder.append(variablePattern);
                        patternBuilder.append(')');
                        String variableName = match.substring(1, colonIdx);
                        this.variableNames.add(variableName);
                    }
                }
                end = m.end();
            }
            patternBuilder.append(quote(pattern, end, pattern.length()));
            this.pattern = Pattern.compile(patternBuilder.toString());
        }

        private String quote(String s, int start, int end)
        {
            if (start == end)
            {
                return "";
            }
            return Pattern.quote(s.substring(start, end));
        }

        /**
         * Main entry point.
         * 
         * @return {@code true} if the string matches against the pattern, or {@code false} otherwise.
         */
        public boolean matchStrings(String str, Map<String, String> uriTemplateVariables)
        {
            Matcher matcher = this.pattern.matcher(str);
            if (matcher.matches())
            {
                if (uriTemplateVariables != null)
                {
                    // SPR-8455
                    Assert.isTrue(this.variableNames.size() == matcher.groupCount(), "The number of capturing groups in the pattern segment " + this.pattern
                            + " does not match the number of URI template variables it defines, which can occur if "
                            + " capturing groups are used in a URI template regex. Use non-capturing groups instead.");
                    for (int i = 1; i <= matcher.groupCount(); i++)
                    {
                        String name = this.variableNames.get(i - 1);
                        String value = matcher.group(i);
                        uriTemplateVariables.put(name, value);
                    }
                }
                return true;
            } else
            {
                return false;
            }
        }
    }
}
