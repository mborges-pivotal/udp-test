# SpringBoot UDP Tests

The purpose is to make it easier to test UDP communication in PCF. We want to test broadcast, multicast and regular.

* C2C - Server and Client in PCF
* Egress - Client in PCF, server outside

```bash
mvn spring-boot:run -Dspring.profiles.active=server -Djava.net.preferIPv4Stack=true -Dserver.port=8181

cf push  - client
cf push -f manifest-server.yml - server

cf add-network-policy udp-client --destination-app udp-server --protocol udp --port 4446
cf add-network-policy udp-server --destination-app udp-client --protocol udp --port 4446

/send
/send?msg=hello
/send?msg=hello&servers=2   - expect two servers to echo the message back
```