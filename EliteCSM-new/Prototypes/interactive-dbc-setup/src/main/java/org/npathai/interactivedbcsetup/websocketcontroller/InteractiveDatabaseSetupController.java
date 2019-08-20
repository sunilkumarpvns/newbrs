package org.npathai.interactivedbcsetup.websocketcontroller;

import java.sql.SQLException;

import org.npathai.interactivedbcsetup.domain.GreetingMessage;
import org.npathai.interactivedbcsetup.domain.HelloMessage;
import org.npathai.interactivedbcsetup.domain.schema.EliteAAASchemaCreator;
import org.npathai.interactivedbcsetup.domain.schema.SchemaEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class InteractiveDatabaseSetupController {

	@Autowired
	private MessageSendingOperations<String> messagingOperations;
	
	private EliteAAASchemaCreator schemaCreator;

	@MessageMapping("/create")
	public void create(HelloMessage message) throws InterruptedException {
		if (schemaCreator == null) {
			schemaCreator = new EliteAAASchemaCreator();
			schemaCreator.addSchemaEventListener(new WebsocketSchemaEventListener());
			try {
				schemaCreator.create();
				messagingOperations.convertAndSend("/topic/greetings", new GreetingMessage("Done"));
			} catch (SQLException e) {
				e.printStackTrace();
				messagingOperations.convertAndSend("/topic/greetings", new GreetingMessage("Error"));
			}
			schemaCreator = null;
		}
	}
	
	@MessageMapping("/drop")
	public void drop(HelloMessage message) throws InterruptedException {
		if (schemaCreator == null) {
			schemaCreator = new EliteAAASchemaCreator();
			schemaCreator.addSchemaEventListener(new WebsocketSchemaEventListener());
			try {
				schemaCreator.drop();
				messagingOperations.convertAndSend("/topic/greetings", new GreetingMessage("Done"));
			} catch (SQLException e) {
				e.printStackTrace();
				messagingOperations.convertAndSend("/topic/greetings", new GreetingMessage("Error"));
				schemaCreator.dropForcefully();
			}
			schemaCreator = null;
		}
	}
	
	private class WebsocketSchemaEventListener implements SchemaEventListener {

		@Override
		public void creating(String table) {
			sleepQuietly();
			messagingOperations.convertAndSend("/topic/greetings", new GreetingMessage("Creating table: " + table));
		}

		private void sleepQuietly() {
			try {
				Thread.sleep(700);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void created(String table) {
			sleepQuietly();
			messagingOperations.convertAndSend("/topic/greetings", new GreetingMessage("Created table: " + table));
		}

		@Override
		public void error(String table, String message) {
			sleepQuietly();
			messagingOperations.convertAndSend("/topic/greetings", new GreetingMessage("Error in creating table: " + table));
		}

		@Override
		public void dropping(String table) {
			sleepQuietly();
			messagingOperations.convertAndSend("/topic/greetings", new GreetingMessage("Dropping table: " + table));
		}

		@Override
		public void dropped(String table) {
			sleepQuietly();
			messagingOperations.convertAndSend("/topic/greetings", new GreetingMessage("Dropped table: " + table));
		}

		@Override
		public void starting(int count) {
			messagingOperations.convertAndSend("/topic/greetings", new GreetingMessage("Count", count * 2));
		}
	}
}
