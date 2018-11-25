package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.sun.javafx.scene.control.skin.ListViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import email.EmailConnection;
import entry_objects.EmailEntry;
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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import other.Service;
import threads.ThreadPool;
import twitter4j.Status;

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
	private VBox filterMenu;

	@FXML
	private ChoiceBox<String> dateFilter;

	@FXML
	private JFXCheckBox emailFilter;

	@FXML
	private JFXCheckBox facebookFilter;

	@FXML
	private JFXCheckBox twitterFiler;

	@FXML
	private Slider filterSlider;

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

	/** The post content. */
	@FXML
	private VBox postContent;

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

	/** The twitter footer. */
	@FXML
	private HBox twitterFooter;

	// ------------ Settings ------------
	/** The settings. */
	@FXML
	private VBox settings;

	/** The email list. */
	@FXML
	private JFXListView<String> emailList;

	/** The theme list. */
	@FXML
	private ChoiceBox<String> themeList;

	/** The new email. */
	@FXML
	private TextField newEmail;

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

	/** The email connection. */
	private EmailConnection emailConnection;
	private ObservableList<PostBox> originalList;
	private boolean filterOpen = false;

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

		filterSlider.setMin(0);
		filterSlider.setMax(80);
		filterSlider.setValue(0);

		filterMenu.prefHeightProperty().bind(filterSlider.valueProperty());
		centerPane.prefWidthProperty().bind(mainBox.widthProperty().subtract(250));
		postContainer.maxHeightProperty().bind(postContent.heightProperty());
		posts.prefHeightProperty().bind(posts.heightProperty().add(150));
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
			if (reload)
				posts.getItems().clear();

			for (InformationEntry entry : entries)
				posts.getItems().add(toPostBox(entry));

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

	/**
	 * Opens the advanced search menu.
	 */
	@FXML
	private void openFilter() {
		new Thread() {
			public void run() {
				if (filterOpen)
					for (int i = 80; i > 0; i--)
						try {
							filterSlider.setValue(i);
							sleep(2);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
				else if (!filterOpen)
					for (int i = 0; i < 80; i++)
						try {
							filterSlider.setValue(i);
							sleep(2);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

				filterOpen = !filterOpen;
			}
		}.start();
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
	}

	/**
	 * Converts an InformationEntry object into a PostBox object to be displayed on
	 * screen.
	 *
	 * @param informationEntry the information entry
	 * @return the post box
	 */
	private PostBox toPostBox(InformationEntry informationEntry) {
		PostBox postBox = new PostBox(informationEntry);
		FontAwesomeIconView icon = new FontAwesomeIconView();
		VBox entryInfo = new VBox();
		HBox authorInfo = new HBox(), retweetInfo = new HBox();
		Label authorName = new Label(), authorUsername = new Label(), postInfo = new Label(), date = new Label();
		Region region = new Region();

		postInfo.setWrapText(true);
		date.setText(informationEntry.getDate().toString());
		HBox.setHgrow(region, Priority.ALWAYS);
		HBox.setHgrow(entryInfo, Priority.ALWAYS);

		authorUsername.setPadding(new Insets(0, 10, 0, 10));

		authorInfo.getChildren().addAll(authorName, authorUsername, region, date);
		authorInfo.setAlignment(Pos.BASELINE_LEFT);

		entryInfo.getChildren().addAll(authorInfo, postInfo);

		postBox.getChildren().addAll(icon, entryInfo);

		postBox.setSpacing(10);
		postBox.prefWidthProperty().bind(posts.widthProperty().subtract(110));
		postBox.setAlignment(Pos.CENTER_LEFT);

		postBox.setOnMouseClicked(e -> openPost(informationEntry));

		if (informationEntry.getService().equals(Service.EMAIL)) {
			EmailEntry email = (EmailEntry) informationEntry;

			String names[] = email.getWriterName().split("<");
			icon.setIcon(FontAwesomeIcon.ENVELOPE);
			icon.setSize("50");
			icon.setStyle("-fx-fill: #3cbffc");

			authorName.setText(names[0].trim());
			authorUsername.setText(names.length > 1 ? names[1].substring(0, names[1].length() - 1) : names[0]);

			authorUsername.setStyle("-fx-font-weight: bold");

			postInfo.setText(email.getSubject());
		} else if (informationEntry.getService().equals(Service.TWITTER)) {
			TwitterEntry tweet = (TwitterEntry) informationEntry;

			icon.setIcon(FontAwesomeIcon.TWITTER);
			icon.setSize("50");
			icon.setStyle("-fx-fill: #3cbffc");

			Status status = !tweet.isRetweet() ? tweet.getStatus() : tweet.getStatus().getRetweetedStatus();
			ImageView pic = new ImageView(new Image(status.getUser().get400x400ProfileImageURL(), 50, 50, true, true));

			authorName.setText(status.getUser().getName());
			authorUsername.setText("@" + status.getUser().getScreenName());

			authorUsername.setStyle("-fx-font-weight: bold");

			if (tweet.isRetweet()) {
				FontAwesomeIconView retweetIcon = new FontAwesomeIconView(FontAwesomeIcon.RETWEET);
				Label retweeter = new Label(tweet.getStatus().getUser().getName() + " retweeted");

				retweetIcon.setStyle("-fx-fill: #878787");

				retweeter.setStyle("-fx-text-fill: #878787");
				retweeter.setPadding(new Insets(0, 10, 0, 5));

				retweetInfo.getChildren().addAll(retweetIcon, retweeter);

				entryInfo.getChildren().add(0, retweetInfo);
			}

			postInfo.setText(status.getText());

			postBox.getChildren().add(1, pic);
		}

		return postBox;
	}

	/**
	 * Opens the post in more detail.
	 *
	 * @param informationEntry the information entry
	 */
	private void openPost(InformationEntry informationEntry) {
		Platform.runLater(() -> {
			postContent.getChildren().clear();
			postContent.autosize();
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

				Text body = new Text(email.getContent());
				body.setWrappingWidth(470);

				postContent.getChildren().add(body);

				emailFooter.toFront();
			} else if (informationEntry.getService().equals(Service.TWITTER)) {
				TwitterEntry tweet = (TwitterEntry) informationEntry;

				Status status = !tweet.isRetweet() ? tweet.getStatus() : tweet.getStatus().getRetweetedStatus();
				Image pic = new Image(status.getUser().get400x400ProfileImageURL(), 50, 50, true, true);

				profilePic.setFitWidth(50);
				profilePic.setFitHeight(50);
				profilePic.setImage(pic);

				HBox.setMargin(profilePic, new Insets(0, 10, 0, 0));

				authorName.setText(status.getUser().getName());
				authorUsername.setText("@" + status.getUser().getScreenName());

				if (tweet.isRetweet()) {
					retweetLabel.setText(tweet.getStatus().getUser().getName() + " retweeted");
					retweetLabel.setVisible(true);
				}

//			for (MediaEntity m : status.getMediaEntities())
//				postContent.getChildren().add(new ImageView(new Image(m.getMediaURLHttps(), 450, 0, true, true)));

				Text body = new Text(status.getText());
				body.setWrappingWidth(470);

				postContent.getChildren().add(body);

				twitterFooter.toFront();
			}

			postLayer.toFront();
		});
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
		int[] view = postsInView();
		if (posts.getSelectionModel().getSelectedIndex() > view[view.length - 1])
			posts.scrollTo(view[1]);
		openPost(posts.getSelectionModel().getSelectedItem().getInformationEntry());
	}

	private int[] postsInView() {
		ListViewSkin<?> ts = (ListViewSkin<?>) posts.getSkin();
		VirtualFlow<?> vf = (VirtualFlow<?>) ts.getChildren().get(0);
		int first = vf.getFirstVisibleCellWithinViewPort().getIndex();
		int last = vf.getLastVisibleCellWithinViewPort().getIndex();
		return IntStream.rangeClosed(first, last).toArray();
	}

	/**
	 * Closes the currently open post.
	 */
	@FXML
	private void closePost() {
		Platform.runLater(() -> postLayer.toBack());
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
		Platform.runLater(() -> {
			emailReceiver.clear();
			emailSubject.clear();
			emailMessage.clear();
			emailError.setText("");
		});
	}

	/**
	 * Adds an email to the email list.
	 */
	@FXML
	private void addEmail() {
		Platform.runLater(() -> {
			emailList.getItems().add(newEmail.getText());
			newEmail.setText("");
		});
	}

	/**
	 * Removes the selected email from the email list.
	 */
	@FXML
	private void removeEmail() {
		Platform.runLater(() -> emailList.getItems().remove(emailList.getSelectionModel().getSelectedIndex()));
//		emailList.getItems().remove(emailList.getSelectionModel().getSelectedIndex());
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
		Platform.runLater(() -> this.username.setText(username));
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