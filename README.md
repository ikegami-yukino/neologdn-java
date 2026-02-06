# neologdn-java

neologdn is a Japanese text normalizer for mecab-neologd.

The normalization is based on the neologd's rules: https://github.com/neologd/mecab-ipadic-neologd/wiki/Regexp.ja

Contributions are welcome!

## Installation

neologdn has 2 ways to installation.

### JitPack

#### maven

Add the following tags to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.ikegami-yukino</groupId>
    <artifactId>neologdn-java</artifactId>
    <version>v0.0.2</version>
</dependency>
```

#### gradle

Add it in your root `settings.gradle` at the end of repositories:

```groovy
	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
```

Add the dependency

```groovy
	dependencies {
	        implementation 'com.github.ikegami-yukino:neologdn-java:v0.0.2'
	}
```

### GitHub Packages

#### maven

Add the following tags to your `pom.xml`:

```xml
<project>
    <dependencies>
        <dependency>
            <groupId>com.github.ikegami-yukino</groupId>
            <artifactId>neologdn-java</artifactId>
            <version>0.0.2</version>
        </dependency>
    </dependencies>

    <repositories>
        <repository>
            <id>github</id>
            <name>GitHub Packages</name>
            <url>https://maven.pkg.github.com/ikegami-yukino/neologdn-java</url>
        </repository>
    </repositories>
</project>
```

and maybe also edit your `~/.m2/settings.xml`.

```xml
<settings>
    <servers>
        <server>
            <id>github</id>
            <username>Your GitHub name</username>
            <password>Your Personal Access Token</password>
        </server>
    </servers>
</settings>
```

#### gradle

Add it in your root `settings.gradle` at the end of repositories:

```groovy
repositories {
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/ikegami-yukino/neologdn-java")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.key") ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation 'groupId:artifactId:version'
}
```

Add GitHub info to `~/.gradle/gradle.properties`:

```groovy
gpr.user=Your GitHub account name
gpr.key=Your Personal Access Token (classic)
```

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
