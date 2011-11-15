import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

    @Before
    public void setup() {
        Fixtures.deleteAll();
    }

    @Test
    public void createAndRetrieveUser() {
        new User("golettoo@gmail.com", "123456", "Jeffrey Hu").save();
        User jeffrey = User.find("byEmail", "golettoo@gmail.com").first();

        assertNotNull(jeffrey);
        assertEquals("Jeffrey Hu", jeffrey.fullname);
    }

    @Test
    public void tryConnectAsUser() {
        new User("golettoo@gmail.com", "123456", "Jeffrey Hu").save();
        
        assertNotNull(User.connect("golettoo@gmail.com", "123456"));
        assertNull(User.connect("golettoo@gmail.com", "1231325123456"));
        assertNull(User.connect("goleo@gmail.com", "123456"));
    }

    @Test
    public void createPost() {
        User jeffrey = new User("golettoo@gmail.com", "123456", "Jeffrey Hu").save();
        new Post(jeffrey, "My first post", "Hello world").save();
        assertEquals(1, Post.count());

        List<Post> posts = Post.find("byAuthor", jeffrey).fetch();

        // Tests
        assertEquals(1, posts.size());
        Post firstPost = posts.get(0);
        assertNotNull(firstPost);
        assertEquals(jeffrey, firstPost.author);
        assertEquals("My first post", firstPost.title);
        assertEquals("Hello world", firstPost.content);
        assertNotNull(firstPost.postedAt);
    }

    @Test
    public void postComments() {
        // Create a new user and save it 
        User jeffrey = new User("golettoo@gmail.com", "123456", "Jeffrey Hu").save();

        // Create a new post
        Post post = new Post(jeffrey, "My first post", "Hello world").save();

        // Post 2 comments
        new Comment(post, "jeff", "Nice post").save();
        new Comment(post, "Tom", "haha").save();

        // Retrieve all comments
        List<Comment> comments = Comment.find("byPost", post).fetch();

        // Tests
        assertEquals(2, comments.size());

        Comment firstComment = comments.get(0);
        assertNotNull(firstComment);
        assertEquals("jeff", firstComment.author);
        assertEquals("Nice post", firstComment.content);
        assertNotNull(firstComment.postedAt);

        Comment secondComment = comments.get(1);
        assertNotNull(secondComment);
        assertEquals("Tom", secondComment.author);
        assertEquals("haha", secondComment.content);
        assertNotNull(secondComment.postedAt);
    }

    @Test
    public void useTheCommentsRelation() {
        // Create a new user and save it
        User jeffrey = new User("golettoo@gmail.com", "123456", "Jeffrey Hu").save();

        // Create a new post
        Post post = new Post(jeffrey, "My first post", "Hello world").save();

        // Post 2 comments
        post.addComment("jeff", "Nice post").save();
        post.addComment("Tom", "haha").save();

        // Count things
        assertEquals(1, User.count());
        assertEquals(1, Post.count());
        assertEquals(2, Comment.count());

        // Retrieve Jeffrey post
        post = Post.find("byAuthor", jeffrey).first();
        System.out.println(post.comments);
        assertNotNull(post);

        // Navigate to comments
        assertEquals(2, post.comments.size());
        assertEquals("Tom", post.comments.get(1).author);

        // Delete the post
        post.delete();

        assertEquals(1, User.count());
        assertEquals(0, Post.count());
        assertEquals(0, Comment.count());
    }

    @Test
public void fullTest() {
    Fixtures.loadModels("data.yml");
 
    // Count things
    assertEquals(2, User.count());
    assertEquals(3, Post.count());
    assertEquals(3, Comment.count());
 
    // Try to connect as users
    assertNotNull(User.connect("bob@gmail.com", "secret"));
    assertNotNull(User.connect("jeff@gmail.com", "secret"));
    assertNull(User.connect("jeff@gmail.com", "badpassword"));
    assertNull(User.connect("tom@gmail.com", "secret"));
 
    // Find all of Bob's posts
    List<Post> bobPosts = Post.find("author.email", "bob@gmail.com").fetch();
    assertEquals(2, bobPosts.size());
 
    // Find all comments related to Bob's posts
    List<Comment> bobComments = Comment.find("post.author.email", "bob@gmail.com").fetch();
    assertEquals(3, bobComments.size());
 
    // Find the most recent post
    Post frontPost = Post.find("order by postedAt desc").first();
    assertNotNull(frontPost);
    assertEquals("About the model layer", frontPost.title);
 
    // Check that this post has two comments
    assertEquals(2, frontPost.comments.size());
 
    // Post a new comment
    frontPost.addComment("Jim", "Hello guys");
    assertEquals(3, frontPost.comments.size());
    assertEquals(4, Comment.count());
}

}
