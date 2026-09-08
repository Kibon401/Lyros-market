#!/bin/sh

echo "--- RUNNING APPLICATION (forcing IPv4) ---"
# The -Djava.net.preferIPv4Stack=true flag forces the JVM to use IPv4,
# which resolves the 'Network is unreachable' error on some hosting platforms.
java -Djava.net.preferIPv4Stack=true -jar build/libs/Lyros-market-all.jar -port=8080
