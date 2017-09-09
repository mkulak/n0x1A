Transaction statistics service
=
REST service that accumulates information about latest transactions and provides statistics.

[API spec](src/main/resources/api.yaml)  

How to build
-
Execute `./gradlew clean build`

How to run
-
Execute 

```
./gradlew run
```

or build and then execute:

```
java -jar build/libs/stat-server.jar
``` 
You can specify port like this: 
```
java -jar build/libs/stat-server.jar 9090
``` 

How to test manually
-
* Run server
* Post transactions:
```
curl -X POST --data "{\"amount\": 1.0, \"timestamp\": 1234567890}" http://localhost:8080/transactions
```
* Retrieve statistics:
```
curl http://localhost:8080/statistics
```


Tech choices
-
* **Kotlin**
Great language for JVM. Concise, simple and java-friendly. 
* **Vert.x** 
Nice modular framework for building net/web/REST applications. Fully non-blocking and [quite performant](https://www.techempower.com/benchmarks/#section=data-r14&hw=ph&test=json&f=zffh7j-zik0zj-zik0zj-zik0zj-zik0zj-zijbpb-9zldr).
* **Gradle** 
De facto standard build tool for Kotlin.  
* **JUnit**
I prefer to use simple libraries and write mocks by hand (that's why no Mockito or other fancy staff)

Design decisions
-
* In order to make operations O(1) I decided to store events with 1 second granularity. 
This means system will consider that every event is happening at the beginning of corresponding second.
* Server response in case of empty transaction list is unspecified. I defined it as
`{"sum":0.0,"avg":"NaN","min":"NaN","max":"NaN","count":0}`  
* If server receives transaction with timestamp in the future it adjusts it's time window
to that it becomes `[time of event - window size, time of event)`   
* It's a conscious decision to make class `Cell` immutable and allocate new object each time 
 when it's needed. We can save some memory by allowing mutations and pre-allocating array of cells
 but this will make code less readable and is not worth it (unless real life requirements show otherwise).

Missing things
-
* Integration test
* Config
* Docker

Time spent
-
~6 hours
