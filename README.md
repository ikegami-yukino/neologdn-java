# neologdn-java
[![Build Status](https://travis-ci.org/ikegami-yukino/neologdn-java.svg?branch=master)](https://travis-ci.org/ikegami-yukino/neologdn-java)

neologdn is a Japanese text normalizer for mecab-neologd.

The normalization is based on the neologd's rules: https://github.com/neologd/mecab-ipadic-neologd/wiki/Regexp.ja

Contributions are welcome!

## Usage

```java
import io.github.ikegamiyukino.neologdn.NeologdNormalizer;


NeologdNormalizer normalizer = new NeologdNormalizer();
String text = "　　　ＰＲＭＬ　　副　読　本　　　";
normalizer.normalize(text);
// => "PRML副読本"
```

## License
Apache Software License.
