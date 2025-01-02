package com.example.demo.services;

import com.example.demo.models.Notification;
import com.example.demo.models.Post;
import com.example.demo.models.User;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    NotificationService notificationService;
    @Autowired
    NotificationCateService notificationCateService;

    public Post getPostById(int id) {
        return postRepository.findById(id).orElse(null);
    }

    public List<Post> getAllPosts(){
        return postRepository.findAll();
    }

    public void savePost(Post post){
        postRepository.save(post);
    }

    public void deletePostById(int id){
        postRepository.deleteById(id);
    }
    @Transactional
    public Post toggleLike(int postId, int userId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Kiểm tra nếu người dùng đã "like" bài post, thì "unlike"
        if (post.getUsers().contains(user)) {
            post.getUsers().remove(user);  // Xóa người dùng khỏi danh sách "likes"
            post.setReactionQuantity(post.getReactionQuantity() - 1);  // Giảm số lượng "like"
        } else {
            post.getUsers().add(user);// Thêm người dùng vào danh sách "likes"
            post.setReactionQuantity(post.getReactionQuantity() + 1);  // Tăng số lượng "like"
            Notification notification = new Notification();
            notification.setUserIdSend(userRepository.findById(userId).orElse(null));
            Post posts = postRepository.findById(postId).orElse(null);
            notification.setUserIdReceive(userRepository.findById(posts.getUserUpLoad().getUserId()).orElse(null));
            notification.setNotificationCategory(notificationCateService.getById(1)); // Like : 1
            notificationService.save(notification);
        }
        // Lưu lại thay đổi vào database
        postRepository.save(post);
        return post;
    }
//
//    public PostDTO convertPostToDTO(Post post) {
//        Set<UserDTO> userDTOs = post.getUsers().stream()
//                .map(user -> new UserDTO(
//                        user.getUserId(),
//                        user.getFullName(),
//                        user.getUsername(),
//                        user.getEmail(),
//                        user.getPhoneNumber(),
//                        user.getImage(),
//                        user.isStatus(),
//                        null // Bạn có thể bỏ trống Posts nếu không cần thông tin này
//                ))
//                .collect(Collectors.toSet());
//
//        return new PostDTO(
//                post.getPostId(),
//                post.getUserUpLoad(),
//                post.getPostImage(),
//                post.getCaption(),
//                post.getReactionQuantity(),
//                post.getPostedTime(),
//                userDTOs
//        );
//    }
}
