package com.Nexus.repository;

import com.Nexus.entity.Image;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends CrudRepository<Image, Long> {

    Image findByUserEmail(String email);

}
