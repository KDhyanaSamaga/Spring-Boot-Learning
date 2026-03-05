# Mastering Logging in Java with SLF4J and Spring Boot — A Complete Beginner's Guide

**SEO Title:** Logging in Java with SLF4J and Spring Boot | Complete Guide with Code Examples

**Meta Description:** Learn how to implement logging in Java using SLF4J and Spring Boot. Covers log levels (TRACE to ERROR), Lombok @Slf4j, custom patterns, file logging, package-level control, and logback-spring.xml — with real code and terminal output.

**Tags:** Java, Spring Boot, SLF4J, Logging, Backend Development, Java Tutorial, Logback, Lombok

---

## 🪵 What Even Is Logging? (And Why You Desperately Need It)

Imagine this: you build an application. It works perfectly on your machine. You push it to production. A user messages you — *"It's not working."*

Now what?

You can't see their screen. You can't step through the code. You're essentially **blind**.

That's exactly where **logging** saves you.

> Logging is like leaving **breadcrumbs inside your application** — small pieces of information that tell you what happened, when it happened, and where things went wrong.

Instead of guessing, you get clear, timestamped messages telling you:
- Which part of the application executed
- What values were in your variables
- Where exactly the system failed
- Whether you're dealing with a remote server issue, a null pointer, or a bad request

In short: **logging turns debugging from guesswork into detective work.**

