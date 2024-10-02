package com.Nexus.controller;

import java.io.IOException;
import java.security.Principal;

import com.Nexus.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.Nexus.entity.User;
import com.Nexus.repository.UserRepo;
import com.Nexus.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class HomeController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private UserServiceImpl userServiceImpl;

	@ModelAttribute
	public void commonUser(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			User user = userRepo.findByEmail(email);
			m.addAttribute("user", user);
		}

	}

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/register")
	public String register() {
		return "register";
	}

	@GetMapping("/signin")
	public String login() {
		return "login";
	}

	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute User user, HttpSession session, Model m, HttpServletRequest request) {

		String url = request.getRequestURL().toString();

		url = url.replace(request.getServletPath(), "");

		User savedUser = userService.saveUser(user, url);

		if (savedUser == null) {
			session.setAttribute("msg", "Email already exists. Please use a different email or login.");
			return "redirect:/register";
		}
		session.setAttribute("msg", "Registered successfully! Please check your email to verify your account.");
		return "redirect:/signin";
	}


	@GetMapping("/verify")
	public String verifyAccount(@Param("code") String code, Model m) {

		boolean f = userService.verifyAccount(code);

		if (f) {
			m.addAttribute("msg", "Sucessfully your account is verified");
		} else {
			m.addAttribute("msg", "may be your vefication code is incorrect or already veified ");
		}

		return "message";
	}

	@GetMapping("/editProfile")
	public String showEditProfilePage(Model model, Principal principal) {

		String email = principal.getName();
		User user = userRepo.getUserByEmail(email);

		model.addAttribute("user", user);

		return "editProfile";

	}

//	@PostMapping("/updateProfile")
//	public String updateProfile(@ModelAttribute User user, Principal principal, HttpSession session) {
//
//		String email = principal.getName();
//
//		boolean isUpdated = userServiceImpl.updateUserProfile(user, email);
//
//		if (isUpdated) {
//			session.setAttribute("msg", "Profile updated successfully.");
//		} else {
//			session.setAttribute("msg", "Error updating profile.");
//		}
//
//		return "redirect:/editProfile";
//	}

	@PostMapping("/updateProfile")
	public String updateProfile(@ModelAttribute User user,
								@RequestParam("profileImage") MultipartFile profileImage,
								Principal principal,
								HttpSession session) {

		String email = principal.getName(); // Retrieve logged-in user's email

		try {
			// Fetch the existing user using the service method
			User existingUser = userServiceImpl.getUserByEmail(email); // Using service method

			if (existingUser != null) {
				// Update non-image fields
				existingUser.setName(user.getName());
				existingUser.setMobileNo(user.getMobileNo());

				// Handle the profile image if it's uploaded
				if (profileImage != null && !profileImage.isEmpty()) {
					byte[] imageBytes = profileImage.getBytes();
					existingUser.setProfileImage(imageBytes); // Set the byte array to the user
				}

				// Update the user
				boolean isUpdated = userServiceImpl.updateUserProfile(existingUser);

				if (isUpdated) {
					session.setAttribute("msg", "Profile updated successfully.");
				} else {
					session.setAttribute("msg", "Error updating profile.");
				}
			} else {
				session.setAttribute("msg", "User not found.");
			}
		} catch (IOException e) {
			session.setAttribute("msg", "Error processing the image file.");
			e.printStackTrace();
		}

		return "redirect:/editProfile";
	}











}
