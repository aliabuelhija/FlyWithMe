package superapp.logic.MDBservices;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jms.core.JmsTemplate;
import superapp.dal.UserCrud;
import superapp.data.UserEntity;
import superapp.data.UserRole;
import superapp.logic.UsersServiceExtend;
import superapp.logic.converters.UsersConvertor;
import superapp.logic.exceptions‬.BadRequestException;
import superapp.logic.exceptions‬.ConflictException;
import superapp.logic.exceptions‬.DeprecatedOperationException;
import superapp.logic.exceptions‬.ObjectNotFoundException;
import superapp.logic.users.NewUserBoundary;
import superapp.logic.users.UserBoundary;

@Service
public class UserServiceMdb implements UsersServiceExtend {
	private UserCrud userCrud;
	private UsersConvertor userConverter;
	private String superAppName;
	private JmsTemplate jmsTemplate;
	private Log logger = LogFactory.getLog(UserServiceMdb.class);

	@Autowired
	public UserServiceMdb(UserCrud userCrud, UsersConvertor userConverter, JmsTemplate jmsTemplate) {
		this.userCrud = userCrud;
		this.userConverter = userConverter;
		this.jmsTemplate = jmsTemplate;
		this.jmsTemplate.setDeliveryDelay(3000L);
	}

// Set superapp name
	@Value("${spring.application.name}")
	public void setSuperAppName(String superApp) {
		this.superAppName = superApp;
	}

// Create user
	@Override
	public UserBoundary createUser(NewUserBoundary newUser) {
		logger.info("Start create user");
		logger.trace("Start checking the fields for user are valid");
		if (!CheckFields.checkFieldsToCreateUser(newUser)) { // Check if the fields are valid
			logger.error("The fields for user are not valid");
			throw new BadRequestException("The fields for user are not valid");
		}
		Optional<UserEntity> CheckIfAlreadySigned = userCrud.findById(superAppName + newUser.getEmail());

		if (CheckIfAlreadySigned.isPresent()) { // Check if the user is already signed
			logger.error("User is already signed");
			throw new ConflictException("User is already signed");
		}

		logger.trace("End checking the fields for user are valid");
		logger.trace("Make user boundary");
		UserBoundary userBoundary = new UserBoundary(superAppName, newUser.getEmail(), newUser.getRole(),
				newUser.getUsername(), newUser.getAvatar());
		logger.trace("Convert boundary to entity");
		UserEntity userEntity = this.userConverter.toEntity(userBoundary);
		logger.trace("Saving the entity to db");
		userEntity = this.userCrud.save(userEntity);
		logger.info("End create user");
		return userConverter.toBoundary(userEntity);
	}

// Update user
	@Override
	public UserBoundary updateUser(String userSuperApp, String userEmail, UserBoundary update) {
		logger.info("Start update user");
		UserEntity entity = userConverter.toEntity(getUserBoundary(userSuperApp + "#" + userEmail));

		// Update avatar
		if (update.getAvatar() != null && !update.getAvatar().isEmpty())
			entity.setAvatar(update.getAvatar());

		// Update role
		if (update.getRole() != null && CheckFields.isValidUserRole(update.getRole().toString()))
			entity.setRole(update.getRole());

		// Update username
		if (update.getUsername() != null && !update.getUsername().isEmpty())
			entity.setUserName(update.getUsername());

		logger.trace("Save the update user");
		entity = this.userCrud.save(entity);
		logger.info("End update user");
		return this.userConverter.toBoundary(entity);
	}

// Login
	@Override
	public UserBoundary login(String userSuperApp, String userEmail) {
		return getUserBoundary(userSuperApp + "#" + userEmail);
	}

// Delete all users - old
	@Override
	@Deprecated
	public void deleteAllUsers() {
		logger.error("Start delete all users - Deprecated");
		throw new DeprecatedOperationException();
	}

// Delete all users - new
	@Override
	public void deleteAllUsers(String userSuperapp, String userEmail) {
		logger.info("Start delete all users");
		if (!CheckFields.checkIfUserRoleIsValid(userSuperapp, userEmail, UserRole.ADMIN)) {
			logger.error("User is not admin");
			throw new BadRequestException("Only admin can delete all users");
		}
		userCrud.deleteAll();
		logger.info("End delete all users");
	}

// Get all users - old
	@Override
	@Deprecated
	public List<UserBoundary> getAllUsers() {
		logger.error("Start get all users - Deprecated");
		throw new DeprecatedOperationException();
	}

// Get all users - new
	@Override
	public List<UserBoundary> getAllUsers(String userSuperapp, String userEmail, int size, int page) {
		logger.info("Start get all users");
		if (!CheckFields.checkIfUserRoleIsValid(userSuperapp, userEmail, UserRole.ADMIN)) {
			logger.error("User is not admin");
			throw new BadRequestException("Only admin can get all users");
		}
		logger.info("End get all users");
		return userCrud.findAll(PageRequest.of(page, size, Direction.DESC, "username", "userId")).stream()
				.map(this.userConverter::toBoundary).collect(Collectors.toList());
	}

// Get user boundary
	public UserBoundary getUserBoundary(String id) {
		logger.info("Start get user boundary");
		Optional<UserEntity> op = this.userCrud.findById(id);

		if (op.isPresent()) {
			UserEntity entity = op.get();
			logger.info("End get user boundary");
			return userConverter.toBoundary(entity);
		} else {
			logger.error("User not found");
			throw new ObjectNotFoundException("User not found");
		}
	}
}