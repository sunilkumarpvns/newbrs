# swagger-java-client

## Requirements

Building the API client library requires [Maven](https://maven.apache.org/) to be installed.

## Installation

To install the API client library to your local Maven repository, simply execute:

```shell
mvn install
```

To deploy it to a remote Maven repository instead, configure the settings of the repository and execute:

```shell
mvn deploy
```

Refer to the [official documentation](https://maven.apache.org/plugins/maven-deploy-plugin/usage.html) for more information.

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
    <groupId>io.swagger</groupId>
    <artifactId>swagger-java-client</artifactId>
    <version>1.0.0</version>
    <scope>compile</scope>
</dependency>
```

### Gradle users

Add this dependency to your project's build file:

```groovy
compile "io.swagger:swagger-java-client:1.0.0"
```

### Others

At first generate the JAR by executing:

    mvn package

Then manually install the following JARs:

* target/swagger-java-client-1.0.0.jar
* target/lib/*.jar

## Getting Started

Please follow the [installation](#installation) instruction and execute the following Java code:

```java

import com.brs.searchservice.models.invoker.*;
import com.brs.searchservice.models.invoker.auth.*;
import com.brs.searchservice.models.model.*;
import com.brs.searchservice.models.api.DefaultApi;

import java.io.File;
import java.util.*;

public class DefaultApiExample {

    public static void main(String[] args) {
        
        DefaultApi apiInstance = new DefaultApi();
        String appId = "appId_example"; // String | 
        String appKey = "appKey_example"; // String | 
        String format = "format_example"; // String | 
        String source = "source_example"; // String | 
        String destination = "destination_example"; // String | 
        String dateofdeparture = "dateofdeparture_example"; // String | 
        String dateofarrival = "dateofarrival_example"; // String | 
        String appId2 = "appId_example"; // String | 
        String appKey2 = "appKey_example"; // String | 
        String format2 = "format_example"; // String | 
        String source2 = "source_example"; // String | 
        String destination2 = "destination_example"; // String | 
        String dateofdeparture2 = "dateofdeparture_example"; // String | 
        String dateofarrival2 = "dateofarrival_example"; // String | 
        try {
            InlineResponse200 result = apiInstance.findsearch(appId, appKey, format, source, destination, dateofdeparture, dateofarrival, appId2, appKey2, format2, source2, destination2, dateofdeparture2, dateofarrival2);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DefaultApi#findsearch");
            e.printStackTrace();
        }
    }
}

```

## Documentation for API Endpoints

All URIs are relative to *https://developer.goibibo.com*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*DefaultApi* | [**findsearch**](docs/DefaultApi.md#findsearch) | **GET** /api/bus/search/ | find search 


## Documentation for Models

 - [InlineResponse200](docs/InlineResponse200.md)
 - [InlineResponse200Data](docs/InlineResponse200Data.md)
 - [InlineResponse200DataBPPrims](docs/InlineResponse200DataBPPrims.md)
 - [InlineResponse200DataBPPrimsList](docs/InlineResponse200DataBPPrimsList.md)
 - [InlineResponse200DataDPPrims](docs/InlineResponse200DataDPPrims.md)
 - [InlineResponse200DataDPPrimsList](docs/InlineResponse200DataDPPrimsList.md)
 - [InlineResponse200DataFare](docs/InlineResponse200DataFare.md)
 - [InlineResponse200DataOnwardflights](docs/InlineResponse200DataOnwardflights.md)
 - [InlineResponse200DataReddeal](docs/InlineResponse200DataReddeal.md)
 - [InlineResponse200DataReturnflights](docs/InlineResponse200DataReturnflights.md)
 - [InlineResponse200DataRouteSeatTypeDetail](docs/InlineResponse200DataRouteSeatTypeDetail.md)
 - [InlineResponse200DataRouteSeatTypeDetailList](docs/InlineResponse200DataRouteSeatTypeDetailList.md)
 - [InlineResponse200DataUgcreview](docs/InlineResponse200DataUgcreview.md)
 - [InlineResponse200DataUgcreviewRatings](docs/InlineResponse200DataUgcreviewRatings.md)


## Documentation for Authorization

All endpoints do not require authorization.
Authentication schemes defined for the API:

## Recommendation

It's recommended to create an instance of `ApiClient` per thread in a multithreaded environment to avoid any potential issues.

## Author

apiteam@goibibo.com

