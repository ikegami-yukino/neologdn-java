# Deployment steps memo

## Test

```bash
mvn test
```

## Replace version ID

```bash
sed -i '' -e 's/${OLD_VERSION}/${NEW_VERSION}/g' README.md
```

## GitHub Packages

```bash
mvn deploy
```

## JitPack

```bash
git tag -a v${NEW_VERSION}
git push origin v${NEW_VERSION}
```

## GitHub Pages

```bash
mvn clean site scm-publish:publish-scm
```
