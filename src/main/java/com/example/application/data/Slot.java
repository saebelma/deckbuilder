package com.example.application.data;

import lombok.Data;

@Data
public class Slot {

    private Card card;
    private int numberOfCopies;

    public Slot(Card card, int numberOfCopies) {
        this.card = card;
        this.numberOfCopies = numberOfCopies;
    }

    public void addCopy() {
        numberOfCopies++;
    }

    public void subtractCopy() {
        if (numberOfCopies > 0) numberOfCopies--;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Slot)) return false;
        Slot s = (Slot) o;
        if (s.getCard().getCardName() == card.getCardName())
            return true;
        else
            return false;
    }
}
