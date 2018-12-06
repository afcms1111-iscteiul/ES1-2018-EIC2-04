package gui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.sun.javafx.scene.control.skin.ListViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;

import email.EmailConnection;
import entry_objects.EmailEntry;
import entry_objects.FacebookEntry;
import entry_objects.InformationEntry;
import entry_objects.TwitterEntry;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import other.Filter;
import other.Service;
import threads.ThreadPool;
import twitter.TwitterConnection;

/**
 * The Class MainController handles the user interaction with the GUI.
 * 
 * @author Rostislav Andreev
 * @version 2.0
 */
public class MainController implements Initializable {

	/** The Constant INSTANCE. */
	private static final MainController INSTANCE = new MainController();

	// ------------ Main window ------------
	/** The main box. */
	@FXML
	private HBox mainBox;

	/** The center pane. */
	@FXML
	private StackPane centerPane;

	// ------------ Side bar ------------
	/** The side menu. */
	@FXML
	private ToggleGroup sideMenu;

	/** The username. */
	@FXML
	private Label username;

	// ------------ Posts list ------------
	/** The posts side bar. */
	@FXML
	private VBox postsSideBar;

	/** The search bar. */
	@FXML
	private JFXTextField searchBar;

	@FXML
	private Hyperlink leaveSearch;

	@FXML
	private JFXListView<TwitterAccountBox> twitterAccountsFilter;

	@FXML
	private ChoiceBox<String> dateFilter;

	@FXML
	private JFXCheckBox emailFilter;

	@FXML
	private JFXCheckBox facebookFilter;

	@FXML
	private JFXCheckBox twitterFilter;

	@FXML
	private JFXButton removeFilter;

	@FXML
	private TitledPane emailFilterConfigurations;

	@FXML
	private TitledPane twitterFilterConfigurations;

	/** The posts. */
	@FXML
	private JFXListView<PostBox> posts;

	// ------------ Open post ------------
	/** The post layer. */
	@FXML
	private StackPane postLayer;

	/** The post author container. */
	@FXML
	private VBox postContainer;

	@FXML
	private ScrollPane postScrollPane;

	/** The post content. */
	@FXML
	private VBox postContent;

	@FXML
	private Text postText;

	/** The post author info. */
	@FXML
	private VBox postAuthorInfo;

	/** The author name. */
	@FXML
	private Label authorName;

	/** The author username. */
	@FXML
	private Label authorUsername;

	/** The retweet label. */
	@FXML
	private Label retweetLabel;

	/** The profile pic. */
	@FXML
	private ImageView profilePic;

	/** The post footer. */
	@FXML
	private StackPane postFooter;

	/** The email footer. */
	@FXML
	private HBox emailFooter;

	@FXML
	private HBox facebookFooter;

	/** The twitter footer. */
	@FXML
	private HBox twitterFooter;

	@FXML
	private JFXButton retweetButton;

	@FXML
	private JFXButton favouriteButton;

	// ------------ Settings ------------
	/** The settings. */
	@FXML
	private ScrollPane settings;

	/** The email list. */
	@FXML
	private JFXListView<String> emailList;

	/** The theme list. */
	@FXML
	private ChoiceBox<String> themeList;

	/** The new email. */
	@FXML
	private TextField newEmail;

	@FXML
	private JFXButton twitterLoginButton;

	@FXML
	private HBox boxPIN;

	@FXML
	private JFXTextField twitterPIN;

	// ------------ Email writing panel ------------
	/** The email pane. */
	@FXML
	private VBox emailPane;

	/** The email receiver. */
	@FXML
	private JFXTextField emailReceiver;

	/** The email subject. */
	@FXML
	private JFXTextField emailSubject;

	/** The email error. */
	@FXML
	private Label emailError;

	/** The email message. */
	@FXML
	private JFXTextArea emailMessage;

	// ------------ Tweet composing panel ------------
	@FXML
	private StackPane composeTweet;

	@FXML
	private JFXTextArea tweetTextArea;

	@FXML
	private Label tweetCounter;

	@FXML
	private JFXButton tweetButton;

	/** The email connection. */
	private EmailConnection emailConnection;
	private ObservableList<PostBox> originalList;
	private InformationEntry currentlyOpened;

	/**
	 * Instantiates a new main controller.
	 */
	private MainController() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		showHomePage();
		originalList = FXCollections.observableArrayList();

		themeList.getItems().add("Laranja");
		themeList.getItems().add("Azul");
		themeList.getItems().add("Azul Invertido");
		themeList.setValue("Laranja");
		themeList.getSelectionModel().selectedItemProperty().addListener(e -> setTheme());

		dateFilter.getItems().add("Última hora");
		dateFilter.getItems().add("Hoje");
		dateFilter.getItems().add("Esta semana");
		dateFilter.getItems().add("Este mês");
		dateFilter.getItems().add("Este ano");
		dateFilter.setValue("Última hora");

