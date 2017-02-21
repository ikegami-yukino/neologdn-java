package io.github.ikegamiyukino.neologdn;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestNeologdNormalizer {
    NeologdNormalizer normalizer = new NeologdNormalizer();

    @Test
    public void test() {
        assertEquals(normalizer.normalize(" 餃 子 "), "餃子");
        assertEquals(normalizer.normalize("０"), "0");
        assertEquals(normalizer.normalize("ﾊﾝｶｸ"), "ハンカク");
        assertEquals(normalizer.normalize("o₋o"), "o-o");
        assertEquals(normalizer.normalize("majika━"), "majikaー");
        assertEquals(normalizer.normalize("わ〰い"), "わい");
        assertEquals(normalizer.normalize("スーパーーーー"), "スーパー");
        assertEquals(normalizer.normalize("!#"), "!#");
        assertEquals(normalizer.normalize("ゼンカク　スペース"), "ゼンカクスペース");
        assertEquals(normalizer.normalize("お             お"), "おお");
        assertEquals(normalizer.normalize("      おお"), "おお");
        assertEquals(normalizer.normalize("おお      "), "おお");
        assertEquals(normalizer.normalize("検索 エンジン 自作 入門 を 買い ました!!!"), "検索エンジン自作入門を買いました!!!");
        assertEquals(normalizer.normalize("アルゴリズム C"), "アルゴリズムC");
        assertEquals(normalizer.normalize("　　　ＰＲＭＬ　　副　読　本　　　"), "PRML副読本");
        assertEquals(normalizer.normalize("Coding the Matrix"), "Coding the Matrix");
        assertEquals(normalizer.normalize("南アルプスの　天然水　Ｓｐａｒｋｉｎｇ　Ｌｅｍｏｎ　レモン一絞り"),
                "南アルプスの天然水Sparking Lemonレモン一絞り");
        assertEquals(normalizer.normalize("南アルプスの　天然水-　Ｓｐａｒｋｉｎｇ*　Ｌｅｍｏｎ+　レモン一絞り"),
                "南アルプスの天然水- Sparking*Lemon+レモン一絞り");
        assertEquals(normalizer.normalize("ﾊﾟﾊﾟ"), "パパ");
        assertEquals(normalizer.normalize("a˗֊‐‑‒–⁃⁻₋−"), "a-");
        assertEquals(normalizer.normalize("あ﹣－ｰ—―─━ー"), "あー");
        assertEquals(normalizer.normalize("チルダ~∼∾〜〰～"), "チルダ");
        assertEquals(normalizer.normalize("(ﾟ∀ﾟ )"), "(゜∀゜)");
        assertEquals(normalizer.normalize("ﾊﾝｶｸﾀﾞｸﾃﾝ"), "ハンカクダクテン");
    }
}