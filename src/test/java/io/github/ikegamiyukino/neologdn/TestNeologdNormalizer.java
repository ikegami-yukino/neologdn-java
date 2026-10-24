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
        assertEquals(normalizer.normalize("(ﾟ∀ﾟ )"), "(ﾟ∀ﾟ)");
        assertEquals(normalizer.normalize("ﾊﾝｶｸﾀﾞｸﾃﾝ"), "ハンカクダクテン");
    }

    @Test
    public void normalizesVoicingMarksLikePython() {
        assertEquals("ゔぽ", normalizer.normalize("う゛ほﾟ"));
        assertEquals("ガ", normalizer.normalize("ｶ゛"));
        assertEquals("ﾞﾟﾞﾟ", normalizer.normalize("゛゜ﾞﾟ"));
        assertEquals("あﾞ", normalizer.normalize("あﾞ"));
    }

    @Test
    public void usesPythonSymbolMappings() {
        assertEquals("\"\"'`\\＂＇＼｀", normalizer.normalize("“”’‘¥＂＇＼｀"));
        assertEquals("ﾠﾡ￠", normalizer.normalize("ﾠﾡ￠"));
    }

    @Test
    public void handlesSpaceBoundariesLikePython() {
        assertEquals("abc", normalizer.normalize("abc   "));
        assertEquals("A -あ", normalizer.normalize("A ˗あ"));
        assertEquals("A ーあ", normalizer.normalize("A ーあ"));
        assertEquals("Aあ", normalizer.normalize("A ～あ"));
        assertEquals("😀あ", normalizer.normalize("😀 あ"));
        assertEquals("", normalizer.normalize("　 "));
        assertEquals("", normalizer.normalize(""));
    }

    @Test
    public void stripsOneLeadingBomOnlyDuringNormalization() {
        assertEquals("", normalizer.normalize("\uFEFF"));
        assertEquals("a", normalizer.normalize(" \uFEFFa"));
        assertEquals("\uFEFFa", normalizer.normalize("\uFEFF\uFEFFa"));
        assertEquals("a\uFEFF", normalizer.normalize("a\uFEFF"));
        assertEquals("", normalizer.normalize("~\uFEFF"));
        assertEquals("\uFEFF", normalizer.shortenRepeat("\uFEFF", 1));
    }

    @Test
    public void shortensRepeatedSubstrings() {
        assertEquals("うまあああああああい", normalizer.shortenRepeat("うまああああああああああああい", 7, 0));
        assertEquals("かわいいいいいるい", normalizer.shortenRepeat("かわいいいいいるい", 6, 0));
        assertEquals("オラオラーッ", normalizer.shortenRepeat("オラオラオラオラーッ", 2, 0));
        assertEquals("無駄ァ", normalizer.shortenRepeat("無駄無駄無駄無駄ァ", 1, 0));
        assertEquals("😀", normalizer.shortenRepeat("😀😀😀😀", 1));
        assertEquals("𠮷😀!", normalizer.shortenRepeat("𠮷😀𠮷😀𠮷😀!", 1));
        assertEquals("", normalizer.shortenRepeat("", 1));
        assertEquals("aa", normalizer.shortenRepeat("aa", 1));
        assertEquals("aaa", normalizer.shortenRepeat("aaa", 1));
        assertEquals("abab", normalizer.shortenRepeat("abab", 1));
        assertEquals("ab", normalizer.shortenRepeat("ababab", 1));
        assertEquals("", normalizer.shortenRepeat("aaaa", 0));
    }

    @Test
    public void limitsRepeatedSubstringLength() {
        String phrase = "隣の客はよく柿食う客だ、";
        String repeated = phrase + phrase + phrase + "言えた！";
        assertEquals(phrase + "言えた！", normalizer.shortenRepeat(repeated, 1, 0));
        assertEquals(repeated, normalizer.shortenRepeat(repeated, 1, 11));
        assertEquals(repeated, normalizer.shortenRepeat(repeated, 1));
        assertEquals("abcdefgh!", normalizer.shortenRepeat("abcdefghabcdefghabcdefgh!", 1));
        assertEquals("abcdefghabcdefghabcdefgh!", normalizer.shortenRepeat("abcdefghabcdefghabcdefgh!", 1, 7));
        assertEquals("abc!", normalizer.shortenRepeat("abcabcabc!", 1, Integer.MAX_VALUE));
    }

    @Test
    public void normalizesBeforeShortening() {
        assertEquals("無駄ァ", normalizer.normalize("無駄無駄無駄無駄ァ", 1));
        assertEquals("ガ!", normalizer.normalize("ｶﾞガｶﾞガ！", 1));
        assertEquals("a b a b", normalizer.normalize("a b a b", 1, false));
        assertEquals("巴 マミ", normalizer.normalize("巴 マミ", false));
        assertEquals("  巴 マミ ", normalizer.normalize("  巴　マミ  ", false));
        assertEquals("abc  ", normalizer.normalize("abc   ", false));
        assertEquals("😀 あ", normalizer.normalize("😀 あ", false));
        assertEquals("無駄無駄無駄ァ", normalizer.normalize("無駄無駄無駄ァ", 1, true, 1));
        assertEquals("無駄ァ", normalizer.normalize("無駄無駄無駄ァ", 1, true, 0));
        assertEquals("aaaa", normalizer.normalize("aaaa", 0));
    }

    @Test
    public void supportsEveryTildeMode() {
        assertEquals("1467~1487年", normalizer.normalize("1467〜1487年", "normalize"));
        assertEquals("1467〜1487年", normalizer.normalize("1467~1487年", "normalize_zenkaku"));
        assertEquals("1467〜1487年", normalizer.normalize("1467〜1487年", "ignore"));
        assertEquals("14671487年", normalizer.normalize("1467〜1487年", "remove"));
        assertEquals("14671487年", normalizer.normalize("1467〜1487年", "unknown"));
        assertEquals("~∼∾〜〰～", normalizer.normalize("~∼∾〜〰～", "ignore"));
        assertEquals("~~~~~~", normalizer.normalize("~∼∾〜〰～", "normalize"));
        assertEquals("〜〜〜〜〜〜", normalizer.normalize("~∼∾〜〰～", "normalize_zenkaku"));
        assertEquals("A ~あ", normalizer.normalize("A ～あ", "normalize"));
        assertEquals("A 〜あ", normalizer.normalize("A ~あ", "normalize_zenkaku"));
        assertEquals("~", normalizer.normalize("~∼∾〜〰～", 1, true, 8, "normalize"));
    }

    @Test
    public void rejectsNegativeRepeatParameters() {
        assertThrows(IllegalArgumentException.class, () -> normalizer.normalize("abc", -1));
        assertThrows(IllegalArgumentException.class, () -> normalizer.normalize("abc", 1, true, -1));
        assertThrows(IllegalArgumentException.class, () -> normalizer.shortenRepeat("abc", -1));
        assertThrows(IllegalArgumentException.class, () -> normalizer.shortenRepeat("abc", 1, -1));
    }
}
