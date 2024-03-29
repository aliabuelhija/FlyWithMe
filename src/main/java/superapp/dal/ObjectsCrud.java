package superapp.dal;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import superapp.data.SuperAppObjectEntity;

public interface ObjectsCrud extends ListCrudRepository<SuperAppObjectEntity, String> {
	
	List<SuperAppObjectEntity> findAll(Pageable pageable);

    List<SuperAppObjectEntity> findAllByActiveTrue(Pageable pageable);

	List<SuperAppObjectEntity> findAllByObjectParents_ObjectId(String parentId, Pageable pageable);

	List<SuperAppObjectEntity> findAllByObjectParents_ObjectIdAndActive(String parentId, boolean active, Pageable pageable);
	
	List<SuperAppObjectEntity> findAllByObjectChildrens_ObjectId(String childId, Pageable pageable);

	List<SuperAppObjectEntity> findAllByObjectChildrens_ObjectIdAndActive(String childId, boolean active, Pageable pageable);

	List<SuperAppObjectEntity> findAllByType(@Param("type") String type, Pageable pageable);

	List<SuperAppObjectEntity> findAllByTypeAndActiveTrue(@Param("type") String type, Pageable pageable);

	List<SuperAppObjectEntity> findAllByAlias(@Param("alias") String alias, Pageable pageable);

    List<SuperAppObjectEntity> findAllByAliasAndActiveTrue(String alias, Pageable pageable);

	List<SuperAppObjectEntity> findAllByLatBetweenAndLngBetween(@Param("latMin") Double latMin,
			@Param("latMax") Double latMax, @Param("lngMin") Double lngMin, @Param("lngMax") Double lngMax,
			Pageable pageable);

	List<SuperAppObjectEntity> findAllByLatBetweenAndLngBetweenAndActive(@Param("latMin") Double latMin,
			@Param("latMax") Double latMax, @Param("lngMin") Double lngMin, @Param("lngMax") Double lngMax,
			@Param("active") boolean active, Pageable pageable);

	@Query("{ 'location': { $geoWithin: { $centerSphere: [ [ ?0, ?1 ], ?2 ] } } }")
	List<SuperAppObjectEntity> findObjectsByRadius(@Param("lng") double lng, @Param("lat") double lat, @Param("radius") double radius, Pageable pageable);
	
	@Query("{ 'location': { $geoWithin: { $centerSphere: [ [ ?0, ?1 ], ?2 ] } }, active: true }")
	List<SuperAppObjectEntity> findObjectsByRadiusAndActive(@Param("lng") double lng, @Param("lat") double lat, @Param("radius") double radius, PageRequest pageable);
}
