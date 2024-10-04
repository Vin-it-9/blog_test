package com.Nexus.controller;

import com.Nexus.entity.Image;
import com.Nexus.entity.User;
import com.Nexus.repository.UserRepo;
import com.Nexus.service.ImageService;
import com.Nexus.service.ImageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;


@Controller
public class ClientController {
    @Autowired
    private ImageService imageService;

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ImageServiceImpl imageServiceImpl;

    @GetMapping("/display")
    public ResponseEntity<byte[]> displayImage(@RequestParam("id") long id) throws IOException, SQLException
    {
        Image image = imageService.viewById(id);
        byte [] imageBytes = null;
        imageBytes = image.getImage().getBytes(1,(int) image.getImage().length());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }



//    @GetMapping("/")
//    public ModelAndView home(){
//        ModelAndView mv = new ModelAndView("index");
//        List<Image> imageList = imageService.viewAll();
//        mv.addObject("imageList", imageList);
//        return mv;
//    }


    @GetMapping("/add")
    public ModelAndView addImage(){
        return new ModelAndView("addimage");
    }

//    @PostMapping("/add")
//    public String addImagePost(@RequestParam("image") MultipartFile file) throws IOException, SerialException, SQLException {
//        byte[] bytes = file.getBytes();
//        Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
//
//        Image image = new Image();
//        image.setImage(blob);
//        imageService.create(image);
//        return "redirect:/";
//    }

//    @PostMapping("/add")
//    public String addImagePost(@RequestParam("image") MultipartFile file, Principal principal) throws IOException, SerialException, SQLException {
//
//        // Get the currently logged-in user's email
//        String email = principal.getName();
//
//        // Fetch the user by email
//        User user = userRepo.getUserByEmail(email);
//
//        byte[] bytes = file.getBytes();
//        Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
//
//        Image  image = new Image();
//        image.setImage(blob);
//        image.setUser(user);
//
//        // Save the image to the database
//        imageService.create(image);
//
//        return "redirect:/";
//    }

    @PostMapping("/add")
    public String addImagePost(@RequestParam("image") MultipartFile file, Principal principal) throws IOException, SerialException, SQLException {

        // Get the currently logged-in user's email
        String email = principal.getName();

        // Fetch the user by email
        User user = userRepo.getUserByEmail(email);

        // Convert the uploaded file into a byte array
        byte[] bytes = file.getBytes();
        Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);

        // Check if an image for this user already exists
        Image existingImage = imageServiceImpl.findByUserEmail(email);

        if (existingImage != null) {
            // If image exists, update the existing image with the new image blob
            existingImage.setImage(blob);
//            existingImage.setDate(LocalDateTime.now()); // Update the date if needed
            imageServiceImpl.update(existingImage);
        } else {
            // If no image exists, create a new Image object and associate it with the user
            Image newImage = new Image();
            newImage.setImage(blob);      // Set the image blob
            newImage.setUser(user);       // Set the user who uploaded the image
//            newImage.setDate(LocalDateTime.now());

//            0xFFD8FFE000104A46494600010100000100010000FFDB008
            // Save the new image to the database
            imageService.create(newImage);
        }

        return "redirect:/";
    }







}
