package superapp.logic.MDBservices;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;  
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import superapp.dal.MiniAppCommandCrud;
import superapp.dal.TripAdvisorApiService;
import superapp.data.DefaultValues;
import superapp.data.MiniAppCommandEntity;
import superapp.data.UserRole;
import superapp.logic.MiniAppCommandsServiceExtend;
import superapp.logic.converters.MiniAppConvertor;
import superapp.logic.exceptions‬.BadRequestException;
import superapp.logic.exceptions‬.DeprecatedOperationException;
import superapp.logic.exceptions‬.ServerErrorException;
import superapp.logic.mini_app.CommandId;
import superapp.logic.mini_app.MiniAppCommandBoundary;

@Service
public class MiniAppCommandServiceMdb implements MiniAppCommandsServiceExtend {	
	private MiniAppCommandCrud miniAppCommandCrud;
	private MiniAppConvertor convertor;
	private ObjectsServiceMdb objectsServiceMdb;
	private String springApplicationName;
	private JmsTemplate jmsTemplate;
	private ObjectMapper jackson;
	private TripAdvisorApiService tripAdvisorApiService;
	private Log logger = LogFactory.getLog(MiniAppCommandServiceMdb.class);

	@Autowired
	public MiniAppCommandServiceMdb(MiniAppCommandCrud miniAppCommandCrud, MiniAppConvertor convertor,
			ObjectsServiceMdb objectsServiceMdb, JmsTemplate jmsTemplate) {
		this.miniAppCommandCrud = miniAppCommandCrud;
		this.convertor = convertor;
		this.objectsServiceMdb = objectsServiceMdb;
		this.jmsTemplate = jmsTemplate;
		this.jmsTemplate.setDeliveryDelay(3000L);
		this.tripAdvisorApiService = new TripAdvisorApiService();

	}

	@PostConstruct
	public void init() {
		this.jackson = new ObjectMapper();
	}

