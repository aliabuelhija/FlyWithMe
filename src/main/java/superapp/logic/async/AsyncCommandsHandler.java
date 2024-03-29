package superapp.logic.async;

import java.util.Date;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jgroups.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import superapp.dal.MiniAppCommandCrud;
import superapp.data.DefaultValues;
import superapp.data.MiniAppCommandEntity;
import superapp.logic.converters.MiniAppConvertor;
import superapp.logic.mini_app.MiniAppCommandBoundary;

@Component
public class AsyncCommandsHandler {
	private ObjectMapper jackson;
	private MiniAppConvertor converter;
	private MiniAppCommandCrud miniAppCommandCrud;
	private Log logger = LogFactory.getLog(AsyncCommandsHandler.class);

// Constructors
	@Autowired
	public AsyncCommandsHandler(MiniAppConvertor converter,	MiniAppCommandCrud miniAppCommandCrud) {
		this.jackson = new ObjectMapper();
		this.converter = converter;
		this.miniAppCommandCrud = miniAppCommandCrud;
	}

	@JmsListener(destination = DefaultValues.ASYNC_COMMANDS_QUEUE)
	public void handleCommandsFromQueue(String json) {
		logger.info("Start take from queue");
		try {
			MiniAppCommandBoundary miniAppCommandBoundary = this.jackson.readValue(json, MiniAppCommandBoundary.class);

			if (miniAppCommandBoundary.getCommandAttributes() == null) {
				miniAppCommandBoundary.setCommandAttributes(new HashMap<>());
			}
			miniAppCommandBoundary.getCommandAttributes().put("status", "remote-is-done");

			MiniAppCommandEntity miniAppCommandEntity = this.converter.toEntity(miniAppCommandBoundary);
			if (miniAppCommandEntity.getCommandId() == null) {
				miniAppCommandEntity.setCommandId(UUID.randomUUID().toString());
			}
			if (miniAppCommandEntity.getInvokcationTimestamp() == null) {
				miniAppCommandEntity.setInvokcationTimestamp(new Date());
			}

			miniAppCommandEntity = this.miniAppCommandCrud.save(miniAppCommandEntity);
			
			logger.info("End take from queue");
		} catch (Exception e) {
			logger.error("Failed to work in async");
			e.printStackTrace(System.err);
		}
	}
}