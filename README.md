[![Build Status](https://travis-ci.org/barakb/aggregate-source.svg?branch=master)](https://travis-ci.org/barakb/aggregate-source)

##aggregate-source

####Use with Maven dependency.

```xml
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>com.mycompany.app</groupId>
                <artifactId>foo</artifactId>
                <version>1.0-SNAPSHOT</version>
            </dependency>
        </dependencies>
    </dependencyManagement>
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
                <include>*:xap-near-cache</include>
            </includes>
        </configuration>
    </plugin>
```


