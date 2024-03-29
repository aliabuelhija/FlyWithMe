package superapp.dal;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import superapp.data.UserEntity;

public interface UserCrud extends ListCrudRepository<UserEntity, String> {
	
	List<UserEntity> findAll(Pageable pageable);
}