package superapp.dal;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import superapp.data.MiniAppCommandEntity;

public interface MiniAppCommandCrud extends ListCrudRepository<MiniAppCommandEntity, String>, PagingAndSortingRepository<MiniAppCommandEntity, String> {
	
	List<MiniAppCommandEntity> findAllByMiniApp(@Param("miniApp") String miniApp, Pageable pageable);
}