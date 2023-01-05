package com.example.application.views;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.vaadin.olli.FileDownloadWrapper;

import com.example.application.data.Card;
import com.example.application.data.Decklist;
import com.example.application.data.service.DecklistService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

@PageTitle("Swifty Deckbuilder")
@Route(value = "")
public class DeckBuilderView extends VerticalLayout {
	Grid<Card> searchResults = new Grid<>(Card.class);
	Image cardImage = new Image();
	Decklist decklist = new Decklist();
	Grid<Card> mainboardView = new Grid<>(Card.class, false);
	Grid<Card> sideboardView = new Grid<>(Card.class, false);
	DecklistService decklistService;

	public DeckBuilderView(DecklistService decklistService) {
		this.decklistService = decklistService;

		// Generate search bar and results view
		addCardSearchView();

		// Generate grid for displaying and editing mainboard
		addMainBoardView();

		// Generate grid for displaying and editing sideboard
		addSideboardView();

		// Add download button
		addDownloadButton();

		// Add load and save buttons
		addLoadDecklistButton();
		addSaveDecklistButton();

	}

	private void addCardSearchView() {
		HorizontalLayout searchBar = getSearchBar();
		HorizontalLayout searchResultsView = getSearchResultsView();
		add(new VerticalLayout(searchBar, searchResultsView));
	}

	private void addDownloadButton() {
		Button button = new Button("Download Decklist");
		FileDownloadWrapper buttonWrapper = new FileDownloadWrapper(
				new StreamResource("decklist.txt", () -> new ByteArrayInputStream(decklist.getText().getBytes())));
		buttonWrapper.wrapComponent(button);
		add(buttonWrapper);
	}

	private void addLoadDecklistButton() {
		Button button = new Button("Load Decklist");
		button.addClickListener(click -> loadDecklist());
		add(button);
	}

	private void loadDecklist() {
		decklist.setMainboard(decklistService.loadMainboard());
		updateMainboardView();
		decklist.setSideboard(decklistService.loadSideboard());
		updateSideboardView();
	}

	private void addSaveDecklistButton() {
		Button button = new Button("Save Decklist");
		button.addClickListener(click -> saveDecklist());
		add(button);
	}

	private void saveDecklist() {
		decklistService.saveMainboard(decklist.getMainboard());
		decklistService.saveSideboard(decklist.getSideboard());
	}

	private HorizontalLayout getSearchResultsView() {
		searchResults.setColumns("cardName", "manaCost", "type");
		searchResults.addColumn(new ComponentRenderer<>(card -> new Button("Add to Mainboard", event -> {
			decklist.addCardToMainboard(card);
			updateMainboardView();
		})));
		searchResults.addColumn(new ComponentRenderer<>(card -> new Button("Add to Sideboard", event -> {
			decklist.addCardToSideboard(card);
			updateSideboardView();
		})));
		searchResults.asSingleSelect().addValueChangeListener(event -> showCard(event.getValue()));
		searchResults.setHeight("400px");

		cardImage.setHeight("400px");
		if (cardImage.getSrc() == "")
			cardImage.setSrc("images/Magic_card_back.webp");
		HorizontalLayout searchResultsView = new HorizontalLayout(searchResults, cardImage);
		searchResultsView.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
		searchResultsView.setWidthFull();
		return searchResultsView;
	}

	private HorizontalLayout getSearchBar() {
		TextField textField = new TextField("Scryfall Search");
		Button searchButton = new Button("Go");
		searchButton.addClickListener(click -> searchScryfall(textField.getValue()));
		searchButton.addClickShortcut(Key.ENTER);
		HorizontalLayout searchBar = new HorizontalLayout(textField, searchButton);
		searchBar.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
		return searchBar;
	}

	private void addSideboardView() {
		sideboardView.addColumn(card -> card.getCardName()).setHeader("Card Name");
		sideboardView.addColumn(card -> card.getManaCost()).setHeader("Mana Cost");
		sideboardView.addColumn(card -> card.getType()).setHeader("Type");
		sideboardView.addColumn(card -> card.getNumberOfCopies()).setHeader("Number of Copies");
		sideboardView.addColumn(new ComponentRenderer<>(card -> new Button("+", event -> {
			card.addCopy();
			updateSideboardView();
		})));
		sideboardView.addColumn(new ComponentRenderer<>(card -> new Button("-", event -> {
			card.subtractCopy();
			updateSideboardView();
		})));
		sideboardView.addColumn(new ComponentRenderer<>(card -> new Button("Remove", event -> {
			decklist.deleteCardFromSideboard(card);
			updateSideboardView();
		})));
		sideboardView.asSingleSelect().addValueChangeListener(event -> {
			if (event.getValue() != null)
				showCard(event.getValue());
		});
		updateSideboardView();
		add(sideboardView);
	}

	private void addMainBoardView() {
		mainboardView.addColumn(card -> card.getCardName()).setHeader("Card Name");
		mainboardView.addColumn(card -> card.getManaCost()).setHeader("Mana Cost");
		mainboardView.addColumn(card -> card.getType()).setHeader("Type");
		mainboardView.addColumn(card -> card.getNumberOfCopies()).setHeader("Number of Copies");
		mainboardView.addColumn(new ComponentRenderer<>(card -> new Button("+", event -> {
			card.addCopy();
			updateMainboardView();
		})));
		mainboardView.addColumn(new ComponentRenderer<>(card -> new Button("-", event -> {
			card.subtractCopy();
			updateMainboardView();
		})));
		mainboardView.addColumn(new ComponentRenderer<>(card -> new Button("Remove", event -> {
			decklist.deleteCardFromMainboard(card);
			updateMainboardView();
		})));
		mainboardView.asSingleSelect().addValueChangeListener(event -> {
			if (event.getValue() != null)
				showCard(event.getValue());
		});
		updateMainboardView();
		add(mainboardView);
	}

	private void showCard(Card card) {
		if (card != null)
			cardImage.setSrc(card.getImageURL());
	}

	private void updateMainboardView() {
		mainboardView.setItems(decklist.getMainboard());
	}

	private void updateSideboardView() {
		sideboardView.setItems(decklist.getSideboard());
	}

	private void searchScryfall(String value) {
		List<Card> cardList = new ArrayList<>();

		try {
			// Create rest template
			RestTemplate rest = new RestTemplate();

			// Get response from scryfall
			String jsonString = rest.getForObject("https://api.scryfall.com/cards/search?q=" + value, String.class);

			// Parse list of objects
			JacksonJsonParser parser = new JacksonJsonParser();
			Map<String, Object> map = parser.parseMap(jsonString);
			List<Object> cards = (List<Object>) map.get("data");

			// Parse card names
			for (Object card : cards) {
				Map<String, Object> cardProperties = (Map<String, Object>) card;
				Card newCard = new Card();
				newCard.setCardName((String) cardProperties.get("name"));
				newCard.setImageURL((String) cardProperties.get("uri") + "?format=image&version=normal");
				newCard.setManaCost((String) cardProperties.get("mana_cost"));
				newCard.setType((String) cardProperties.get("type_line"));
				cardList.add(newCard);
			}
		} catch (RestClientException e) {
			e.printStackTrace();
		}

		searchResults.setItems(cardList);
	}
}
