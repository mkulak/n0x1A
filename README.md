What is it
-

How to build
-

How to run
-

How to test
-
```
curl -X POST --data "{\"amount\": 1.0, \"timestamp\": 1234567890}" http://localhost:8080/transactions
curl http://localhost:8080/statistics
```
-

Design decisions
-

* Kotlin
* Vert.x
* Gradle (commited gradle wrapper)
* JUnit without mocks
* Loss of precision inside interval of 1 second
* What to return if window is empty
* Mutable vs immutable
* What to do when receive transaction with timestamp in the future
