
### Spring Boot | Spring Cloud

## Exploring Resilience4j: Enhancing Circuit Breaker Patterns for Robust Applications

![Photo by [Alex Shute](https://unsplash.com/@faithgiant?utm_source=unsplash&utm_medium=referral&utm_content=creditCopyText) on [Unsplash](https://unsplash.com/photos/4lA1sDFr8Y8?utm_source=unsplash&utm_medium=referral&utm_content=creditCopyText)](https://cdn-images-1.medium.com/max/10814/1*-YeCcgzTEcQDAZAd_aP9cQ.jpeg)

In today’s interconnected world, where applications are expected to handle increasing loads and maintain high availability, building resilient systems is crucial. One of the key challenges in creating robust applications lies in handling failures and mitigating their impact. This is where circuit breakers come into play. Circuit breakers act as a protective shield, isolating faulty services and preventing cascading failures across the system.

In this article, we delve into the world of circuit breakers and explore the powerful capabilities of Resilience4j, a popular resilience library for Java applications. Resilience4j provides a comprehensive set of tools and patterns that enable developers to implement resilient solutions effectively.

Throughout this article, we will unravel the concept of circuit breakers and explain how Resilience4j enhances their functionality. We will examine the various strategies and configurations available in Resilience4j that can be leveraged to build fault-tolerant systems. Additionally, we will discuss best practices and implementation tips to maximize the benefits of Resilience4j in your applications.

So, let’s embark on this journey into the world of circuit breakers and discover how Resilience4j can help us build resilient systems that gracefully handle failures and maintain high performance.

## What is a circuit breaker ?

A circuit breaker is a software design pattern used in distributed systems to improve fault tolerance and resilience. Inspired by the electrical circuit breaker, which protects electrical circuits from damage caused by excessive current, the software circuit breaker acts as a safety mechanism for services or components within an application.

The primary purpose of a circuit breaker is to prevent cascading failures in a distributed system. When a service or component fails or experiences abnormal behavior, such as slow response times or errors, the circuit breaker trips and starts redirecting calls to a predefined fallback mechanism or returns an error response directly. By doing so, it isolates the faulty component from the rest of the system, limiting the impact of the failure and allowing the system to gracefully degrade.

A circuit breaker operates based on a set of predefined conditions and thresholds. It monitors the number of failures or errors that occur within a certain time period and compares them against configured thresholds. If the error rate exceeds a threshold, the circuit breaker trips, indicating that the component or service is in a problematic state. While the circuit breaker is open, subsequent requests are not forwarded to the failing component, reducing the load on the component and preventing further damage.

In addition to fault isolation, circuit breakers also provide mechanisms for self-healing. After a specified period of time, the circuit breaker allows a limited number of requests to be sent to the failing component to check if it has recovered. If those requests are successful, the circuit breaker closes again, allowing normal operations to resume. If the requests continue to fail, the circuit breaker remains open, preventing further damage and allowing the system to adapt accordingly.
>  To illustrate the concept of a circuit breaker, let’s consider a practical example. Imagine you have an API endpoint `/countries` in your application that fetches a list of countries. To retrieve this data, your application relies on an external API, let’s say `[https://restcountries.com/v3.1/all`](https://restcountries.com/v3.1/all`). However, if this external API goes down or becomes unavailable, it would result in errors being thrown by your application.

![Example : Circuit breaker functionality](https://cdn-images-1.medium.com/max/2000/1*4z0bHBT-pyE8ApT-TnDTNA.png)

By implementing a circuit breaker pattern, you can mitigate the impact of such failures and introduce a fallback mechanism. Here’s how it works:

1. Normal Operation:
During normal operation, when the external API is up and running, the circuit breaker remains in a closed state, allowing requests to flow through seamlessly. Your application retrieves the list of countries from the external API and serves the data to the users.

2. Failure Detection:
If the external API experiences issues and starts responding with errors or becomes unresponsive, the circuit breaker detects this abnormal behavior by monitoring the failure rate or response times. Once the predefined threshold is exceeded, the circuit breaker trips, transitioning to an open state.

3. Fallback Mechanism:
When the circuit breaker is in the open state, instead of allowing requests to reach the failing external API, it redirects them to a fallback method or a pre-defined response. In the context of our example, the fallback method could be retrieving a cached version of the country list or providing a default list.

4. Automatic Recovery:
To periodically check if the external API has recovered, the circuit breaker allows a limited number of requests to pass through after a certain duration. If these requests are successful, indicating that the external API is functioning again, the circuit breaker transitions back to the closed state, resuming normal operations. However, if the requests continue to fail, the circuit breaker remains open to prevent further damage.

By incorporating a circuit breaker, you can ensure that your application gracefully handles the failure of the external API and provides a fallback mechanism to maintain functionality even in the absence of external data. This not only improves the resilience of your application but also enhances the user experience by mitigating the impact of external service failures.

Additionally, circuit breakers offer configurability to define thresholds, timeouts, and other parameters, allowing you to fine-tune the behavior based on your specific requirements. They provide a valuable tool in building robust and reliable systems that can adapt to changing conditions and ensure uninterrupted service delivery.

Next, we will explore how to develop a Spring Boot application and incorporate Resilience4j to implement a circuit breaker.

## LETS GET STARTED !

Lets head to [https://start.spring.io/](https://start.spring.io/) and configure the project as shown below :

![Project setup](https://cdn-images-1.medium.com/max/3068/1*WtDhLHS9lQreMD6eJTilVw.png)

We are going to be using Java 17 for this project , The dependencies that we are going to be using are :

 1. Spring Web

 2. Spring Boot Actuator

Now lets open the project in the ide of your choice. I am going to be using Intellij Idea.

After setting up the project as discussed in the previous example, I have created a CountriesController in my Spring Boot application. This controller is responsible for handling the /countries endpoint, which retrieves the list of countries from an external API.

In the CountriesController class, I have implemented the necessary logic to make the API call to the external API endpoint (https://restcountries.com/v3.1/all) and fetch the list of countries. I have used the RestTemplate class from Spring to handle the HTTP request and retrieve the response.

By invoking the /countries API endpoint, you will receive the list of countries as a response. The source code is available [here.](https://github.com/MaheshBabu11/CircuitBreaker)

![](https://cdn-images-1.medium.com/max/3830/1*Fn8wA874im8yABNg5Kh48A.png)

Now, let’s make a request to the /countries API endpoint and observe the outcome. We get the list of countries as shown below.

![Expected application response](https://cdn-images-1.medium.com/max/3770/1*lpvyid5jcBwYCI1cnVyGZA.png)

Now, let’s see what happesns when https://restcountries.com/v3.1/all API is unavailable. In such a case, instead of receiving the expected response, our application will encounter an error and display a generic "white label" error page.

![White label error when the external api is down](https://cdn-images-1.medium.com/max/3804/1*eLZrTMNiBOCMGHsdIdC-WQ.png)

Now, let’s delve into the benefits of incorporating a circuit breaker to avoid encountering errors, such as the “white label” error page, when the https://restcountries.com/v3.1/all API becomes unavailable.

Lets configure the circuit breaker in our application, for that lets add these dependencies.

    <dependency>
       <groupId>io.github.resilience4j</groupId>
       <artifactId>resilience4j-spring-boot3</artifactId>
       <version>2.0.2</version>
     </dependency>
     <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-aop</artifactId>
     </dependency>

Next, we’ll configure the application to utilize the circuit breaker by making the necessary configurations in the applications.yml file.

    spring:
      application.name: CircuitBreakerDemo
      jackson.serialization.indent_output: true
    
    management:
      endpoints.web.exposure.include:
        - '*'
      endpoint.health.show-details: always
      health.circuitbreakers.enabled: true
    
    resilience4j.circuitbreaker:
      configs:
        default:
          registerHealthIndicator: true
          slidingWindowSize: 10
          minimumNumberOfCalls: 5
          permittedNumberOfCallsInHalfOpenState: 3
          automaticTransitionFromOpenToHalfOpenEnabled: true
          waitDurationInOpenState: 5s
          failureRateThreshold: 50
          eventConsumerBufferSize: 10

Given below is a simple explanation of parameters that we have configured.

* resilience4j.circuitbreaker: This specifies the configuration for the circuit breaker module of Resilience4j.

* configs: This defines the different circuit breaker configurations. In this case, there is a single configuration named "default".

* registerHealthIndicator: This parameter determines whether to register a health indicator for the circuit breaker. It allows monitoring the circuit breaker's health status.

* slidingWindowSize: This sets the size of the sliding window used by the circuit breaker to track the success and failure rates of calls.

* minimumNumberOfCalls: This specifies the minimum number of calls required within the sliding window before the circuit breaker can calculate the success or failure rate.

* permittedNumberOfCallsInHalfOpenState: This sets the maximum number of calls allowed when the circuit breaker is in the half-open state. If this limit is exceeded, the circuit breaker transitions back to the open state.

* automaticTransitionFromOpenToHalfOpenEnabled: This parameter enables or disables automatic transition from the open state to the half-open state when the wait duration in the open state has passed.

* waitDurationInOpenState: This determines the duration that the circuit breaker remains in the open state before transitioning to the half-open state. In this case, it is set to 5 seconds.

* failureRateThreshold: This sets the failure rate threshold in percentage. If the failure rate exceeds this threshold within the sliding window, the circuit breaker transitions to the open state.

* eventConsumerBufferSize: This parameter determines the size of the buffer used by the event consumer for tracking circuit breaker events.

Now lets add the circuit breaker to our code and see it in action.

    @GetMapping("/countries")
        @CircuitBreaker(name = "countriesCircuitBreaker", fallbackMethod = "getCountries")
        public List<Object> getCountries() throws Exception {
            return countriesService.getCountries();
        }
    
        public List<Object> getCountries(Throwable throwable) {
            List<Object> countries = new ArrayList<>();
            countries.add("Country service unavailable!");
            return countries;
        }

Here we can see that when the external service is unavailable it returns a message *“Country service unavailable!”.*

Note : The fallback method should have the same parameter list and return type as the circuit breaker method.

These are the controller and service classes that I have utilized.
{% gist https://gist.github.com/MaheshBabu11/d807eaf5b3482478a7ddeb16289f1e0d.js %}{% gist https://gist.github.com/MaheshBabu11/614b09b564464bc9c3957ee374ffc745.js %}
Lets see the application in action , Instead a getting a white label fall back page , we will get a proper message, this can be also modified to return a local list of countries until the external api is available.

![When the external API is unavailable.](https://cdn-images-1.medium.com/max/3758/1*VWmx7azM_O9clkNQnXNGVA.png)

Now if go to [https://localhost:8080/actuator/health](https://localhost:8080/actuator/health) we can see the details about the circuit breaker. Here we can see that the circuit breaker is up and in a closed state.

![](https://cdn-images-1.medium.com/max/3780/1*rTqWGWOSgsxSnecpvzTRVg.png)

When the external API becomes unavailable, the circuit breaker triggers and opens the circuit, leading to a fallback method being invoked. In this scenario, we can observe that the circuit is open, along with additional information about failed calls and calls that were not permitted.

![](https://cdn-images-1.medium.com/max/3836/1*CJOdJgjaB1iWJXvjQ621vQ.png)

In conclusion, implementing a circuit breaker using Resilience4j can greatly enhance the resilience of our applications by providing fault tolerance and preventing failures from propagating throughout the system. By following the guidelines and examples provided in this article, developers can effectively incorporate circuit breakers into their applications and build more robust and reliable systems.

Happy coding!
