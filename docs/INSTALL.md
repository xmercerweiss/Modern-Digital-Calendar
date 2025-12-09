# Installation Guide
This library is available through [Maven central](https://central.sonatype.com/artifact/net.xmercerweiss/mdc/overview).
To use it in your project, simply add one of the following snippets to the configuration file of your build tool.

Once installed, it's recommended you read over the [quickstart](QUICKSTART.md) to familiarize yourself with this library's uses.

## For Maven...
`pom.xml`
```xml
<project>
    ...
    <dependencies>
        <dependency>
            <groupId>net.xmercerweiss</groupId>
            <artifactId>mdc</artifactId>
            <version>v1.0</version>
        </dependency>
    </dependencies>
    ...
</project>
```

## For Gradle...
`build.gradle`
```
...
repositories {
    mavenCentral() 
}

dependencies {
    implementation 'net.xmercerweiss:mdc:v1.0'
}
...
```