	@Value("${spring.application.name}")
	public void setSpringApplicationName(String nameFromSpring) {
		this.springApplicationName = nameFromSpring;
	}

// Invoke command
	@Override
	public Object invokeCommand(MiniAppCommandBoundary miniAppCommandBoundary) {
		logger.info("Start invoke command");
		
		// Check command input validity
		logger.trace("Start checking the fields for invoke command");
		if (!CheckFields.checkInvokeCommandInput(miniAppCommandBoundary)) {
			logger.error("The fields for invoke command are not valid");
			throw new BadRequestException("The fields for invoke command are not valid");
		}
		
		// Check user role validity
		if (!CheckFields.checkIfUserRoleIsValid(miniAppCommandBoundary.getInvokedBy().getUserId().getSuperapp(),
				miniAppCommandBoundary.getInvokedBy().getUserId().getEmail(), UserRole.MINIAPP_USER)) {
			logger.error("User is not miniapp user");
			throw new BadRequestException("Only miniapp user can invoke command");
		}

		// Check if target object exists and is active
		if (!CheckFields.checkIfTargetObjectIsExistAndActive(miniAppCommandBoundary)) {
			logger.error("Target object does not exist or is not active");
			throw new BadRequestException("Target object does not exist or is not active");
		}
		
		logger.trace("End checking the fields for invoke command");

		logger.trace("Set invocation timestamp and command ID");
		// Set invocation timestamp
		miniAppCommandBoundary.setInvocationTimestamp(new Date());

		// Generate command ID
		miniAppCommandBoundary.getCommandId().setSuperapp(springApplicationName);
		miniAppCommandBoundary.getCommandId().setInternalCommandId(UUID.randomUUID().toString());

		logger.trace("Save entity");
		// Save command entity
		MiniAppCommandEntity entity = convertor.toEntity(miniAppCommandBoundary);
		entity = this.miniAppCommandCrud.save(entity);

		String miniAppName = miniAppCommandBoundary.getCommandId().getMiniApp();
		String command = miniAppCommandBoundary.getCommand();

		logger.info("Check which miniapp to active");
		if (miniAppName.equalsIgnoreCase(DefaultValues.FIND_HOTEL_MINIAPP_NAME)) { // Find hotel miniapp
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DefaultValues.DATE_FORMAT_TO_TRIP_ADVISOR);
			SimpleDateFormat sdf = new SimpleDateFormat(DefaultValues.DATE_FORMAT_TO_TRIP_ADVISOR);
			String query = DefaultValues.DEFAULT_CITY_QUERY;
			int adults = DefaultValues.DEFAULT_ADULTS;
			int rooms = DefaultValues.DEFAULT_ROOMS;
			int nights = DefaultValues.DEFAULT_NIGHTS;
			String checkin = dtf.format(LocalDateTime.now());
			Calendar c = Calendar.getInstance();
			try {
				c.setTime(sdf.parse(checkin));
			}
			catch(Exception e)
			{
				logger.error("Failed to parse the checkin date into sdf format");
				throw new ServerErrorException("Failed to parse the checkin date into sdf format");
			}
			c.add(Calendar.DATE, nights);  // number of days to add
			String checkout = sdf.format(c.getTime());
			String sortOrder = "";
			
			if(miniAppCommandBoundary.getCommandAttributes() != null) {
				if(miniAppCommandBoundary.getCommandAttributes().get("query") != null)
					query = (String) miniAppCommandBoundary.getCommandAttributes().get("query");
				if(miniAppCommandBoundary.getCommandAttributes().get("adults") != null)
					adults = (int) miniAppCommandBoundary.getCommandAttributes().get("adults");
				if(miniAppCommandBoundary.getCommandAttributes().get("rooms") != null)
					rooms = (int) miniAppCommandBoundary.getCommandAttributes().get("rooms");
				if(miniAppCommandBoundary.getCommandAttributes().get("nights") != null)
					nights = (int) miniAppCommandBoundary.getCommandAttributes().get("nights");
				if(miniAppCommandBoundary.getCommandAttributes().get("checkin") != null)
					checkin = (String) miniAppCommandBoundary.getCommandAttributes().get("checkin");
				if(miniAppCommandBoundary.getCommandAttributes().get("checkout") != null)
					checkout = (String) miniAppCommandBoundary.getCommandAttributes().get("checkout");
			}
			
			int locationID = tripAdvisorApiService.getLocationId(query);

			switch (command.toLowerCase()) { // Check which command activated
			case DefaultValues.FIND_HOTEL_COMMAND1_NAME:
				logger.trace("Get hotels by cheapest price command in Tal miniapp will invoke");
				sortOrder = "PRICE_LOW_TO_HIGH";
				break;
			case DefaultValues.FIND_HOTEL_COMMAND2_NAME:
				logger.trace("Get hotels by travel ranking command in Tal miniapp will invoke");
				sortOrder = "POPULARITY";
				break;
			case DefaultValues.FIND_HOTEL_COMMAND3_NAME:
				logger.trace("Get hotels by distance do city centre command in Tal miniapp will invoke");
				sortOrder = "DISTANCE_FROM_CITY_CENTER";
				break;
			default: // Set a default sort order if the command is unrecognized
				logger.trace("Get hotels by price low to high command in Tal miniapp will invoke");
				sortOrder = "PRICE_LOW_TO_HIGH";
				break;
			}
			logger.info("End invoke command - Tal app");
			return this.objectsServiceMdb.insertEachJsonInJsonArrayOfHotelsAsObject(tripAdvisorApiService.getHotels(locationID, adults, rooms, nights, checkin, checkout, sortOrder));
		}
		else if (miniAppName.equalsIgnoreCase(DefaultValues.FIND_FLIGHT_MINIAPP_NAME)) { // Find flight miniapp
			String query = (String) miniAppCommandBoundary.getCommandAttributes().get("query");

			switch (command.toLowerCase()) {
			case DefaultValues.FIND_FLIGHT_COMMAND1_NAME:
				logger.trace("Search airport command in Ahmad miniapp will invoke");
				logger.info("End invoke command");
				return tripAdvisorApiService.getAirport(query);
			case DefaultValues.FIND_FLIGHT_COMMAND2_NAME:
				logger.trace("Search flight command in Ahmad miniapp will invoke");
				String sourceAirportCode = (String) miniAppCommandBoundary.getCommandAttributes()
						.get("sourceAirportCode");
				String destinationAirportCode = (String) miniAppCommandBoundary.getCommandAttributes()
						.get("destinationAirportCode");
				String date = (String) miniAppCommandBoundary.getCommandAttributes().get("date");
				String returnDate = (String) miniAppCommandBoundary.getCommandAttributes().get("returnDate");
				String itineraryType = (String) miniAppCommandBoundary.getCommandAttributes().get("itineraryType");
				int numAdults = (int) miniAppCommandBoundary.getCommandAttributes().get("numAdults");
				int numSeniors = (int) miniAppCommandBoundary.getCommandAttributes().get("numSeniors");
				String classOfService = (String) miniAppCommandBoundary.getCommandAttributes().get("classOfService");

				logger.info("End invoke command - Ahmad miniapp");
				return tripAdvisorApiService.searchFlights(sourceAirportCode, destinationAirportCode, date, returnDate,
						itineraryType, numAdults, numSeniors, classOfService);

			default:
				break;
			}
		}
		// Unrecognized command
		logger.trace("No known command in known miniapp will invoke");
		logger.info("End invoke command");
		return entity;
	}