Spring Boot has great documentation on logging here: [https://docs.spring.io/spring-boot/reference/features/logging.html](https://docs.spring.io/spring-boot/reference/features/logging.html)

---

## 📋 What a Default Spring Boot Log Looks Like

When you run a Spring Boot application, you'll see output like this in your terminal — this is Spring's **default log format**:

```
2026-02-19T12:22:37.451Z  INFO 128443 --- [myapp] [main] o.s.b.d.f.logexample.MyApplication : Starting MyApplication using Java 25.0.2
2026-02-19T12:22:40.359Z  INFO 128443 --- [myapp] [main] o.s.boot.tomcat.TomcatWebServer    : Tomcat initialized with port 8080 (http)
2026-02-19T12:22:41.797Z  INFO 128443 --- [myapp] [main] o.s.boot.tomcat.TomcatWebServer    : Tomcat started on port 8080 (http) with context path '/'
2026-02-19T12:22:41.813Z  INFO 128443 --- [myapp] [main] o.s.b.d.f.logexample.MyApplication : Started MyApplication in 5.537 seconds
```

Each log line contains:
- **Timestamp** — when it happened
- **Log Level** — how important the message is (INFO, WARN, ERROR, etc.)
- **Process ID** — the running process
- **Thread name** — which thread executed it
- **Logger name** — which class generated it
- **Message** — the actual log content

Now let's go beyond the defaults and write our own logs.

---

## 🚀 Setting Up: Your First Logger with SLF4J

### Step 1 — Create a Spring Boot Project

Open IntelliJ IDEA, create a new Spring Boot project, add the **Spring Web** dependency, and give it a name (e.g., `Java-SLF4J`).

### Step 2 — Create a Controller

Create a `BasicController.java` file with the following code:

```java
package com.dhyana.javaslf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasicController {

    private static final Logger logger = LoggerFactory.getLogger(BasicController.class);

    @GetMapping("/hello")
    public String sayHello() {
        logger.info("Received request for /hello");
        return "Hello World This is Demo";
    }
}
```

**Breaking down the Logger line:**

```java
private static final Logger logger = LoggerFactory.getLogger(BasicController.class);
```

| Part | What it does |
|---|---|
| `private static final` | Scoped to the class, not recreated each time |
| `Logger` | Interface from **SLF4J** (not java.util!) |
| `logger` | Your logger object |
| `LoggerFactory.getLogger(...)` | Creates the logger tied to this class |
| `BasicController.class` | Tells SLF4J which class the logs come from |

### Step 3 — Run and Test

Start the application, then visit `http://localhost:8080/hello` in your browser. Now check the terminal:

```
2026-03-05T10:09:53.427+05:30  INFO 7040 --- [Java-SLF4J] [nio-8080-exec-1] com.dhyana.javaslf4j.BasicController : Received request for /hello
2026-03-05T10:09:57.716+05:30  INFO 7040 --- [Java-SLF4J] [nio-8080-exec-5] com.dhyana.javaslf4j.BasicController : Received request for /hello
2026-03-05T10:09:57.876+05:30  INFO 7040 --- [Java-SLF4J] [nio-8080-exec-6] com.dhyana.javaslf4j.BasicController : Received request for /hello
```

Every time you refresh the page, a new log entry appears. You can literally **watch traffic hit your endpoint** in real time.

---

## 🔢 Logging with Parameters

Static messages are fine, but in real apps you need dynamic context. Here's how you add **parameters** to your logs:

```java
String userName = "Dhyana";
logger.info("Received request for /hello :{}", userName);
```

The `{}` is a **placeholder** — SLF4J substitutes the variable at runtime without expensive string concatenation.

**Terminal output:**
```
INFO --- [nio-8080-exec-1] com.dhyana.javaslf4j.BasicController : Received request for /hello :Dhyana
```

You can chain multiple parameters too:

```java
logger.info("User: {} accessed endpoint: {} at time: {}", userName, endpoint, timestamp);
```

---

## 🎚️ Log Levels — Not All Messages Are Equal

SLF4J defines **6 log levels**, ordered by severity:

```
TRACE → DEBUG → INFO → WARN → ERROR → OFF
```

| Level | When to Use |
|---|---|
| **TRACE** | Most granular — traces every line of execution. Rarely used in production |
| **DEBUG** | For developers — shows internal flow, variables, logic checks |
| **INFO** | High-level events — app started, server ready, user logged in |
| **WARN** | App didn't crash, but something suspicious happened |
| **ERROR** | Something failed — exception thrown, DB down, etc. |
| **OFF** | Disables all logging entirely |

Here's all five levels in action:

```java
logger.trace("TRACE: LOG");
logger.debug("DEBUG: LOG");
logger.info("INFO: LOG");
logger.warn("WARN: LOG");
logger.error("ERROR: LOG");
```

**Terminal output (default — only INFO and above):**
```
INFO  --- [nio-8080-exec-1] com.dhyana.javaslf4j.BasicController : INFO: LOG
WARN  --- [nio-8080-exec-1] com.dhyana.javaslf4j.BasicController : WARN: LOG
ERROR --- [nio-8080-exec-1] com.dhyana.javaslf4j.BasicController : ERROR: LOG
```

Notice: **TRACE and DEBUG are missing!** That's because the default level is `INFO` — only INFO and above show up.

### Enabling TRACE and DEBUG

Go to `src/main/resources/application.properties` and add:

```properties
logging.level.root = TRACE
```

Now rerun — you'll see all five levels, including TRACE and DEBUG. The `root` setting applies **globally** to your entire application.

> 💡 Change to `DEBUG` to see debug and above, or keep `INFO` for production (the default).

---

## ✨ Lombok @Slf4j — Let the Annotation Do the Work

Creating the logger manually every time is tedious. If you're using **Lombok**, you can eliminate that boilerplate entirely.

### Step 1 — Add Lombok to pom.xml

Find the latest version at [https://central.sonatype.com/artifact/org.projectlombok/lombok](https://central.sonatype.com/artifact/org.projectlombok/lombok) and add it:

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

### Step 2 — Replace Manual Logger with @Slf4j

```java
package com.dhyana.javaslf4j;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j  // ← This replaces the manual Logger declaration
public class BasicController {

    @GetMapping("/hello")
    public String sayHello() {
        String userName = "Dhyana";
        log.info("Received request for /hello :{}", userName);
        log.trace("TRACE: LOG");
        log.debug("DEBUG: LOG");
        log.info("INFO: LOG");
        log.warn("WARN: LOG");
        log.error("ERROR: LOG");
        return "Hello World This is Demo";
    }
}
```

Lombok automatically injects `private static final Logger log = LoggerFactory.getLogger(...)` at compile time. You use `log` instead of `logger` — clean, simple, and less code to maintain.

---

## 🎨 Customizing the Log Format in the Console

Spring Boot lets you control exactly how logs appear. Add this to `application.properties`:

```properties
logging.pattern.console = %d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
```

**Pattern breakdown:**

| Token | Meaning |
|---|---|
| `%d{yyyy-MM-dd HH:mm:ss}` | Date and timestamp |
| `[%thread]` | Thread name |
| `%-5level` | Log level, padded to 5 characters |
| `%logger{36}` | Logger/class name, max 36 characters |
| `%msg` | The actual log message |
| `%n` | Newline |

---

## 💾 Saving Logs to a File

Logs in the terminal are gone when the app stops. In production, you need to **persist them**. Add this to `application.properties`:

```properties
logging.file.name = logs/app.log
```

Spring Boot will automatically create a `logs/` folder with `app.log` inside when the app runs. If the folder doesn't appear in IntelliJ, right-click the project → **Reload from Disk**.

To apply your custom format to the file as well:

```properties
logging.pattern.file = %d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
```

> Why save logs to a file? In production, log files are **your audit trail** — they help you trace bugs, understand user behavior, monitor performance, and respond to incidents — even days after they happened.

---

## 📦 Package-Level Log Control — The Production-Ready Way

In a real application you have multiple packages — controllers, services, repositories. Using `logging.level.root = DEBUG` is a blunt instrument: it floods your terminal with everything.

The right approach is **package-level control**.

### Setting Up Multiple Packages

Create two packages: `controller` and `service`.

**BasicService.java:**

```java
package com.dhyana.javaslf4j.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BasicService {

    public String sayHello() {
        log.trace("TRACE: BasicService LOG");
        log.debug("DEBUG:  BasicService LOG");
        log.info("INFO:   BasicService LOG");
        log.warn("WARN:   BasicService LOG");
        log.error("ERROR:  BasicService LOG");
        return "Hello";
    }
}
```

**BasicController.java:**

```java
package com.dhyana.javaslf4j.controller;

import com.dhyana.javaslf4j.service.BasicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class BasicController {

    private final BasicService basicService;

    public BasicController(BasicService basicService) {
        this.basicService = basicService;
    }

    @GetMapping("/hello")
    public String sayHello() {
        String userName = "Dhyana";
        log.info("Received request for /hello :{}", userName);
        log.trace("TRACE:  BasicController LOG");
        log.debug("DEBUG:  BasicController LOG");
        log.info("INFO:   BasicController LOG");
        log.warn("WARN:   BasicController LOG");
        log.error("ERROR:  BasicController LOG");
        return basicService.sayHello();
    }
}
```

### application.properties — Fine-Grained Control

```properties
spring.application.name=Java-SLF4J

# Global default
logging.level.root = INFO

# Per-package control
logging.level.com.dhyana.javaslf4j.controller = INFO
logging.level.com.dhyana.javaslf4j.service = DEBUG

# File output
logging.file.name = logs/app.log
logging.pattern.file = %d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n

# Console output
logging.pattern.console = %d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
```

Now when you hit `/hello`:
- **Controller** logs at INFO level and above (TRACE, DEBUG hidden)
- **Service** logs at DEBUG level and above (only TRACE hidden)

This is exactly how production systems work — you give more verbosity to services you're actively debugging, while keeping everything else quiet.

---

## ⚙️ Advanced Customization with logback-spring.xml

For more complex setups — different log files per level, daily rolling files, sending logs to Kafka or a database — `application.properties` alone isn't enough. Enter `logback-spring.xml`.

Create this file in `src/main/resources/`:

```xml
<?xml version="1.0" encoding="utf-8" ?>
<configuration>

    <!-- Console output -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- Rolling file — creates a new file every day -->
    <appender name="MAIN_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/new_app.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/app-%d{yyyy-MM-dd}.log</fileNamePattern>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- Root logger — INFO and above go to console -->
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
    </root>

</configuration>
```

With `logback-spring.xml` you can:
- Create **separate log files for ERROR only**
- Configure **daily rolling log files** (auto-archived by date)
- Set **different patterns per appender**
- **Send logs to external systems** like Kafka, Elasticsearch, or a database
- Filter logs by package and route them to separate files

> For a simple application, `app.log` is plenty. For large-scale production systems, `logback-spring.xml` gives you full control.

---

## 🗺️ The Full Picture — Quick Reference

| What you want | How to do it |
|---|---|
| Add logging to a class | `private static final Logger logger = LoggerFactory.getLogger(MyClass.class);` |
| Use Lombok instead | Add `@Slf4j` annotation and use `log.info(...)` |
| Log with variables | `log.info("User: {}", userName)` |
| Set global log level | `logging.level.root = DEBUG` in application.properties |
| Set per-package level | `logging.level.com.yourpackage = DEBUG` |
| Save logs to file | `logging.file.name = logs/app.log` |
| Customize log format | `logging.pattern.console = %d{...} [%thread] %-5level ...` |
| Advanced file control | Use `logback-spring.xml` |

---

## 🎯 Final Thoughts

Logging is one of those things that seems optional until you absolutely need it — and by then, it's too late. Setting it up early, doing it right, and knowing how to read logs is a **core backend engineering skill**.

To summarize what we covered:
- What logging is and why it matters
- Writing your first SLF4J logger in Spring Boot
- Using parameterized log messages
- Understanding all 6 log levels (TRACE to ERROR)
- Simplifying with Lombok's `@Slf4j`
- Customizing console and file output patterns
- Controlling log levels per package
- Going advanced with `logback-spring.xml`

Next time a user says "it's not working," you won't be flying blind. You'll have your breadcrumbs — and you'll know exactly where to look.

---

*Found this helpful? Drop a clap 👏 and follow for more Java and Spring Boot deep dives!*
