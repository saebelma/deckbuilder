package com.example.application.data;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Deck {

    private String name;
    private List<Slot> decklist = new ArrayList<>();

    public void addCard(Card card) {
        Slot newSlot = new Slot(card, 1);
        if (!decklist.contains(newSlot)) decklist.add(newSlot);
    }

    public void deleteSlot(Slot slot) {
        decklist.remove(slot);
    }
}
