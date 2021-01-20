package top.aengus.panther.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.aengus.panther.model.ImageModel;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/1/20
 */
public interface ImageRepository extends JpaRepository<ImageModel, Long> {
}
