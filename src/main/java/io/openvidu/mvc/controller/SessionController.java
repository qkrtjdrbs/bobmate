package io.openvidu.mvc.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.openvidu.dto.SessionUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import io.openvidu.java.client.ConnectionProperties;
import io.openvidu.java.client.ConnectionType;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduRole;
import io.openvidu.java.client.Session;

import static io.openvidu.Const.*;

@Controller
@Slf4j
public class SessionController {

	// OpenVidu object as entrypoint of the SDK
	private OpenVidu openVidu;

	// Collection to pair session names and OpenVidu Session objects
	private Map<String, Session> mapSessions = new ConcurrentHashMap<>();
	// Collection to pair session names and tokens (the inner Map pairs tokens and
	// role associated)
	private Map<String, Map<String, OpenViduRole>> mapSessionNamesTokens = new ConcurrentHashMap<>();

	// URL where our OpenVidu server is listening
	private String OPENVIDU_URL;
	// Secret shared with our OpenVidu server
	private String SECRET;

	public SessionController(@Value("${openvidu.secret}") String secret,
							 @Value("${openvidu.url}") String openviduUrl) {
		this.SECRET = secret;
		this.OPENVIDU_URL = openviduUrl;
		this.openVidu = new OpenVidu(OPENVIDU_URL, SECRET);
	}

	@PostMapping("/session")
	public String joinSession(@RequestParam(name = "data") String clientData,
							  @RequestParam(name = "session-name") String sessionName,
							  Model model,
							  @SessionAttribute(name = LOGIN_USER, required = false) SessionUser loginUser) {

		if(loginUser == null){
			return "index";
		}
		System.out.println("Getting sessionId and token | {sessionName}={" + sessionName + "}");

		// Role associated to this user
		OpenViduRole role = loginUser.getOpenViduRole();

		// Optional data to be passed to other users when this user connects to the
		// video-call. In this case, a JSON with the value we stored in the HttpSession
		// object on login
		String serverData = "{\"serverData\": \"" + loginUser.getUsername() + "\"}";

		// Build connectionProperties object with the serverData and the role
		ConnectionProperties connectionProperties = new ConnectionProperties.Builder().type(ConnectionType.WEBRTC)
				.role(role).data(serverData).build();

		if (this.mapSessions.get(sessionName) != null) {
			// Session already exists
			System.out.println("Existing session " + sessionName);
			try {

				// Generate a new token with the recently created connectionProperties
				String token = this.mapSessions.get(sessionName).createConnection(connectionProperties).getToken();
				String screenToken
						= this.mapSessions.get(sessionName).createConnection(connectionProperties).getToken();

				// Update our collection storing the new token
				this.mapSessionNamesTokens.get(sessionName).put(token, role);

				// Add all the needed attributes to the template
				model.addAttribute("sessionName", sessionName);
				model.addAttribute("token", token);
				model.addAttribute("screenToken", screenToken);
				model.addAttribute("nickname", clientData);
				model.addAttribute("username", loginUser.getUsername());
				if(loginUser.getOpenViduRole().equals(OpenViduRole.PUBLISHER)){
					model.addAttribute("canPublish", true);
				} else {
					model.addAttribute("canPublish", false);
				}
				model.addAttribute("user", loginUser);

				// Return session.html template
				return "session";

			} catch (Exception e) {
				// If error just return dashboard.html template
				return "redirect:/";
			}
		} else {
			// New session
			System.out.println("New session " + sessionName);
			try {

				// Create a new OpenVidu Session
				Session session = this.openVidu.createSession();
				// Generate a new token with the recently created connectionProperties
				String token = session.createConnection(connectionProperties).getToken();
				String screenToken
						= session.createConnection(connectionProperties).getToken();

				// Store the session and the token in our collections
				this.mapSessions.put(sessionName, session);
				this.mapSessionNamesTokens.put(sessionName, new ConcurrentHashMap<>());
				this.mapSessionNamesTokens.get(sessionName).put(token, role);

				// Add all the needed attributes to the template
				model.addAttribute("sessionName", sessionName);
				model.addAttribute("token", token);
				model.addAttribute("screenToken", screenToken);
				model.addAttribute("nickname", clientData);
				model.addAttribute("username", loginUser.getUsername());
				if(loginUser.getOpenViduRole().equals(OpenViduRole.PUBLISHER)){
					model.addAttribute("canPublish", true);
				} else {
					model.addAttribute("canPublish", false);
				}
				model.addAttribute("user", loginUser);

				// Return session.html template
				return "session";

			} catch (Exception e) {
				// If error just return dashboard.html template
				log.info("openvidu server error");
				return "redirect:/";
			}
		}
	}

	@PostMapping("/leave-session")
	public String removeUser(
			@RequestParam(name = "session-name") String sessionName,
			@RequestParam(name = "token") String token,
			@SessionAttribute(name = LOGIN_USER, required = false) SessionUser loginUser
	) throws Exception {

		if(loginUser == null){
			return "index";
		}
		System.out.println("Removing user | sessioName=" + sessionName + ", token=" + token);

		// If the session exists ("TUTORIAL" in this case)
		if (this.mapSessions.get(sessionName) != null && this.mapSessionNamesTokens.get(sessionName) != null) {

			// If the token exists
			if (this.mapSessionNamesTokens.get(sessionName).remove(token) != null) {
				// User left the session
				if (this.mapSessionNamesTokens.get(sessionName).isEmpty()) {
					// Last user left: session must be removed
					this.mapSessions.remove(sessionName);
				}
				return "redirect:/";

			} else {
				// The TOKEN wasn't valid
				System.out.println("Problems in the app server: the TOKEN wasn't valid");
				return "redirect:/";
			}

		} else {
			// The SESSION does not exist
			System.out.println("Problems in the app server: the SESSION does not exist");
			return "redirect:/";
		}
	}

}
