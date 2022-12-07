package com.example.application.views.helloworld;

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

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.vaadin.olli.FileDownloadWrapper;

import com.example.application.data.*;

@PageTitle("Swifty Deckbuilder")
@Route(value = "")
public class DeckBuilderView extends VerticalLayout {
    Grid<Card> searchResults = new Grid<>(Card.class);
    Image cardImage = new Image();
    Deck deck = new Deck();
    Grid<Slot> deckView = new Grid<>(Slot.class, false);

    public DeckBuilderView() {

        // Generate Scryfall search bar
        TextField textField = new TextField("Scryfall Search");
        Button searchButton = new Button("Go");
        searchButton.addClickListener(
                click -> searchScryfall(textField.getValue()));
        HorizontalLayout searchBar = new HorizontalLayout(textField,
                searchButton);
        searchBar.setDefaultVerticalComponentAlignment(Alignment.BASELINE);

        // Generate grid for displaying search results
        searchResults.setColumns("cardName", "manaCost", "type");
        searchResults.addColumn(new ComponentRenderer<>(
                card -> new Button("Add to Deck", event -> {
                    deck.addCard(card);
                    updateDeckView();
                })));
        searchResults.asSingleSelect()
                .addValueChangeListener(event -> showCard(event.getValue()));
        searchResults.setHeight("400px");

        cardImage.setHeight("400px");
        HorizontalLayout searchResultsView = new HorizontalLayout(searchResults,
                cardImage);
        searchResultsView
                .setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        searchResultsView.setWidthFull();

        // Assemble and add card search view
        VerticalLayout cardSearchView = new VerticalLayout(searchBar,
                searchResultsView);
        add(cardSearchView);

        // Generate grid for displaying and editing decklist
        deckView.addColumn(slot -> slot.getCard().getCardName())
                .setHeader("Card Name");
        deckView.addColumn(slot -> slot.getCard().getManaCost())
                .setHeader("Mana Cost");
        deckView.addColumn(slot -> slot.getCard().getType()).setHeader("Type");
        deckView.addColumn("numberOfCopies");
        deckView.addColumn(
                new ComponentRenderer<>(slot -> new Button("+", event -> {
                    slot.addCopy();
                    updateDeckView();
                })));
        deckView.addColumn(
                new ComponentRenderer<>(slot -> new Button("-", event -> {
                    slot.subtractCopy();
                    updateDeckView();
                })));
        deckView.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) showCard(event.getValue().getCard());
        });
        updateDeckView();

        add(deckView);

        Button button = new Button("Download Decklist");
        FileDownloadWrapper buttonWrapper = new FileDownloadWrapper(
                new StreamResource("decklist.txt",
                        () -> new ByteArrayInputStream(deck.getDecklist()
                                .stream()
                                .map(slot -> slot.getNumberOfCopies() + " "
                                        + slot.getCard().getCardName())
                                .collect(Collectors.joining("\n"))
                                .getBytes())));
        buttonWrapper.wrapComponent(button);
        add(buttonWrapper);

    }

    private void showCard(Card value) {
        if (value != null) cardImage.setSrc(value.getImageURL());
    }

    private void updateDeckView() {
        deckView.setItems(deck.getDecklist());
    }

    private void searchScryfall(String value) {
        List<Card> cardList = new ArrayList<>();

        try {
            // Create rest template
            RestTemplate rest = new RestTemplate();

            // Get response from scryfall
            String jsonString = rest.getForObject(
                    "https://api.scryfall.com/cards/search?q=" + value,
                    String.class);

            // Parse list of objects
            JacksonJsonParser parser = new JacksonJsonParser();
            Map<String, Object> map = parser.parseMap(jsonString);
            List<Object> cards = (List<Object>) map.get("data");

            // Parse card names
            for (Object card : cards) {
                Map<String, Object> cardProperties = (Map<String, Object>) card;
                Card newCard = new Card();
                newCard.setCardName((String) cardProperties.get("name"));
                newCard.setImageURL((String) cardProperties.get("uri")
                        + "?format=image&version=normal");
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
