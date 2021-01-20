package top.aengus.panther.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.aengus.panther.dao.ImageRepository;
import top.aengus.panther.model.ImageModel;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/1/20
 */
@Service
public class ImageService {

    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public List<ImageModel> findAllImages() {
        return imageRepository.findAll().stream()
                .filter(imageModel -> !imageModel.isDeleted())
                .collect(Collectors.toList());
    }

    public List<ImageModel> findAllDeletedImages() {
        return imageRepository.findAll().stream()
                .filter(ImageModel::isDeleted)
                .collect(Collectors.toList());
    }

    public void insertImage(ImageModel imageModel) {
        imageRepository.save(imageModel);
    }

    public void deleteImage(ImageModel imageModel) {
        imageModel.delete();
        imageRepository.save(imageModel);
    }
}
