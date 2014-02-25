package com.jfxgraph.scanner.util;

/**
 * 基于字符串的路径匹配策略接口
 * 
 * <p>{@link AntPathMatcher}是此接口的唯一默认实现：支持Ant风格的路径模式匹配语法。
 * 注：参考Spring.
 * @author    Albert
 * @since   1.0
 */
public interface PathMatcher
{
    /**
     * Does the given {@code path} represent a pattern that can be matched
     * by an implementation of this interface?<br>
     * 给定路径是否代表一种可以被这个接口的某个实现匹配的模式？
     * <p>
     * 如果返回值为 {@code false}，则不需要使用{@link #match}方法，因为纯粹的等值比较静态路径字符串将返回一样的结果。
     * 
     * @param path 要检查的路径字符串
     * @return {@code true} 如果给定的路径可以代表一种模式
     */
    boolean isPattern(String path);

    /**
     * 根据路径匹配器的匹配策略 match 给定的路径against给定的{@code pattern}。
     * <p>
     * Match the given {@code path} against the given {@code pattern},
     * according to this PathMatcher's matching strategy.
     * @param pattern the pattern to match against
     * @param path the path String to test
     * @return {@code true} if the supplied {@code path} matched,
     * {@code false} if it didn't
     */
    boolean match(String pattern, String path);

    /**
     * Match the given {@code path} against the corresponding part of the given
     * {@code pattern}, according to this PathMatcher's matching strategy.
     * <p>Determines whether the pattern at least matches as far as the given base
     * path goes, assuming that a full path may then match as well.
     * @param pattern the pattern to match against
     * @param path the path String to test
     * @return {@code true} if the supplied {@code path} matched,
     * {@code false} if it didn't
     */
    boolean matchStart(String pattern, String path);

    /**
     * Given a pattern and a full path, determine the pattern-mapped part.
     * <p>This method is supposed to find out which part of the path is matched
     * dynamically through an actual pattern, that is, it strips off a statically
     * defined leading path from the given full path, returning only the actually
     * pattern-matched part of the path.
     * <p>For example: For "myroot/*.html" as pattern and "myroot/myfile.html"
     * as full path, this method should return "myfile.html". The detailed
     * determination rules are specified to this PathMatcher's matching strategy.
     * <p>A simple implementation may return the given full path as-is in case
     * of an actual pattern, and the empty String in case of the pattern not
     * containing any dynamic parts (i.e. the {@code pattern} parameter being
     * a static path that wouldn't qualify as an actual {@link #isPattern pattern}).
     * A sophisticated implementation will differentiate between the static parts
     * and the dynamic parts of the given path pattern.
     * @param pattern the path pattern
     * @param path the full path to introspect
     * @return the pattern-mapped part of the given {@code path}
     * (never {@code null})
     */
    String extractPathWithinPattern(String pattern, String path);
}
