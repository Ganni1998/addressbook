package com.vaadin.tutorial.addressbook;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tutorial.addressbook.backend.Contact;
import com.vaadin.tutorial.addressbook.backend.ContactService;
import com.vaadin.ui.*;

import javax.servlet.annotation.WebServlet;
import java.util.Arrays;

/* The web user interface.
 * Define the user interface by extending the UI class.  New instance of this class
 * is automatically created for every user accessing the application. This can also be a managed bean (CDI or Spring).
 *
 */



// HTML title for the application
@Title("Addressbook")
// Use the vaadin.com/valo theme
@Theme("valo")
public class AddressbookUI extends UI {



	/* Hundreds of widgets and components.
	 * The default Vaadin components are in com.vaadin.ui package and
	 * there is over 500 more at vaadin.com/directory.
	 * Note that the class variables are user session scoped.
	 */
	private TextField filter = new TextField();

	private Button newContact = new Button("New contact");

	private Table contactList = new Table();

	// ContactForm is an example of a custom component class
	private ContactForm contactForm = new ContactForm(this);

	// ContactService mimics a real world DAO, that you'd typically implement as
	// EJB or Spring Data based service.
	private ContactService service = ContactService.createDemoService();


	/* The "main method".
	 * This is the entry point method executed to initialize and configure
	 * the visible user interface. Executed on every browser reload.
	 */
	@Override
	protected void init(VaadinRequest request) {

		// If you need to configure the components, the init
		// method is a good place to do that.
		filter.setInputPrompt("Filter contacts...");

		contactList.setSelectable(true);



		/* Event-based programming.
		 * With Vaadin you receive user interaction events and send your own events as needed.
		 * Here we attach listeners for components for click event, selection and filtering.
		 */
		newContact.addClickListener((Button.ClickEvent e) -> editContact(new Contact()));

		filter.addTextChangeListener((TextChangeEvent e) -> listContacts(e.getText()));

		contactList.addValueChangeListener((Property.ValueChangeEvent e)
						-> 	editContact((Contact) e.getProperty().getValue()));



		/* Building the layout.
		 * Layouts are components that contain other components.
		 * Nest HorizontalLayout for filter and wrap them together
		 * contactList to VerticalLayout on the left side of the screen.
		 * Allow user to resize the components with a SplitPanel.
		 *
		 * In addition to Java, you may also choose Vaadin Designer,
		 * CSS and HTML templates or declarative format for
		 * creating your layouts.
		 */
		HorizontalLayout actions = new HorizontalLayout(filter, newContact);
		actions.setWidth("100%");
		filter.setWidth("100%");
		actions.setExpandRatio(filter, 1);

		VerticalLayout left = new VerticalLayout(actions, contactList);
		left.setSizeFull();
		contactList.setSizeFull();
		left.setExpandRatio(contactList, 1);

		// Split to allow resizing
		setContent(new HorizontalSplitPanel(left, contactForm));

		// List initial content from the back-end data source
		listContacts();
	}

	/* Embrace clean code.
	 * It is good practice to have separate data access methods that
	 * handle the back-end access and/or the user interface updates.
	 * Further split your code into classes to easier maintenance.
	 *
	 */
	private void listContacts() {
		listContacts(filter.getValue());
	}

	private void listContacts(String text) {
		contactList.setContainerDataSource(new BeanItemContainer<>(
				Contact.class, service.findAll(text)), Arrays.asList(
				"firstName", "lastName", "email"));
		contactList.setColumnHeaders("First name", "Last name", "email");
		contactForm.setVisible(false);
	}

	private void editContact(Contact contact) {
		if (contact != null) {
			contactForm.edit(contact);
		} else {
			contactForm.setVisible(false);
		}
	}

	/*
	 * The save() and deselect() methods are called by custom ContactForm when user wants to
	 * persist or reset changes to the edited contact.
	 */
	public void save(Contact contact) {
		service.save(contact);
		listContacts();
	}

	public void deselect() {
		contactList.setValue(null);
	}

	/*  Simple servlet configuration.
	 *
	 *  You can specify additional servlet parameters like the URI and UI
	 *  class name and turn on production mode when you have finished developing the application.
	 *
	 */
	@WebServlet(urlPatterns = "/*")
	@VaadinServletConfiguration(ui = AddressbookUI.class, productionMode = false)
	public static class MyUIServlet extends VaadinServlet {
	}


}
