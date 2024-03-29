package superapp.logic.MDBservices;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import superapp.dal.ObjectsCrud;
import superapp.dal.UserCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.data.UserRole;
import superapp.logic.exceptions‬.ObjectNotFoundException;
import superapp.logic.mini_app.MiniAppCommandBoundary;
import superapp.logic.mini_app.TargetObject;
import superapp.logic.objects.ObjectBoundary;
import superapp.logic.users.NewUserBoundary;

@Component
public class CheckFields {
    private static UserCrud userCrud;
    private static ObjectsCrud objectCrud;

    @Autowired
    public CheckFields(UserCrud userCrud, ObjectsCrud objectCrud) {
        CheckFields.userCrud = userCrud;
        CheckFields.objectCrud = objectCrud;
    }
    
// Check if the string is valid email
	public static boolean isValidEmail(String email) {
		if(email == null)
			return false;
		String emailRegex = "^[\\w\\.-]+@([\\da-z\\.-]+)\\.([a-z\\.]{2,6})$"; // Valid pattern of email
		Pattern pattern = Pattern.compile(emailRegex);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

// Check if the input is valid user role
	public static boolean isValidUserRole(String input) {
		try {
			UserRole.valueOf(input);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
// Check fields to create user
	public static boolean checkFieldsToCreateUser(NewUserBoundary newUser) {
		if (!CheckFields.isValidEmail(newUser.getEmail()) // Check email is valid
				|| newUser.getUsername() == null // Check username is not null
				|| newUser.getUsername().isBlank() // Check username is not empty
				|| newUser.getAvatar() == null // Check avatar is not null
				|| newUser.getAvatar().isEmpty() // Check avatar is not empty
				|| newUser.getRole() == null // Check role is not null
				|| !CheckFields.isValidUserRole(newUser.getRole().toString())) // Check the role is valid
			return false;
		return true;
	}
	
// Check fields to create object
	public static boolean checkFieldsToCreateObject(ObjectBoundary object) {
		if (object.getCreatedBy() == null // Check created by is not null
				|| object.getCreatedBy().getUserId() == null // Check user id is not null
				|| object.getCreatedBy().getUserId().getEmail() == null // Check email is not null
				|| !CheckFields.isValidEmail(object.getCreatedBy().getUserId().getEmail()) // Check email is valid
				|| object.getAlias() == null // Check alias is not null
				|| object.getAlias().isBlank() // Check alias is not empty
				|| object.getType() == null // Check type is not null
				|| object.getType().isBlank()) // Check type is not empty
			return false;
		return true;
	}
	
// Check invoke command input
	public static boolean checkInvokeCommandInput(MiniAppCommandBoundary miniAppCommandBoundary) {
	    if (miniAppCommandBoundary == null) {
	        return false;
	    }
	    if (miniAppCommandBoundary.getCommand() == null 
	            || miniAppCommandBoundary.getCommand().isBlank()
	            || miniAppCommandBoundary.getInvokedBy() == null
	            || miniAppCommandBoundary.getInvokedBy().getUserId() == null
	            || miniAppCommandBoundary.getInvokedBy().getUserId().getEmail() == null
	            || !CheckFields.isValidEmail(miniAppCommandBoundary.getInvokedBy().getUserId().getEmail())
	            || miniAppCommandBoundary.getInvokedBy().getUserId().getSuperapp() == null
	            || miniAppCommandBoundary.getInvokedBy().getUserId().getSuperapp().isBlank()
	            || miniAppCommandBoundary.getTargetObject() == null
	            || miniAppCommandBoundary.getTargetObject().getObjectId() == null
	            || miniAppCommandBoundary.getTargetObject().getObjectId().getInternalObjectId() == null
	            || miniAppCommandBoundary.getTargetObject().getObjectId().getInternalObjectId().isBlank()
	            || miniAppCommandBoundary.getTargetObject().getObjectId().getSuperapp() == null
	            || miniAppCommandBoundary.getTargetObject().getObjectId().getSuperapp().isBlank()
	            || miniAppCommandBoundary.getCommandId() == null
	            || miniAppCommandBoundary.getCommandId().getMiniApp() == null
	            || miniAppCommandBoundary.getCommandId().getMiniApp().isBlank()) {
	        return false;
	    }
	    return true;
	}
	
// Check if location is less than radius from center
	public static  boolean checkIfInTheCircle(double center_lat, double center_long, double lat, double lng, double radius) {
		double distance = Math.sqrt(Math.pow(center_lat - lat, 2) + Math.pow(center_long - lng, 2));
		return distance <= radius;
	}
	
// Get user role
	public static boolean checkIfUserRoleIsValid(String userSuperapp, String userEmail, UserRole userRole) {
		return (userCrud.findById(userSuperapp + "#" + userEmail) // Find the user
				.orElseThrow(() -> new ObjectNotFoundException("User is not exist")) // Not find user
				.getRole())	== userRole; // Check if roles are same
	}
	
// Check if target object is exist and active
	public static boolean checkIfTargetObjectIsExistAndActive(MiniAppCommandBoundary miniAppCommandBoundary) {
	    TargetObject targetObject = miniAppCommandBoundary.getTargetObject();

	    if (targetObject == null || targetObject.getObjectId() == null)
	        return false; // Target object or object ID is missing

	    String superApp = targetObject.getObjectId().getSuperapp();
	    String internalID = targetObject.getObjectId().getInternalObjectId();

	    Optional<SuperAppObjectEntity> objectOptional = objectCrud.findById(superApp + "#" + internalID);
	    if (objectOptional.isEmpty() || !objectOptional.get().getActive())
	        return false; // Object not found or not active

	    return true; // Target object is found and active
	}
}