		leaveSearch.setVisible(false);
		leaveSearch.setDisable(true);

		List<String> twitterAccounts = Arrays.asList(Filter.DEFAULT_TWITTER_USER_FILTERS);
		Collections.sort(twitterAccounts);

		for (String account : twitterAccounts)
			twitterAccountsFilter.getItems().add(new TwitterAccountBox(account, true));

		addLoadingBox();

		tweetTextArea.lengthProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue.intValue() <= 280 && newValue.intValue() > 280)
				tweetCounter.setTextFill(Paint.valueOf("red"));
			if (oldValue.intValue() > 280 && newValue.intValue() <= 280)
				tweetCounter.setTextFill(Paint.valueOf("black"));
		});
		tweetCounter.textProperty().bind(tweetTextArea.lengthProperty().asString());
		tweetButton.disableProperty().bind(tweetTextArea.lengthProperty().greaterThan(280));
		emailFilterConfigurations.disableProperty().bind(emailFilter.selectedProperty().not());
		twitterFilterConfigurations.disableProperty().bind(twitterFilter.selectedProperty().not());
		centerPane.prefWidthProperty().bind(mainBox.widthProperty().subtract(250));
		postScrollPane.maxHeightProperty().bind(postLayer.heightProperty().subtract(150));
	}

	private void addLoadingBox() {
		posts.getItems().clear();
		posts.setMouseTransparent(true);
		posts.setFocusTraversable(false);
		PostBox loading = new PostBox(null);
		loading.prefHeightProperty().bind(posts.heightProperty().subtract(50));
		posts.getItems().add(loading);
	}

	/**
	 * Adds posts to the posts list and displays them on screen. If reload is true
	 * then it deletes all the posts from the posts lists and displays only the new
	 * ones.
	 * 
	 * @param entries the entries
	 * @param reload  the reload
	 */
	public void loadPosts(List<InformationEntry> entries, boolean reload) {
		Platform.runLater(() -> {
			posts.setMouseTransparent(false);
			posts.setFocusTraversable(true);

			if (reload)
				posts.getItems().clear();

			for (InformationEntry entry : entries) {
				PostBox postBox = new PostBox(entry);

				postBox.prefWidthProperty().bind(posts.widthProperty().subtract(110));
				postBox.setOnMouseClicked(e -> openPost(entry));

				posts.getItems().add(postBox);
			}

			originalList.addAll(posts.getItems());
		});
	}

	/**
	 * Sets the theme.
	 */
	private void setTheme() {
		int cssIndex = themeList.getSelectionModel().getSelectedIndex();
		String css = getClass().getResource("/res/MainScene" + cssIndex + ".css").toExternalForm();
		mainBox.getStylesheets().clear();
		mainBox.getStylesheets().add(css);
	}

	/**
	 * Shows home page.
	 */
	@FXML
	private void showHomePage() {
		postsSideBar.toFront();
	}

	/**
	 * Shows the email writing panel.
	 */
	@FXML
	private void writeEmail() {
		emailPane.toFront();
	}

	/**
	 * Shows the tweet composing panel.
	 */
	@FXML
	private void composeTweet() {
		composeTweet.toFront();
	}

	/**
	 * Shows settings.
	 */
	@FXML
	private void showSettings() {
		settings.toFront();
	}

	/**
	 * Logs out and closes the program.
	 *
	 * @param event the event
	 */
	@FXML
	private void logOut(ActionEvent event) {
		ThreadPool.getInstance().stopThreads();
		((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
	}

	/**
	 * Searches posts with the given keyword.
	 */
	@FXML
	private void search() {
		if (!searchBar.getText().isEmpty()) {
			String search = searchBar.getText().toLowerCase();
			List<PostBox> results = new ArrayList<>();

			for (PostBox post : originalList)
				if (post.getService().equals(Service.EMAIL)) {
					EmailEntry email = (EmailEntry) post.getInformationEntry();
					String writer = email.getWriterName().toLowerCase();
					String subject = email.getSubject().toLowerCase();
					String content = email.getContent().toLowerCase();

					if (writer.contains(search) || subject.contains(search) || content.contains(search))
						results.add(post);
				} else if (post.getService().equals(Service.TWITTER)) {
					TwitterEntry tweet = (TwitterEntry) post.getInformationEntry();
					String name = tweet.getStatus().getUser().getName().toLowerCase();
					String username = tweet.getStatus().getUser().getScreenName().toLowerCase();
					String content = tweet.getStatus().getText().toLowerCase();

					if (name.contains(search) || username.contains(search) || content.contains(search))
						results.add(post);
				}

			if (!results.isEmpty()) {
				posts.getItems().clear();
				posts.getItems().addAll(results);

				leaveSearch.setVisible(true);
				leaveSearch.setDisable(false);
			}
		} else
			leaveSearch();
	}

	@FXML
	private void applyFilter() {
	}

	@FXML
	private void leaveSearch() {
		posts.getItems().clear();
		posts.getItems().addAll(originalList);
		System.out.println(posts.getItems().size());

		searchBar.clear();

		leaveSearch.setVisible(false);
		leaveSearch.setDisable(true);
		leaveSearch.setVisited(false);
	}

	/**
	 * Opens the post in more detail.
	 *
	 * @param informationEntry the information entry
	 */
	private void openPost(InformationEntry informationEntry) {
		currentlyOpened = informationEntry;
		postContent.getChildren().clear();
		postText.setText("");
		postContent.getChildren().add(postText);
		retweetLabel.setVisible(false);
		retweetLabel.setMaxHeight(0);

		if (informationEntry.getService().equals(Service.EMAIL)) {
			EmailEntry email = (EmailEntry) informationEntry;

			String names[] = email.getWriterName().split("<");
			profilePic.setFitWidth(0);
			profilePic.setFitHeight(0);
			profilePic.setImage(null);

			HBox.setMargin(profilePic, new Insets(0, 0, 0, 0));

			authorName.setText(names[0].trim());
			authorUsername.setText(names.length > 1 ? names[1].substring(0, names[1].length() - 1) : names[0]);

			postText.setText(email.getContent().trim());

			emailFooter.toFront();
		} else if (informationEntry.getService().equals(Service.FACEBOOK)) {
			FacebookEntry post = (FacebookEntry) informationEntry;

			Image pic = new Image(post.getProfileImageUrl(), 50, 50, true, true);

			profilePic.setFitWidth(50);
			profilePic.setFitHeight(50);
			profilePic.setImage(pic);

			HBox.setMargin(profilePic, new Insets(0, 10, 0, 0));

			authorName.setText(post.getAuthor());
			authorUsername.setText(post.getAttachmentTitle());

			postText.setText(post.getAttachmentDescription());

			String url = post.getAttachmentMedia().getImage().getSrc();
			Image image = new Image(url, 450, 0, true, true);

			postContent.getChildren().add(new ImageView(image));

			facebookFooter.toFront();
		} else if (informationEntry.getService().equals(Service.TWITTER)) {
			TwitterEntry tweet = (TwitterEntry) informationEntry;

			Image pic = new Image(tweet.getProfilePictureURL(), 50, 50, true, true);

			profilePic.setFitWidth(50);
			profilePic.setFitHeight(50);
			profilePic.setImage(pic);

			HBox.setMargin(profilePic, new Insets(0, 10, 0, 0));

			authorName.setText(tweet.getName());
			authorUsername.setText("@" + tweet.getUsername());

			if (tweet.isRetweet()) {
				retweetLabel.setText(tweet.getRetweeter() + " retweeted");
				retweetLabel.setVisible(true);
			}

			postText.setText(tweet.getContent());

			retweetButton.setStyle(
					TwitterConnection.getInstance().isRetweetedbyMe(tweet.getStatus()) ? "-fx-background-color: #34bf49"
							: "");

			if (tweet.getStatus().isFavorited())
				favouriteButton.setStyle("-fx-background-color: #34bf49");

			for (String media : tweet.getMediaURL()) {
				String url = media.split("!;!")[0], type = media.split("!;!")[1];

				if (type.equals("photo"))
					postContent.getChildren().add(new ImageView(new Image(url, 450, 0, true, true)));
				else if (type.equals("animated_gif")) {
					MediaPlayer player = new MediaPlayer(new Media(url));
					MediaView view = new MediaView(player);
					player.setAutoPlay(true);
					player.setOnEndOfMedia(() -> player.seek(Duration.ZERO));
					view.setFitWidth(450);
					view.setOnMouseClicked(e -> {
						if (player.getStatus().equals(Status.PLAYING))
							player.pause();
						else
							player.play();
					});
					postContent.getChildren().add(view);
				} else if (type.equals("video")) {
					MediaPlayer player = new MediaPlayer(new Media(url));
					MediaView view = new MediaView(player);
					player.setOnEndOfMedia(() -> {
						player.seek(Duration.ZERO);
						player.stop();
					});
					view.setFitWidth(450);
					view.setOnMouseClicked(e -> {
						if (player.getStatus().equals(Status.PLAYING))
							player.pause();
						else
							player.play();
					});
					postContent.getChildren().add(view);
				}
			}

			twitterFooter.toFront();
		}

		postLayer.toFront();
	}

	/**
	 * Opens the previous post.
	 */
	@FXML
	private void openPreviousPost() {
		if (posts.getSelectionModel().getSelectedIndex() - 1 >= 0) {
			posts.getSelectionModel().select(posts.getSelectionModel().getSelectedIndex() - 1);
			posts.scrollTo(posts.getSelectionModel().getSelectedIndex());
			openPost(posts.getSelectionModel().getSelectedItem().getInformationEntry());
		}
	}

	/**
	 * Opens the next post.
	 */
	@FXML
	private void openNextPost() {
		posts.getSelectionModel().select(posts.getSelectionModel().getSelectedIndex() + 1);

		ListViewSkin<?> ts = (ListViewSkin<?>) posts.getSkin();
		VirtualFlow<?> vf = (VirtualFlow<?>) ts.getChildren().get(0);
		int first = vf.getFirstVisibleCellWithinViewPort().getIndex();
		int last = vf.getLastVisibleCellWithinViewPort().getIndex();
		int[] view = IntStream.rangeClosed(first, last).toArray();

		if (posts.getSelectionModel().getSelectedIndex() > view[view.length - 1])
			posts.scrollTo(view[2]);

		openPost(posts.getSelectionModel().getSelectedItem().getInformationEntry());
	}

	/**
	 * Closes the currently open post.
	 */
	@FXML
	private void closePost() {
		postLayer.toBack();
	}

	@FXML
	private void commentTweet() {

	}

	@FXML
	private void retweet() {
		if (TwitterConnection.getInstance().isLoggedIn()) {
			TwitterEntry tweet = (TwitterEntry) currentlyOpened;
			if (TwitterConnection.getInstance().isRetweetedbyMe(tweet.getStatus())) {
				TwitterConnection.getInstance().deleteRetweet(tweet.getStatus());
				retweetButton.setStyle("-fx-background-color: #ff3000");
			} else {
				TwitterConnection.getInstance().retweet(tweet.getStatus());
				retweetButton.setStyle("-fx-background-color: #34bf49");
			}
		}
	}

	@FXML
	private void favouriteTweet() {

	}

	/**
	 * Closes the tweet composing panel.
	 */
	@FXML
	private void closeComposeTweet() {
		composeTweet.toBack();
	}

	/**
	 * Sends email.
	 */
	@FXML
	private void sendEmail() {
		if (!emailReceiver.getText().isEmpty() && !emailSubject.getText().isEmpty()
				&& !emailMessage.getText().isEmpty())
			emailConnection.sendEmail(emailReceiver.getText(), emailSubject.getText(), emailMessage.getText());
		else {
			FadeTransition errorFade = new FadeTransition(Duration.seconds(1), emailError);
			emailError.setText("Preencha todos os campos");

			errorFade.setFromValue(0);
			errorFade.setToValue(1);

			errorFade.play();
		}
	}

	/**
	 * Clears all fields in the email writing panel.
	 */
	@FXML
	private void clearEmail() {
		emailReceiver.clear();
		emailSubject.clear();
		emailMessage.clear();
		emailError.setText("");
	}

	/**
	 * Adds an email to the email list.
	 */
	@FXML
	private void addEmail() {
		emailList.getItems().add(newEmail.getText());
		newEmail.setText("");
	}

	/**
	 * Removes the selected email from the email list.
	 */
	@FXML
	private void removeEmail() {
		emailList.getItems().remove(emailList.getSelectionModel().getSelectedIndex());
	}

	@FXML
	private void twitterLogin() {
		if (TwitterConnection.getInstance().isLoggedIn()) {
			TwitterConnection.getInstance().logout();
			twitterLoginButton.setText("Iniciar Sessão");
		} else
			try {
				Desktop.getDesktop().browse(new URI(TwitterConnection.getInstance().getAuthUrl()));
				boxPIN.setVisible(true);
				boxPIN.setDisable(false);
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}

	}

	@FXML
	private void authenticateTwitter() {
		if (TwitterConnection.getInstance().confirmAuth(twitterPIN.getText())) {
			boxPIN.setDisable(true);
			boxPIN.setVisible(false);
			twitterLoginButton.setText("Terminar sessão: " + TwitterConnection.getInstance().getUsername());
		}
	}

	/**
	 * Consumes event.
	 * 
	 * @param e
	 */
	@FXML
	private void consumeEvent(MouseEvent e) {
		e.consume();
	}

	/**
	 * Sets the username on the sidebar.
	 * 
	 * @param username
	 */
	protected void setUsername(String username) {
		this.username.setText(username);
	}

	/**
	 * Returns the JFXListView containing all posts currently being displayed on
	 * screen.
	 * 
	 * @return
	 */
	public JFXListView<PostBox> getPosts() {
		return posts;
	}

	/**
	 * Gets the single instance of MainController.
	 *
	 * @return single instance of MainController
	 */
	public static MainController getInstance() {
		return INSTANCE;
	}
}