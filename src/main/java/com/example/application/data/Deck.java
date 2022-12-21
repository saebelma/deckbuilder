package com.example.application.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class Deck {

	private String name;
	private List<Slot> mainboard = new ArrayList<>();
	private List<Slot> sideboard = new ArrayList<>();

	public void addSlotToMainboard(Card card) {
		Slot newSlot = new Slot(card, 1);
		if (!mainboard.contains(newSlot))
			mainboard.add(newSlot);
	}

	public void deleteSlotFromMainboard(Slot slot) {
		mainboard.remove(slot);
	}

	public void addSlotToSideboard(Card card) {
		Slot newSlot = new Slot(card, 1);
		if (!sideboard.contains(newSlot))
			sideboard.add(newSlot);
	}

	public void deleteSlotFromSideboard(Slot slot) {
		sideboard.remove(slot);
	}

	public void deleteAllSlots() {
		mainboard.clear();
		sideboard.clear();
	}

	public String getText() {
		String text = "Mainboard\n";
		text += getMainboard().stream().map(slot -> slot.getNumberOfCopies() + " " + slot.getCard().getCardName())
				.collect(Collectors.joining("\n"));
		text += "\n\nSideboard\n";
		text += getSideboard().stream().map(slot -> slot.getNumberOfCopies() + " " + slot.getCard().getCardName())
				.collect(Collectors.joining("\n"));
		return text;
	}
}
