package io.pivotal.pas.c2c.udptest;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Based on https://www.baeldung.com/java-broadcast-multicast
 */

@SpringBootApplication
@RestController
public class UdpTestApplication {

	private static Logger LOG = LoggerFactory.getLogger(UdpTestApplication.class);

	@Value("${udp.server}")
	private boolean server;

	@Autowired
	private MulticastingClient client;

	@PostConstruct
	public void initIt() throws Exception {
		if (server) {
			LOG.info("Running in server mode");
			MulticastEchoServer server = new MulticastEchoServer();
			server.start();
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(UdpTestApplication.class, args);
	}

	@RequestMapping("/send")
	public String sendEcho(@RequestParam(value="msg", defaultValue="World")String msg, @RequestParam(value="servers", defaultValue="1")int servers) throws IOException {
		return client.discoverServers(msg, servers);
	}

}
