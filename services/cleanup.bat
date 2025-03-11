@echo off
REM cleanup.bat for Windows

echo Stopping and removing existing containers...
docker stop keycloak keycloak-mysql kafka kafka-ui zookeeper prometheus grafana
docker rm keycloak keycloak-mysql kafka kafka-ui zookeeper prometheus grafana

echo Removing existing networks...
docker network rm keycloak-network monitoring-network

echo Cleanup completed!