// Async invoke command
	@Override
	public Object asyncInvokeCommand(MiniAppCommandBoundary miniAppCommandBoundary) {
		logger.info("Start async invoke command");
		
		// Check user role validity
		if (!CheckFields.checkIfUserRoleIsValid(miniAppCommandBoundary.getInvokedBy().getUserId().getSuperapp(),
				miniAppCommandBoundary.getInvokedBy().getUserId().getEmail(), UserRole.MINIAPP_USER)) {
			logger.error("User is not miniapp user");
			throw new BadRequestException("Only miniapp user can invoke command");
		}

		// Check command input validity
		logger.trace("Start checking the fields for invoke command");
		if (!CheckFields.checkInvokeCommandInput(miniAppCommandBoundary)) {
			logger.error("The fields for invoke command are not valid");
			throw new BadRequestException("The fields for invoke command are not valid");
		}

		// Check if target object exists and is active
		if (!CheckFields.checkIfTargetObjectIsExistAndActive(miniAppCommandBoundary)) {
			logger.error("Target object does not exist or is not active");
			throw new BadRequestException("Target object does not exist or is not active");
		}

		logger.info("End async invoke command");
		return asyncOperation(miniAppCommandBoundary);
	}

// Work in async
	@Override
	public Object asyncOperation(MiniAppCommandBoundary miniAppCommandBoundary) {
		logger.info("Start async operation");
		
		logger.trace("Set command ID, invocation time stamp, superapp, command attributes");
		miniAppCommandBoundary.getCommandId().setInternalCommandId(UUID.randomUUID().toString());
		miniAppCommandBoundary.setInvocationTimestamp(new Date());
		miniAppCommandBoundary.getCommandId().setSuperapp(springApplicationName);
		if (miniAppCommandBoundary.getCommandId() == null) {
			CommandId id;
			id = new CommandId();
			miniAppCommandBoundary.setCommandId(id);
		}
		if (miniAppCommandBoundary.getCommandAttributes() == null) {
			miniAppCommandBoundary.setCommandAttributes(new HashMap<>());
		}

		miniAppCommandBoundary.getCommandAttributes().put("status", "waiting...");

		try {
			logger.trace("Add the opration to queue of commands");
			String json = this.jackson.writeValueAsString(miniAppCommandBoundary);
			this.jmsTemplate.convertAndSend("asyncCommandsQueue", json);
		} catch (Exception e) {
			logger.trace("Failed to add the opration to queue of commands");
			throw new RuntimeException(e);
		}
		
		logger.info("End async operation");
		return miniAppCommandBoundary;
	}

// Delete all commands
	@Override
	@Deprecated
	public void deleteAllCommands() {
		logger.error("Start delete all commands - Deprecated");
		throw new DeprecatedOperationException();
	}

// Delete all commands - new
	@Override
	public void deleteAllCommands(String userSuperapp, String userEmail) {
		logger.info("Start delete all commands");
		if (!CheckFields.checkIfUserRoleIsValid(userSuperapp, userEmail, UserRole.ADMIN)) {
			logger.error("User is not admin");
			throw new BadRequestException("Only admin can delete all commands");
		}
		miniAppCommandCrud.deleteAll();
		logger.info("End delete all commands");
	}

// Get all commands
	@Override
	@Deprecated
	public List<MiniAppCommandBoundary> getAllCommands() {
		logger.error("Start get all commands - Deprecated");
		throw new DeprecatedOperationException();
	}

// Get all commands new
	@Override
	public List<MiniAppCommandBoundary> getAllCommands(String userSuperapp, String userEmail, int size, int page) {
		logger.trace("Start get all commands");
		if (!CheckFields.checkIfUserRoleIsValid(userSuperapp, userEmail, UserRole.ADMIN)) {
			logger.error("User is not admin");
			throw new BadRequestException("Only admin can get all commands");
		}
		logger.trace("End get all commands");
		return this.miniAppCommandCrud
				.findAll(PageRequest.of(page, size, Direction.DESC, "command", "invokcationTimestamp", "commandId"))
				.stream() // Stream<CommandEntity>
				.map(this.convertor::toBoundary) // Stream<CommandBoundary>
				.toList(); // List<CommandBoundary>
	}

// Get all mini app commands - old
	@Override
	@Deprecated
	public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName) {
		logger.error("Start get all miniapp commands - Deprecated");
		throw new DeprecatedOperationException();
	}

// Get all mini app commands - new
	@Override
	public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName, String userSuperapp, String userEmail,
			int size, int page) {
		logger.trace("Start get all miniapp commands");
		if (!CheckFields.checkIfUserRoleIsValid(userSuperapp, userEmail, UserRole.ADMIN)) {
			logger.error("User is not admin");
			throw new BadRequestException("Only admin can get all commands");
		}
		logger.trace("End get all miniapp commands");
		return this.miniAppCommandCrud
				.findAllByMiniApp(miniAppName,
						PageRequest.of(page, size, Direction.DESC, "command", "invokcationTimestamp", "commandId"))
				.stream() // Stream<CommandEntity>
				.map(this.convertor::toBoundary) // Stream<CommandBoundary>
				.toList(); // List<CommandBoundary>
	}
}
