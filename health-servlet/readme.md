
## Overview 

Use the servlets to check the karaf health and readiness status with kubernetes.

The package will deploy the servlets under /system/health and /system/ready where it can be called from the kubernetes http check.

## Kubernetes

You can use a kubernetes configuration like this
```
livenessProbe:
  httpGet:
     path: /system/health
     port: 8181
  timeoutSeconds: 3
  initialDelaySeconds: 15
  failureThreshold: 3
readinessProbe:
  httpGet:
    path: /system/ready
    port: 8181
  timeoutSeconds: 3
  failureThreshold: 1
```

## Checks

The two servlets are using nearly the same mechanisms to check status of the engine. You can configure it with the default osgi configuration in the configuration file or web console.

Configuration files:

```
etc/de.mhus.osgi.healthservlet.HealthServlet.cfg

bundlesEnabled = B"true"
bundlesIgnore = [ \
  "org.apache.karaf.features.extension", \
  "org.apache.aries.blueprint.core.compatibility", \
  "org.apache.karaf.shell.console", \
  "org.jline.terminal-jansi", \
  ]
checkCombineTagsWithOr = B"false"
checkEnabled = B"true"
checkForceInstantExecution = B"false"
checkIgnore = [ \
  "", \
  ]
checkOverrideGlobalTimeoutStr = ""
checkTags = "*"
logEnabled = B"true"
logLevel = "DEBUG"
logPatterns = [ \
  ".* java\\.lang\\.OutOfMemoryError:.*", \
  ]
logResetFinding = B"false"
waitAfterStart = L"60000"

etc/de.mhus.osgi.healthservlet.ReadyServlet.cfg

bundlesEnabled = B"true"
bundlesIgnore = [ \
  "org.apache.karaf.features.extension", \
  "org.apache.aries.blueprint.core.compatibility", \
  "org.apache.karaf.shell.console", \
  "org.jline.terminal-jansi", \
  ]
checkCombineTagsWithOr = B"false"
checkEnabled = B"true"
checkForceInstantExecution = B"false"
checkIgnore = [ \
  "", \
  ]
checkOverrideGlobalTimeoutStr = ""
checkTags = "*"
logEnabled = B"true"
logLevel = "DEBUG"
logPatterns = [ \
  ".* java\\.lang\\.OutOfMemoryError:.*", \
  ]
logResetFinding = B"false"
```

### Start Wait

This configuration is only available in the Health Check. The Heals Check is from starting in the 'start mode' In this mode no error will be reported. If start mode is done it will switch to 'check mode'. This is mode the servlet will return also a negative health status.

Switching to checkk mode could have two reasons. First all bundles are in mode ready and second the start wait time is up.

This parameter define the start wait mode in milliseconds.

```
waitAfterStart: 60000 - How long to wait after start
```

### Bundle State Check

This check scans all bundles and returns an alert if a bundle is not in status running. It's possible to ignore bundles.

```
bundlesEnabled: true - Enable bundle status check
bundlesIgnore: ["org.apache.karaf.features.extension",
                "org.apache.aries.blueprint.core.compatibility",
                "org.apache.karaf.shell.console,org.jline.terminal-jansi"]
                - Bundles to ignore
```

### Log Messages Check

The servlet is able to scan log messages and alert if it found a pattern. Obligatory scans are out of memory messages.

```
logEnabled: true - Enable log scan
logLevel: DEBUG - The minimum debug level to scan
logPatterns: [ ".* java\\.lang\\.OutOfMemoryError:.*" ]
             - Regular expression for log messages
logResetFinding: false - Reset the findings after delivery or return alert for ever
```

### OSGi Health Check

The servlet is able to use the felix health check api and alert if a health check return a critical status.

The Health Servlet will trigger alerts if messages with CRITICAL are produced. The Ready Servlet will return an alert if messages with WARN are produced.

This means CRITICAL will cause shutdown of the container and WARN will cause to remove the container from Kubernetes services pools.

```
checkEnabled: true - Enable OSGi health check
checkIgnore: [] - List of health check to ignore
checkCombineTagsWithOr: false - Filter will use or instead of and
checkTags: "*" - Filter for special tags
checkForceInstantExecution: false - Force execution even if the period of execution is not finished
checkOverrideGlobalTimeoutStr: ""
```

## Installation

You need to install the felix health framework and the check servlet

```
felix.healthcheck.api.version=2.0.2
felix.healthcheck.core.version=2.0.6
felix.healthcheck.generalchecks.version=2.0.4
mhus-osgi-servlets.version=7.0.0

install -s mvn:org.apache.felix/org.apache.felix.healthcheck.api/${felix.healthcheck.api.version}
install -s mvn:org.apache.felix/org.apache.felix.healthcheck.core/${felix.healthcheck.core.version}
install -s mvn:org.apache.felix/org.apache.felix.healthcheck.generalchecks/${felix.healthcheck.generalchecks.version}
install -s mvn:de.mhus.osgi/health-servlet/${mhus-osgi-servlets.version}
```

If you want to use the felix webconsole extensio too, install the console extension:

```
felix.healthcheck.webconsoleplugin.version=2.0.0

feature:install webconsole
install -s mvn:org.apache.felix/org.apache.felix.healthcheck.webconsoleplugin/${felix.healthcheck.webconsoleplugin.version}
```
