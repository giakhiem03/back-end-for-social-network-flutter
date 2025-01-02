package com.example.demo.controller;

import com.example.demo.DTO.*;
import com.example.demo.DTO.UserDTO;
import com.example.demo.models.*;
import com.example.demo.services.*;
import com.example.demo.token.AuthResponse;
import com.example.demo.token.JwtTokenProvider;
import com.example.demo.token.JwtUtil;
import lombok.Getter;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

//import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PostService postService;
    @Autowired
    private CommentsService commentsService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationCateService notificationCateService;
    @Autowired
    private FriendsService friendsService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginDTO loginRequest) {
        // Create an instance of BCryptPasswordEncoder
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Find the user by username
        Optional<User> user = userService.getAll().stream()
                .filter(u -> u.getUsername().equals(loginRequest.getUsername()))
                .findFirst();

        // Check if user exists and if the password matches
        if (user.isPresent() && passwordEncoder.matches(loginRequest.getPassword(), user.get().getPassword())) {
            // Set user status to active
            user.get().setStatus(true);
            try {
                System.out.println("Generating token for user: " + user.get().getUsername());
                String token = jwtTokenProvider.generateJwtToken(user.get().getUsername());
                System.out.println("Token generated successfully: " + token);
                user.get().setToken(token);

            }

            catch (Exception e) { e.printStackTrace(); return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();}

            userService.saveUser(user.get());

            // Return success response
            return ResponseEntity.ok(user.get());
        } else {
            // Return error if username or password is invalid
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody UserDTO userDTO) {
        User u = userService.getUserByUsername(userDTO.getUsername());
        if(u==null){
            User user = new User();
            user.setFullName(userDTO.getFullName());
            user.setUsername(userDTO.getUsername());

            // Hash the password before setting it
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(userDTO.getPassword());

            user.setPassword(hashedPassword);
            user.setEmail(userDTO.getEmail());
            user.setPhoneNumber(userDTO.getPhoneNumber());
            user.setImage(userDTO.getImage());
            user.setBackgroundImage(userDTO.getBackgroundImage());
            user.setRole(roleService.getRoles(userDTO.getRole()));
            user.setStatus(userDTO.isStatus());
            Set<Post> posts = new HashSet<>();
            for (var p : userDTO.getPosts()){
                posts.add(postService.getPostById(p));
            }
            user.setPosts(posts);
            return ResponseEntity.ok(userService.saveUser(user));
        } else {
            // Nếu username đã tồn tại, trả về mã lỗi 409 Conflict với thông điệp rõ ràng
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/get-ipv4")
    public String getIpv4() {
        try {
            for (NetworkInterface network : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for (InetAddress inetAddress : Collections.list(network.getInetAddresses())) {
                    if (!inetAddress.isLoopbackAddress() && inetAddress.getAddress().length == 4) {
                        return "http://"+inetAddress.getHostAddress() + ":8080";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "not found";
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        if (userService.getUserById(id).isPresent()) {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/updateUser")
    public ResponseEntity<User> updateUser(int userId,String fullName,String email,String phoneNumber,
                                             MultipartFile profileImage, MultipartFile backgroundImage) {
        try {
            User user = userService.getUserById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPhoneNumber(phoneNumber);
            String uploadDir = "static/images/users/" + userId + "/";
            if(profileImage != null) {
                String fileNameAvatar = UUID.randomUUID().toString() + "_" + profileImage.getOriginalFilename();
                // Tạo tên file duy nhất bằng UUID và giữ lại tên gốc của file

                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath); // Tạo thư mục nếu chưa tồn tại
                }

                Path filePathAvatar = uploadPath.resolve(fileNameAvatar);
                Files.copy(profileImage.getInputStream(), filePathAvatar, StandardCopyOption.REPLACE_EXISTING);
                // Lưu URL vào DB
                String Ipv4 = getIpv4();
                String avatar = Ipv4 + "/images/users/" + userId + "/" + fileNameAvatar;
                user.setImage(avatar);
            }

            if(backgroundImage != null) {
                String fileNameBackground = UUID.randomUUID().toString() + "_" + backgroundImage.getOriginalFilename();

                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath); // Tạo thư mục nếu chưa tồn tại
                }

                Path filePathBackground = uploadPath.resolve(fileNameBackground);
                Files.copy(profileImage.getInputStream(), filePathBackground, StandardCopyOption.REPLACE_EXISTING);
                // Lưu URL vào DB
                String Ipv4 = getIpv4();
                String background = Ipv4 + "/images/users/" + userId + "/" + fileNameBackground;
                user.setBackgroundImage(background);
            }
            userService.saveUser(user);

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/posts/image")
    public ResponseEntity<String> uploadPostImage(@RequestParam MultipartFile postImage) {
        try {
            // Xử lý lưu ảnh
            Path path = Paths.get("static/images/posts/");
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            String fileName = UUID.randomUUID().toString() + "_" + postImage.getOriginalFilename();
            Path pathImage = path.resolve(fileName);
            Files.copy(postImage.getInputStream(), pathImage);
            return ResponseEntity.ok(getIpv4()+"/images/posts/"+fileName); // Trả về tên file để sử dụng trong bài viết
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi!");
        }
    }

    // Các API của bài post

    @PostMapping("/posts")
    public ResponseEntity<String> uploadPostContent(@RequestBody PostDTO postDTO) {
        try {
            Set<User> user = new HashSet<>();
            for (var u : postDTO.getUsers()) {
                user.add(userService.getUserById(u).orElse(null));
            }
            Post post = new Post();
            post.setUserUpLoad(userService.getUserById(postDTO.getUserUpLoad()).orElse(null));
            post.setPostImage(postDTO.getPostImage());
            post.setCaption(postDTO.getCaption());
            post.setReactionQuantity(postDTO.getReactionQuantity());
            post.setPostedTime(postDTO.getPostedTime());
            post.setUsers(user);
            // Lưu bài viết
            postService.savePost(post);
            return ResponseEntity.ok("Đăng bài thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi!");
        }
    }

    @GetMapping("/getAllPosts")
    public ResponseEntity<List<Post>> getAllPosts() {
        try {
            return ResponseEntity.ok(postService.getAllPosts());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @PostMapping("/{postId}/toggle-like/{userId}")
    public ResponseEntity<Post> toggleLike(@PathVariable int postId, @PathVariable int userId) {
        try {
            Post new_post = postService.toggleLike(postId, userId);
            return ResponseEntity.ok(new_post);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/createCmt")
    public ResponseEntity<List<Comments>> createCmt(int post, int user, String content, MultipartFile image) {
        try {
            Optional<Comments> commentsWithMaxId = commentsService.getAllCmt().stream()
                    .max(Comparator.comparingInt(Comments::getId));
            int id;
            id = commentsWithMaxId.map(cmt -> cmt.getId() + 1).orElse(1);

            User new_user = userService.getUserById(user).orElse(null);
            Post new_post = postService.getPostById(post);

            Comments comment = new Comments();
            comment.setUser(new_user);
            comment.setPost(new_post);
            if(image == null) {
                comment.setContent(content);
            } else {
                String uploadDir = "static/images/comment/" + id + "/";

                String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                // Tạo tên file duy nhất bằng UUID và giữ lại tên gốc của file

                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath); // Tạo thư mục nếu chưa tồn tại
                }

                Path filePathAvatar = uploadPath.resolve(fileName);
                Files.copy(image.getInputStream(), filePathAvatar, StandardCopyOption.REPLACE_EXISTING);
                // Lưu URL vào DB
                String Ipv4 = getIpv4();
                String avatar = Ipv4 + "/images/comment/" + id + "/" + fileName;
                comment.setContent(avatar);
            }
            commentsService.save(comment);

            // Tạo thông báo
            Notification notification = new Notification();
            notification.setUserIdSend(userService.getUserById(user).orElse(null));
            Post posts = postService.getPostById(post);
            notification.setUserIdReceive(userService.getUserById(posts.getUserUpLoad().getUserId()).orElse(null));
            notification.setNotificationCategory(notificationCateService.getById(2)); // Comment : 2
            notificationService.save(notification);

            return ResponseEntity.ok(
                    commentsService.getAllCmt()
                            .stream()
                            .filter(p -> (p.getPost().getPostId() == post))
                            .toList()  // Collect matching messages into a list
            );
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/comments")
    public ResponseEntity<List<Comments>> getAllCmts() {
        try {
            return  ResponseEntity.ok(commentsService.getAllCmt());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getAllNotes() {
        try {
            return ResponseEntity.ok(notificationService.getAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    //Search Users
    @GetMapping("/search/{name}")
    public ResponseEntity<List<User>> searchAllUsers(@PathVariable String name) {
        try {
            List<User> u = userService.searchAllByName(name);
            return ResponseEntity.ok(u);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @PostMapping("/addFriend")
    public ResponseEntity<Friends> addFriend(@RequestBody FriendsDTO friendsDTO) {
        try {
            Friends friends = new Friends();
            friends.setUserIdSend(userService.getUserById(friendsDTO.getUserIdSend()).orElse(null));
            friends.setUserIdReceive(userService.getUserById(friendsDTO.getUserIdReceive()).orElse(null));
            friends.setStatusRelationship(friendsDTO.getStatusRelationship());
            friendsService.save(friends);
            return ResponseEntity.ok(friends);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getFriends")
    public ResponseEntity<List<Friends>> getFriends() {
        try{
            return ResponseEntity.ok(friendsService.getAll());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @DeleteMapping("/removeFriend/{friendId}")
    public ResponseEntity<String> removeFriend(@PathVariable int friendId) {
        try {
            friendsService.deleteById(friendId);
            return ResponseEntity.ok("Successful Delete Friend");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/acceptFriend/{friendId}")
    public ResponseEntity<String> acceptFriend(@PathVariable int friendId) {
        try {
            friendsService.acceptFriend(friendId);
            return ResponseEntity.ok("Successful Accept Friend");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        try {
            return ResponseEntity.ok(messageService.getAllMessages());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.emptyList());
        }
    }

    @PostMapping("/sendMessage")
    public ResponseEntity<List<Message>> addMessage(int userSendMessage, int userReceiveMessage, String content, MultipartFile image) {
        try {
            Optional<Message> messageWithMaxId = messageService.getAllMessages().stream()
                    .max(Comparator.comparingInt(Message::getId));
            int id;
            id = messageWithMaxId.map(message -> message.getId() + 1).orElse(1);
            Message message = new Message();
            message.setUserSendMessage(userService.getUserById(userSendMessage).orElse(null));
            message.setUserReceiveMessage(userService.getUserById(userReceiveMessage).orElse(null));
            if(image == null) {
                message.setContent(content);
            } else {
                String uploadDir = "static/images/message/" + id + "/";

                String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                // Tạo tên file duy nhất bằng UUID và giữ lại tên gốc của file

                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath); // Tạo thư mục nếu chưa tồn tại
                }

                Path filePathAvatar = uploadPath.resolve(fileName);
                Files.copy(image.getInputStream(), filePathAvatar, StandardCopyOption.REPLACE_EXISTING);
                // Lưu URL vào DB
                String Ipv4 = getIpv4();
                String avatar = Ipv4 + "/images/message/" + id + "/" + fileName;
                message.setContent(avatar);
            }
            messageService.save(message);
            return ResponseEntity.ok(
                    messageService.getAllMessages()
                            .stream()
                            .filter(p -> (p.getUserReceiveMessage().getUserId() == userReceiveMessage && p.getUserSendMessage().getUserId() == userSendMessage)
                                    || (p.getUserReceiveMessage().getUserId() == userSendMessage && p.getUserSendMessage().getUserId() == userReceiveMessage))
                            .collect(Collectors.toList())  // Collect matching messages into a list
            );
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/verify-token")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String token) {
        try {
            // Loại bỏ "Bearer " khỏi token nếu có
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // Kiểm tra token có hợp lệ không
            boolean isValid = jwtTokenProvider.validateToken(token);

            if (isValid) {
                // Token hợp lệ, trả về trạng thái OK
                return ResponseEntity.ok("Token is valid");
            } else {
                // Token không hợp lệ
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @GetMapping("/getUserByToken/{token}")
    public ResponseEntity<User> getUserByToken(@RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(userService.getUserByUsername(jwtTokenProvider.getUsernameFromToken(token)));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}