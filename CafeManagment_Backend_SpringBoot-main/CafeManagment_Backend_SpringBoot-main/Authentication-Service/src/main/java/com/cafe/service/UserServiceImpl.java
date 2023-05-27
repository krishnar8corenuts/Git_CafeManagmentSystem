package com.cafe.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.cafe.jwt.CustomerUserDetailsService;
import com.cafe.jwt.JwtFilter;
import com.cafe.jwt.JwtUtil;
import com.cafe.constants.CafeConstants;
import com.cafe.dao.UserDao;
import com.cafe.model.User;
import com.cafe.utils.CafeUtils;
import com.cafe.wrapper.UserWrapper;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@CrossOrigin
public class UserServiceImpl implements UserService {

	@Autowired
	UserDao userDao;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	CustomerUserDetailsService customerUserDetailsService;

	@Autowired
	JwtUtil jwtUtil;

	@Autowired
	JwtFilter jwtFilter;
	
	@Autowired
	PasswordEncoder passwordEncoder;


	@Autowired
	private EmailServiceImpl emailServiceImpl;

	@Override
	public ResponseEntity<String> signUp(Map<String, String> formData) // name: value
	{
		log.info("Inside Signup {}", formData);

		try {
			if (validateSignUpMap(formData))// true
			{
				log.info("Inside Validate {}", formData); // form data
				User user = userDao.findByEmailId(formData.get(CafeConstants.EMAIL)); // data from database //new -->null
																			// //alreadypresent-->not null
				if (Objects.isNull(user)) // true //false
				{
					userDao.save(getUserFromMap(formData));
					return CafeUtils.getResponseEntity("SignUp is SuccessFull", HttpStatus.OK);
				} else {
					return CafeUtils.getResponseEntity("Email already Exist", HttpStatus.BAD_REQUEST);
				}
			} else {
				return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/* Used For validating the signUp Form */
	@SuppressWarnings("unused")
	private boolean validateSignUpMap(Map<String, String> requestMap) 
	{
		return requestMap.containsKey("name") 
				&& requestMap.containsKey("contactNumber") 
				&& requestMap.containsKey(CafeConstants.EMAIL)
				&& requestMap.containsKey(CafeConstants.PASSWORD);
	}

	/*
	 * GetData Of One User For Validation ,It is present or not in the Database
	 */

	private User getUserFromMap(Map<String, String> requestMap) {
		User user = new User();
		user.setName(requestMap.get("name"));
		user.setContactNumber(requestMap.get("contactNumber"));
		user.setEmail(requestMap.get(CafeConstants.EMAIL));
		user.setPassword(requestMap.get(CafeConstants.PASSWORD));
		user.setStatus("inactive");
		user.setRole("user");
		return user;
	}

	/*************** Login ******************/
	@Override
	public ResponseEntity<String> login(Map<String, String> requestMap) {
		log.info("Inside Login {}", requestMap);
		try {
			Authentication auth = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(requestMap.get(CafeConstants.EMAIL), requestMap.get(CafeConstants.PASSWORD)));
			if (auth.isAuthenticated()) // true data //false
			{
				if (customerUserDetailsService.getUserDetail().getStatus().equalsIgnoreCase("active")) {
					log.info(" Authentication Success");
					return new ResponseEntity<>(
							"{\"token\":\""
									+ jwtUtil.generateToken(customerUserDetailsService.getUserDetail().getEmail(),
											customerUserDetailsService.getUserDetail().getRole())
									+ "\"}",
							HttpStatus.OK);
				} else {
					log.info(" Authentication Failed");
					return new ResponseEntity<>("{\"message\":\"" + "Wait for Admin Approval." + "\"}",
							HttpStatus.BAD_REQUEST);
				}
			} else {
				log.info("Something Went Wrong");
			}
		} catch (Exception e) {
			log.error("{}", e);
		}
		return new ResponseEntity<>("{\"message\":\"" + "Bad Credentials" + "\"}", HttpStatus.BAD_REQUEST);
	}

	/***************
	 * Getting Data By Admin
	 ******************/
	/*************** GetAllUser ******************/
	@Override
	public ResponseEntity<List<UserWrapper>> getAllUser() {
		try {
			if (jwtFilter.isAdmin()) { // true
				return new ResponseEntity<>(userDao.getAllUser(), HttpStatus.OK);
			} else {
				return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> update(Map<String, String> requestMap) {

		try {
			if (jwtFilter.isAdmin()) {
				Optional<User> optional = userDao.findById(Integer.parseInt(requestMap.get("id")));
				if (!optional.isEmpty()) {
					log.info("Status ::" + requestMap.get(CafeConstants.STATUS));

					if (requestMap.get(CafeConstants.STATUS).equals("true")) {
						log.info("Active Status ::" + requestMap.get(CafeConstants.STATUS));
						userDao.updateStatus("active", Integer.parseInt(requestMap.get("id")));
					} else {
						userDao.updateStatus("inactive", Integer.parseInt(requestMap.get("id")));
						log.info("InStatus ::" + requestMap.get(CafeConstants.STATUS));

					}
					return CafeUtils.getResponseEntity("User Status Updated Successfully", HttpStatus.OK);
				} else {
					return CafeUtils.getResponseEntity("User id doesnot Exist", HttpStatus.OK);
				}
			} else {
				return CafeUtils.getResponseEntity(CafeConstants.UNATHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@Override
	public ResponseEntity<String> checkToken() {
		return CafeUtils.getResponseEntity("true", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
		try {
			User userObj = userDao.findByEmail(jwtFilter.getCurrentUser());
			if (userObj != null ) {
				if (userObj.getPassword().equals(requestMap.get("oldPassword"))) {
					userObj.setPassword(requestMap.get("newPassword"));
					log.info("USer Object " + userObj);
					userDao.save(userObj);
					return CafeUtils.getResponseEntity("Password Updated Successfully", HttpStatus.OK);
				}
				return CafeUtils.getResponseEntity("Incorrect Old Password", HttpStatus.BAD_REQUEST);
			}
			return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	Random random = new Random(1000);

	@Override
	public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
		try {
			log.info("generateOtp  {}", requestMap);
			User userObj = userDao.findByEmail(requestMap.get(CafeConstants.EMAIL));
			if (!Objects.isNull(userObj) && !Strings.isNullOrEmpty(userObj.getEmail())) {
				int otp = random.nextInt(999999);
//				Sending OTP as Message
				String message = " Otp :" + otp + " Previous Password : " + userObj.getPassword();
//				Subject
				String subject = "OTP from Cafe Managment System";
				String to = requestMap.get(CafeConstants.EMAIL);
				// write code for send otp to email
				boolean flag = this.emailServiceImpl.sendEmail(message, subject, to);
				if (flag) {
					log.info("IF {} ", flag);
				} else {
					log.info("Else {} ", flag);
				}

				log.info("OTP  {}", otp);
				return CafeUtils.getResponseEntity("Check Your Mail For OTP", HttpStatus.OK);
			} else {
				return CafeUtils.getResponseEntity("Email Not Exists", HttpStatus.OK);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

	}

}
