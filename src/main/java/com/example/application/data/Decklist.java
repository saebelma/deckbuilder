package com.example.application.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class Decklist {

	private List<Card> mainboard = new ArrayList<>();
	private List<Card> sideboard = new ArrayList<>();

	public void addCardToMainboard(Card card) {
		if (!mainboard.contains(card))
			mainboard.add(card);
	}

	public void deleteCardFromMainboard(Card Card) {
		mainboard.remove(Card);
	}

	public void addCardToSideboard(Card card) {
		if (!sideboard.contains(card))
			sideboard.add(card);
	}

	public void deleteCardFromSideboard(Card Card) {
		sideboard.remove(Card);
	}

	public void deleteAllCards() {
		mainboard.clear();
		sideboard.clear();
	}

	public String getText() {
		String text = "Mainboard\n";
		text += getMainboard().stream().map(card -> card.getNumberOfCopies() + " " + card.getCardName())
				.collect(Collectors.joining("\n"));
		text += "\n\nSideboard\n";
		text += getSideboard().stream().map(card -> card.getNumberOfCopies() + " " + card.getCardName())
				.collect(Collectors.joining("\n"));
		return text;
	}
}
