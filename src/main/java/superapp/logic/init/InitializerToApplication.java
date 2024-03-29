package superapp.logic.init;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import superapp.data.DefaultValues;
import superapp.data.UserRole;
import superapp.logic.MiniAppCommandsServiceExtend;
import superapp.logic.ObjectsServiceExtend;
import superapp.logic.UsersServiceExtend;
import superapp.logic.mini_app.CommandId;
import superapp.logic.mini_app.InvokedBy;
import superapp.logic.mini_app.MiniAppCommandBoundary;
import superapp.logic.mini_app.TargetObject;
import superapp.logic.objects.CreatedBy;
import superapp.logic.objects.ObjectBoundary;
import superapp.logic.objects.ObjectId;
import superapp.logic.users.NewUserBoundary;
import superapp.logic.users.UserBoundary;

@Component
@Profile("ApplicationInitializer")
public class InitializerToApplication implements CommandLineRunner {
	
	private MiniAppCommandsServiceExtend miniAppCommandsServiceExtend;
	private ObjectsServiceExtend objectsServiceExtend;
	private UsersServiceExtend usersServiceExtend;
	private Log logger = LogFactory.getLog(InitializerToApplication.class);
	
	@Autowired
	public InitializerToApplication(MiniAppCommandsServiceExtend miniAppCommands, ObjectsServiceExtend objects, UsersServiceExtend users) {
		super();
		this.miniAppCommandsServiceExtend = miniAppCommands;
		this.objectsServiceExtend = objects;
		this.usersServiceExtend = users;
	}
	
	@Override
	public void run(String... args) throws Exception {
		logger.info("Start initialize");
		logger.trace("Start to put admin user");
		usersServiceExtend.createUser(new NewUserBoundary(DefaultValues.DEFAULT_ADMIN_EMAIL, UserRole.ADMIN, DefaultValues.DEFAULT_ADMIN_USERNAME, DefaultValues.DEFAULT_ADMIN_AVATAR));
		logger.trace("End to put admin users");
		logger.trace("Start to put superapp users");
		UserBoundary defaultSuperappUserBoundary = usersServiceExtend.createUser(new NewUserBoundary(DefaultValues.DEFAULT_SUPERAPP_USER_EMAIL, UserRole.SUPERAPP_USER, DefaultValues.DEFAULT_SUPERAPP_USER_USERNAME, DefaultValues.DEFAULT_SUPERAPP_USER_AVATAR));
		logger.trace("End to put superapp users");
		logger.trace("Start to put miniapp users");
		UserBoundary defaultMiniappUserBoundary = usersServiceExtend.createUser(new NewUserBoundary(DefaultValues.DEFAULT_MINIAPP_USER_EMAIL, UserRole.MINIAPP_USER, DefaultValues.DEFAULT_MINIAPP_USER_USERNAME, DefaultValues.DEFAULT_MINIRAPP_USER_AVATAR));
		logger.trace("End to put miniapp users");
		logger.trace("Start to put objects");
		ObjectBoundary defaultObjectBoundary = objectsServiceExtend.createObject(new ObjectBoundary(null, DefaultValues.DEFAULT_OBJECT_TYPE, DefaultValues.DEFAULT_OBJECT_ALIAS, null, null, null, new CreatedBy(defaultSuperappUserBoundary.getUserId()), null));
		logger.trace("End to put objects");
		logger.trace("Start to put miniapp commands");
		MiniAppCommandBoundary defaultminiAppCommandBoundary = new MiniAppCommandBoundary(DefaultValues.DEFAULT_MINIAPP_COMMAND_COMMAND, 
				new TargetObject(new ObjectId(defaultObjectBoundary.getObjectId().getSuperapp(), defaultObjectBoundary.getObjectId().getInternalObjectId())), 
				new InvokedBy(defaultMiniappUserBoundary.getUserId()));
		defaultminiAppCommandBoundary.setCommandId(new CommandId(DefaultValues.DEFAULT_MINIAPP_COMMAND_MINIAPP));
		miniAppCommandsServiceExtend.invokeCommand(defaultminiAppCommandBoundary);
		logger.trace("End to put miniapp commands");
		logger.info("End initialize");
	}
}