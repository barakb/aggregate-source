![Build Status](https://travis-ci.org/barakb/aggregate-source.svg?branch=master)(https://travis-ci.org/barakb/aggregate-source)

##aggregate-source

####Use with Maven dependency.

```xml
    <dependency>
      <groupId>com.github.barakb</groupId>
      <artifactId>aggregate-source</artifactId>
      <version>1.2</version>
    </dependency>
    
```

```xml
    <plugin>
        <groupId>com.github.barakb</groupId>
        <artifactId>aggregate-source</artifactId>
        <version>1.2</version>
        <executions>
            <execution>
                <id>aggregate-javadoc</id>
                <phase>install</phase>
                <goals>
                    <goal>aggregate</goal>
                </goals>
            </execution>
        </executions>
        <configuration>
            <includes>
                <include>*:xap-map</include>
                <include>*:xap-map-spring</include>
                <include>*:xap-spatial</include>
                <include>*:xap-openspaces</include>
                <include>*:xap-rest</include>
                <include>*:xap-jms</include>
                <include>*:xap-rest-spring</include>
                <include>*:xap-common</include>
                <include>*:xap-datagrid</include>
            </includes>
        </configuration>
    </plugin>
```


